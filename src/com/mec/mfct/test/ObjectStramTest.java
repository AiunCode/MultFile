package com.mec.mfct.test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.mec.mfct.file.Resource;

public class ObjectStramTest {

    public static void main(String[] args) {
        new Thread(new receiver()).start();
        new Thread(new sender()).start();
    }

    static class sender implements Runnable {

        @Override
        public void run() {
            try {
                Socket socket = new Socket("127.0.0.1", 54188);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                Resource resource = new Resource();
                resource.scanResource("D:\\ProgrammeStudy\\temp");
                oos.writeObject(resource);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class receiver implements Runnable {

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(54188);
                Socket socket = serverSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                System.out.println(ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
