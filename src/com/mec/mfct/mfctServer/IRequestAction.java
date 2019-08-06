package com.mec.mfct.mfctServer;

import com.mec.mfct.file.Resource;

/**
 * 请求动作
 */
public interface IRequestAction {
    Resource requestResource(int resourceId);
    boolean registryResource(int resourceId, Resource resource);
    void healthCheck(int resourceId, String ip, boolean resourceIsExist);

    /**
     * 作为有资源的客户端注册
     * @param resourceId
     * @param ip
     * @return
     */
    boolean registryResourceOwner(int resourceId, String ip);
    void revokeResource(int resourceId);
    void alterResource(int resourceId, Resource resource);
}
