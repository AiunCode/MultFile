package com.mec.mfct.test;

import com.mec.mfct.appclient.AppClient;
import com.mec.mfct.progressBar.ProgressBar;

import javax.swing.*;

public class AppClientTest {

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        jFrame.setSize(900, 800);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        AppClient appClient = new AppClient();
        appClient.setMfcfIp("192.168.1.19");
        appClient.setProgressBar(new ProgressBar());
        appClient.addProgressBar(jFrame);
        appClient.startup();
        jFrame.setVisible(true);
        appClient.requestResource(1, "F:\\curr\\rewwy");
    }

}
