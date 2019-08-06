package com.mec.mfct.file;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 文件发送类
 */
public class Sender implements Runnable{
    private Socket socket;
    private DataOutputStream dos;
    private volatile boolean goon;

    private Resource resource;
    private SenderTask st;

    public Sender () {
    }

    public Sender (Resource resource, SenderTask st) {
        setResource(resource);
        setSenderTask(st);
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setSenderTask(SenderTask st) {
        this.st = st;
    }

    public void startup() {
        String receiverIp = st.getReceiverIp();
        int receiverPort = st.getReceiverPort();

        goon = true;
        try {
            socket = new Socket(receiverIp, receiverPort);
            dos = new DataOutputStream(socket.getOutputStream());
            new Thread(this, "Sender").start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        goon = false;
        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int num = 0;
        while (num++ < st.taskSize() + 1) {
            try {
                MataData data = st.next();
                if (data == null) {
                    data = new MataData(-1,-1,-1);//文件发送完的结束标志
                    dos.write(data.getBytes());
                    break;
                }
                dos.write(data.getBytes()); //发送文件头信息

                int fileId = data.getFileId();
                long offset = data.getOffset();
                int len = data.getLength();

                byte[] sections = resource.getFileSet().sentOneFileSection(fileId, offset, len);
                dos.write(sections); //发送文件信息
            } catch (IOException e) {
                stop();
                if (goon == true) {
                    e.printStackTrace();
                }
            }
        }
        stop();
    }
}
