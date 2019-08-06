package com.mec.mfct.mfctServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.mec.mfct.file.Resource;
import com.my.util.core.TickTick;

/**
 * 维护node表，定时心跳检测
 */
public class ResourceDefinition {
    private TickTick tick;
    private Resource resource;
    private List<NetNode> owners;

    static {
    }

    /**
     * 定时心跳检测
     * @param resource
     */
    ResourceDefinition (Resource resource) {
        setResource(resource);
        owners = Collections.synchronizedList(new ArrayList<>());
        tick = new TickTick() {
            @Override
            public void doSomething() {
                healthCheck();
            }
        }.setTiming(IDefaultConnectConfig.DEFAULT_FLASHTIME);
        tick.startup();
    }

    void setResource (Resource resource) {
        this.resource = resource;
    }

    boolean addOwner(String ip) {
        NetNode node = new NetNode(ip);
        if (!owners.contains(node)) {
            synchronized (ResourceDefinition.class) {
                if (!owners.contains(node)) {
                    owners.add(node);
                    return true;
                }
            }
        }

        return false;
    }

    void clearOwners() {
        owners.clear();
    }

    List<NetNode> getOwners() {
        return owners;
    }

    Resource getResource() {
        return resource;
    }

    NetNode getNode(String ip) {
        NetNode node = new NetNode(ip);
        int index = owners.indexOf(node);
        return owners.get(index);
    }

    private void healthCheck() {
        Iterator<NetNode> nodes = owners.iterator();
        while (nodes.hasNext()) {
            NetNode node = nodes.next();
            if (node.isOffline()) {
                nodes.remove();
                continue;
            }
            node.setOffline();
        }
    }


}
