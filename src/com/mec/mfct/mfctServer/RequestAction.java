package com.mec.mfct.mfctServer;

import com.mec.mfct.file.Resource;

public class RequestAction implements IRequestAction{

    @Override
    public Resource requestResource(int resourceId) {
        return ResourceManager.getResource(resourceId);
    }

    @Override
    public boolean registryResource(int resourceId, Resource resource) {
        return ResourceManager.resourceRegistry(resourceId, resource);
    }

    @Override
    public void healthCheck(int resourceId, String ip, boolean resourceIsExist) {
        System.out.println(ip + " 开始进行心跳检测");
        ResourceManager.checkHealth(resourceId, ip, resourceIsExist);
    }

    @Override
    public boolean registryResourceOwner(int resourceId, String ip) {
        return ResourceManager.ownerRegistry(resourceId, ip);
    }

    @Override
    public void revokeResource(int resourceId) {
        ResourceManager.revokeResource(resourceId);
    }

    @Override
    public void alterResource(int resourceId, Resource resource) {
        revokeResource(resourceId);
        registryResource(resourceId, resource);
    }
}
