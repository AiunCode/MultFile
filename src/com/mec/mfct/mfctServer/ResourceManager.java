package com.mec.mfct.mfctServer;

import com.mec.mfct.file.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 资源管理者
 */
class ResourceManager {
    private static Map<Integer, ResourceDefinition> resourceMap;

    static {
        resourceMap = new ConcurrentHashMap<>();
    }

    static boolean resourceRegistry(int resourceId, Resource resource) {
        if (!resourceMap.containsKey(resourceId)) {
            synchronized (ResourceManager.class) {
                if (!resourceMap.containsKey(resourceId)) {
                    resourceMap.put(resourceId, new ResourceDefinition(resource));
                    return true;
                }
            }
        }

        return false;
    }

    static void checkHealth(int resourceId, String ip, boolean resourceIsExist) {
        if (resourceIsExist) {
            ownerRegistry(resourceId, ip);
            long startTime = System.currentTimeMillis();
            long endTime = System.currentTimeMillis();
            NetNode node = resourceMap.get(resourceId).getNode(ip);
            node.Online();
            node.setNetTime((int)(endTime - startTime));
        }
    }

    static boolean ownerRegistry(int resourceId, String ip) {
        ResourceDefinition rd = resourceMap.get(resourceId);
        return rd.addOwner(ip);
    }

    static void revokeResource(int resourceId) {
        resourceMap.remove(resourceId).clearOwners();
    }

    static Resource getResource(int resourceId) {
        System.out.println("用户开始请求资源!!!");
        return resourceMap.get(resourceId).getResource();
    }

    static List<NetNode> getResourceOwner(int resourceId) {
        return resourceMap.get(resourceId).getOwners();
    }
}
