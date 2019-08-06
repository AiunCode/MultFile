package com.mec.mfct.util;

import com.mec.mfct.file.Receiver;

import java.io.IOException;
import java.net.Socket;
/**
 * 作为客户端建立连接
 */
public abstract class ClientThread implements Runnable{
    private Socket socket;
    private String ip;
    private int port;

    public ClientThread(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * 作为客户端建立连接
     */
    public void startup() {
        if(port < 1000 || port > 65535) {
            System.out.println("port有问题！！！");
            return;
        }
        if (ip == null) {
            System.out.println("ip没有设置！！！");
            return;
        }

        new Thread(this).start();
    }

    /**
     * 主要做一些将资源信息发送给对端，然后从对端读取参与发送者的个数
     * 并且启动文件接收线程
     * @param socket
     */
    public abstract void doSomething(Socket socket);

    @Override
    public void run() {
        try {
            System.out.println("开始建立连接!!!");
            socket = new Socket(ip, port);
            doSomething(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stop();
    }

    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
