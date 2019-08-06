package com.mec.mfct.test;

import com.mec.mfct.file.Resource;
import com.mec.mfct.file.ResourceFile;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

public class Test {

    public static void main(String[] args) {
//        Resource resource = new Resource();
//        resource.scanResource("D:\\ProgrammeStudy\\copy");
//
//        resource.checkAllFileExist();
//        System.out.println(resource.isTransFinished());
//        Iterator<ResourceFile> files = resource.getFiles();
//        while (files.hasNext()) {
//            ResourceFile resourceFile = files.next();
//            System.out.println(resourceFile.getLength());
//        }
        try {
            ServerSocket serverSocket = new ServerSocket(54188);
            Socket socket = serverSocket.accept();
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String message = dis.readUTF();
            System.out.println(socket.getInetAddress().getHostAddress() + "说：" + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
