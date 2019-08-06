package com.mec.mfct.mfctServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mec.mec_rmi.core.Node;
import com.mec.mec_rmi.core.RmiResourceFactory;
import com.mec.mec_rmi.core.RmiServer;
import com.mec.mfct.file.Resource;
import com.mec.mfct.util.ServerThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * 文件传输服务器
 */
public class MFCTServer {
    private static Gson gson = new GsonBuilder().create();
    private RmiServer appInteractiveServer;
    private ServerThread appTransClient;
    private volatile boolean goon;
    private int interactivePort;
    private int transforPort;
    private int requestTransResourcePort;

    private Distributor distributor;

    public MFCTServer() {
        this.interactivePort = IDefaultConnectConfig.DEFAULT_APP_INTERACTIVE_PORT;
        this.transforPort = IDefaultConnectConfig.DEFAULT_TRANS_PORT;
        this.requestTransResourcePort = IDefaultConnectConfig.DEFAULT_REQUEST_TRANS_RESOURCE_PORT;

        distributor = new Distributor();
    }

    public void setRequestTransResource(int port) {
        this.requestTransResourcePort = port;
    }

    public void setinteractivePort(int interactivePort) {
        this.interactivePort = interactivePort;
    }

    public void setTransforPort(int transforPort) {
        this.transforPort = transforPort;
    }

    public void setFileDistributeStrategy(IFileDistributeStrategy  distributeStrategy) {
        distributor.setDistributeStrategy(distributeStrategy);
    }

    public void startup() {
        if (goon == true) {
            return;
        }
        try {
            goon = true;
            initServers();
            appTransClient.startup();
            appInteractiveServer.startup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void initServers() {
        try {
            RmiResourceFactory.registery(RequestAction.class, IRequestAction.class);
            appInteractiveServer = new RmiServer(interactivePort);
        } catch (Exception e) {
            e.printStackTrace();
        }
        appTransClient = new ServerThread(requestTransResourcePort) {
            @Override
            public void doSomething(Socket socket) {
                try {
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                    String ip = socket.getInetAddress().getHostAddress();
                    Node node = new Node(transforPort, ip);
                    String json = dis.readUTF();
                    Resource resource = gson.fromJson(json, Resource.class);
                    int resourceId = resource.getResourceId();
                    //拥有此资源的结点
                    List<NetNode> nodes = ResourceManager.getResourceOwner(resourceId);
                    distributor.setNode(node);
                    distributor.setOwners(nodes);
                    distributor.setResource(resource);
                    ServerThread.getThreadPool().execute(distributor);
                    dos.writeInt(nodes.size());

                    dis.close();
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void shutdown() {
        appTransClient.stop();
        appInteractiveServer.shutdown();
    }
}
