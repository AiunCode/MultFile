package com.mec.mfct.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 服务器线程
 */
public abstract class ServerThread implements Runnable{
    private static final int DEFAULT_POOL_CORE_SIZE = 10;
    private static final int DEFAULT_POOL_MAX_SIZE = 20;
    private static final long DEFAULT_POOL_THREAD_ALIVE_TIME = 100;
    private static final TimeUnit DEFAULT_POOL_UNIT = TimeUnit.MILLISECONDS;
    private static final int DEFAULT_QUEUE_SIZE = 20;

    private int poolCoreSize;
    private int poolMaxSize;
    private long poolThreadAliveTime;
    private TimeUnit timeUnit;
    private int queueSize;

    private static ThreadPoolExecutor threadPool;
    private ServerSocket serverSocket;
    private int port;
    private volatile boolean goon;

    public ServerThread (int port) {
        this.port = port;
        poolCoreSize = DEFAULT_POOL_CORE_SIZE;
        poolMaxSize = DEFAULT_POOL_MAX_SIZE;
        poolThreadAliveTime = DEFAULT_POOL_THREAD_ALIVE_TIME;
        timeUnit = DEFAULT_POOL_UNIT;
        queueSize = DEFAULT_QUEUE_SIZE;
    }

    public static ThreadPoolExecutor getThreadPool(){
        return threadPool;
    }

    public void startup() {
        if (port < 1000 || port > 65535) {
            return;
        }
        if (goon) {
            return;
        }
        goon = true;
        BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>(queueSize);
        threadPool = new ThreadPoolExecutor(poolCoreSize, poolMaxSize, poolThreadAliveTime, timeUnit, queue);
        try {
            serverSocket = new ServerSocket(port);
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void doSomething(Socket socket);

    @Override
    public void run() {
        while(goon) {
            try {
                Socket socket = serverSocket.accept();
                threadPool.execute(new Executor(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stop();
    }

    class Executor implements Runnable{
        private Socket socket;

        Executor (Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            doSomething(socket);
        }
    }

    public void stop() {
        goon = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopAll() {
        stop();
        threadPool.shutdown();
    }

}
