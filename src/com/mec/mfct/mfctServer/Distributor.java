package com.mec.mfct.mfctServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mec.mec_rmi.core.Node;
import com.mec.mfct.file.Resource;
import com.mec.mfct.file.SenderTask;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

/**
 * 给具体的拥有资源的服务器分配发送任务
 */
class Distributor implements Runnable{
    private static final Gson gson = new GsonBuilder().create();

    private Socket socket;
    private List<NetNode> owners;
    private SenderTask[] tasks;
    private Resource resource;
    private IFileDistributeStrategy distributeStrategy;
    private Node node;
    private int distributePort;

    Distributor() {
        this(null, null, null);
    }

    Distributor(List<NetNode> owners, Resource resource, Node node) {
        this.node = node;
        this.owners = owners;
        this.resource = resource;
        this.distributeStrategy = new DefaultFileDistributeStrategy();
        this.distributePort = IDefaultConnectConfig.DEFAULT_DISTRIBUTE_PORT;
    }

    public void setOwners(List<NetNode> owners) {
        this.owners = owners;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setDistributeStrategy(IFileDistributeStrategy distributeStrategy) {
        this.distributeStrategy = distributeStrategy;
    }

    @Override
    public void run() {
        System.out.println("开始为资源客户端分配任务!!!");
        tasks = distributeStrategy.fileDistribute(resource, owners.size());
        System.out.println("任务分配成功！！！");
        Iterator<NetNode> nodes = owners.iterator();
        int num = 0;
        while (nodes.hasNext()) {
            NetNode sender = nodes.next();
            tasks[num].addNode(this.node);
            tasks[num].setResourceId(resource.getResourceId());
            try {
                System.out.println("准备给有资源客户端发送任务!!!");
                socket = new Socket(sender.getIp(), distributePort);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                System.out.println("与资源拥有建立起连接!!");
                oos.writeObject(tasks[num]);
                System.out.println("已给有资源客户端发送任务!!!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            num++;
        }
    }

}

