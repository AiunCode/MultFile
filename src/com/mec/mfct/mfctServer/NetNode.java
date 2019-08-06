package com.mec.mfct.mfctServer;

import java.util.Objects;

/**
 * 服务器节点
 */
class NetNode {
    private String ip;
    private boolean offline;
    private int netTime;

    NetNode(String ip) {
        this.offline = false;
        this.ip = ip;
    }

    void setNetTime(int netTime) {
        this.netTime = netTime;
    }

    int getNetTime() {
        return netTime;
    }

    boolean isOffline() {
        return offline;
    }

    void setOffline() {
        offline = true;
    }

    void Online() {
        offline = false;
    }

    String getIp() {
        return ip;
    }

    void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NetNode)) return false;
        NetNode netNode = (NetNode) o;
        return Objects.equals(ip, netNode.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip);
    }
}
