package com.mec.mfct.file;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;

/**
 *文件资源
 */
public class Resource implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = -7466677245713935704L;
    private int resourceId;

    private int fileNo;
    private ResourceFileSet fileSet;

    public Resource() {
        fileSet = new ResourceFileSet();
        fileNo = 0;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    void receiveSection(int fileId, long offset, byte[] fileBlock) {
        fileSet.receivedOneFileSection(fileId, offset, fileBlock);
    }

    public Iterator<ResourceFile> getFiles() {
        return fileSet.getFiles();
    }

    public boolean checkAllFileExist() {
        return fileSet.checkFileExist();
    }

    public void setAbsoultePath(String absoultePath) {
        fileSet.setAbsoultePathPart(absoultePath);
    }

    public void scanResource(String resourceAbsoultePath) {
        fileSet.setAbsoultePathPart(resourceAbsoultePath);
        File file = new File(resourceAbsoultePath);

        scanFiles(resourceAbsoultePath, "", file);
    }

    public int getResourceId() {
        return resourceId;
    }

    /**
     * 根据文件路径扫描文件，然后把文件放到map里，以文件编号为键，储存文件信息的ResourceFile为值
     * @param absoultePart 绝对路径
     * @param filePath 文件路径
     * @param file 文件
     */
    private void scanFiles(String absoultePart, String filePath, File file) {
        if (file.isFile()) {
            long fileSize = file.length();
            fileSet.addFile(fileNo++, fileSize, filePath);
            return;
        }
        //如果是文件夹递归扫描
        File[] files = file.listFiles();
        for (File curfile : files) {
            String path = curfile.getName();
            scanFiles(absoultePart, filePath + "\\" + path, curfile);
        }
    }

    public ResourceFileSet getFileSet() {
        return fileSet;
    }

    public int filesCount() {
        return fileSet.getFilesCount();
    }

    /**
     * 判断传输是否完成
     * @return
     */
    public synchronized boolean isTransFinished() {
        Iterator<ResourceFile> files = fileSet.getFiles();
        while (files.hasNext()) {
            ResourceFile file = files.next();
            if (!file.isFinished()) {
                return false;
            }
        }
        return true;
    }

    byte[] senderSection(int fileId, long offset, int len) {
        return fileSet.sentOneFileSection(fileId, offset, len);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "resourceId=" + resourceId +
                ", fileSet=" + fileSet +
                '}';
    }
}
