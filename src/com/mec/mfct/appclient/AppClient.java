package com.mec.mfct.appclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mec.mec_rmi.core.RmiClient;
import com.mec.mfct.ResourceOwner.ResourceOwner;
import com.mec.mfct.file.Receiver;
import com.mec.mfct.file.Resource;
import com.mec.mfct.file.ResourceFile;
import com.mec.mfct.mfctServer.IDefaultConnectConfig;
import com.mec.mfct.mfctServer.IRequestAction;
import com.mec.mfct.progressBar.IProgressBar;
import com.mec.mfct.util.ClientThread;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Iterator;

/**
 * 客户端
 */
public class AppClient {
    private Gson gson = new GsonBuilder().create();
    private RmiClient requestResource;
    private ClientThread requestTransResource;
    private Receiver receiver;
    private String mfcfIp;
    private int requestResourcePort;
    private int requestTransResourcePort;
    private int receiverPort;
    private IRequestAction requestAction;
    private String localHost;

    private int requestId;
    private boolean isFinished;
    private IProgressBar progressBar;
    private Resource resource;

    public AppClient() {
        this.requestResourcePort = IDefaultConnectConfig.DEFAULT_APP_INTERACTIVE_PORT;
        this.requestTransResourcePort = IDefaultConnectConfig.DEFAULT_REQUEST_TRANS_RESOURCE_PORT;
        this.receiverPort = IDefaultConnectConfig.DEFAULT_TRANS_PORT;
    }

    public void setProgressBar(IProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setRequestTransResourcePort(int requestTransResourcePort) {
        this.requestTransResourcePort = requestTransResourcePort;
    }

    public void setRequestResourcePort(int port) {
        this.requestResourcePort = port;
    }

    public void setMfcfIp(String ip) {
        this.mfcfIp = ip;
    }

    public void setReceiverPort(int receiverPort) {
        this.receiverPort = receiverPort;
    }
    //根据资源id和绝对路径请求资源，通过和文件传输服务器建立RMI短链接
    public void requestResource(int requestId, String absoultePath) {
        this.requestId = requestId;
        this.resource = requestAction.requestResource(requestId);
        if (resource == null) {
            System.out.println("该资源不存在！！！");
            return;
        }
        this.resource.setAbsoultePath(absoultePath);
        //检测该资源下文件是否存在
        boolean res = resource.checkAllFileExist();
        if (res) {
            receiveComplete();
            return;
        }
        System.out.println("开始请求资源！！！");
        requestTransResourceStartup();
        Iterator<ResourceFile> files = resource.getFiles();
        while (files.hasNext()) {
            ResourceFile file = files.next();
            System.out.println("file.getReceivedLenth() " + file.getReceivedLenth());
            System.out.println("file.getSectionsSize() " + file.getSectionsSize());
            System.out.println("file " + file);
        }
        receiveComplete();
    }

    public void startup() {
        receiver = new Receiver();
        receiver.setPort(receiverPort);

        requestResource = new RmiClient();
        requestResource.setServerIp(mfcfIp);
        requestResource.setPort(requestResourcePort);
        if (requestAction == null) {
            requestAction = requestResource.getProxy(IRequestAction.class);
        }
        requestTransResource = new ClientThread(this.mfcfIp, requestTransResourcePort) {
            @Override
            public void doSomething(Socket socket) {
                try {
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                    System.out.println("发送真正需要的资源列表!!!");
                    dos.writeUTF(gson.toJson(resource));

                    int senderCount = dis.readInt(); //参与发送的个数
                    localHost = socket.getLocalAddress().getHostAddress();
                    receiver.setConnectCount(senderCount);
                    receiver.setResource(resource);
                    if (progressBar != null) {
                        progressBar.setResource(resource);
                        //设置进度条界面
                        receiver.setProgressBar(progressBar);
                    }
                    //启动文件接收线程
                    receiver.startup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void addProgressBar(JFrame jFrame) {
        progressBar.addProgressBar(jFrame);
    }

    /**
     * 启动文件doSomething()线程工作
     */
    private void requestTransResourceStartup() {
        //启动ClientThread线程
        requestTransResource.startup();
        synchronized (Receiver.class) {
            try {
                Receiver.class.wait(); //等待，文件接收完后唤醒此线程
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 只要文件没有传输结束，就不断启动接收线程接收文件
     */
    private void receiveComplete() {
        isFinished = resource.isTransFinished();
        if (isFinished) {
            //如果传输结束，拥有资源的客户端向文件传输服务器注册资源
            boolean res = requestAction.registryResourceOwner(requestId, localHost);
            //不同机子检测时用
//            if (res) {
//                ResourceOwner owner = new ResourceOwner();
//                owner.setMfcfIp(mfcfIp);
//                owner.addResource(requestId, resource);
//                owner.setLocalHost(localHost);
//                owner.startup();
//            }
//            return;
        }

        requestTransResourceStartup();
        receiveComplete();
    }

}
