package com.mec.mfct.file;

import com.mec.mec_rmi.core.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 发送任务
 */
public class SenderTask implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8279773091295931724L;
	private List<MataData> tasks;
    /**
     * 服务器结点
     */
	private Node node;
    private int number;
    private int resourceId;

    public SenderTask() {
        number = 0;
        tasks = new ArrayList<>();
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    int taskSize() {
        return tasks.size();
    }

    public void addTask(MataData data) {
        tasks.add(data);
    }

    void setReceiverIpAndPort(String ip, int port) {
        node = new Node(port, ip, "");
    }

    int getReceiverPort() {
        return node.getPort();
    }

    String getReceiverIp() {
        return this.node.getIp();
    }

    private boolean hasNext() {
        return number < tasks.size();
    }

    public void addNode(Node node) {
        this.node = node;
    }

    MataData next() {
        if (hasNext()) {
            return tasks.get(number++);
        }
        return null;
    }

    @Override
    public String toString() {
        return "SenderTask{" +
                "tasks=" + tasks +
                ", node=" + node +
                ", number=" + number +
                '}';
    }
}
