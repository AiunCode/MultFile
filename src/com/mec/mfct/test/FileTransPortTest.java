package com.mec.mfct.test;

import com.mec.mec_rmi.core.Node;
import com.mec.mfct.file.Receiver;
import com.mec.mfct.file.Resource;
import com.mec.mfct.file.Sender;
import com.mec.mfct.file.SenderTask;
import com.mec.mfct.mfctServer.DefaultFileDistributeStrategy;
import com.mec.mfct.progressBar.ProgressBar;

import javax.swing.*;
import java.awt.*;

public class FileTransPortTest {

    public static void main(String[] args) {
        Resource resource = new Resource();
        resource.scanResource("D:\\ProgrammeStudy\\temp");

        Resource receiveSrc = new Resource();
        receiveSrc.scanResource("D:\\ProgrammeStudy\\temp");
        receiveSrc.setAbsoultePath("D:\\ProgrammeStudy\\copyFIle");

        JFrame frame = new JFrame();
        frame.setSize(900, 800);
        frame.setLocationRelativeTo(null);
        frame.setBackground(Color.CYAN);

        ProgressBar progressBar = new ProgressBar();
        progressBar.addProgressBar(frame);
        progressBar.setResource(receiveSrc);
        frame.setVisible(true);

        SenderTask[] tasks = new DefaultFileDistributeStrategy().fileDistribute(resource, 1);
        Receiver receiver = new Receiver(1, receiveSrc);
        receiver.setPort(54190);
        receiver.setProgressBar(progressBar);
        try {
            receiver.startup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        tasks[0].addNode(new Node(54190, "127.0.0.1"));
        Sender sender = new Sender();
        sender.setSenderTask(tasks[0]);
        sender.setResource(resource);
        sender.startup();

    }

}
