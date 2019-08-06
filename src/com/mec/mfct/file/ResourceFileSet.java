package com.mec.mfct.file;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 文件资源信息表，包括所有的文件资源
 */
class ResourceFileSet implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8253458003406553704L;
	private String absoultePathPart;
    private Map<Integer, ResourceFile> files;

    ResourceFileSet() {
        this("");
    }

    ResourceFileSet(String absoultePathPart) {
        this.absoultePathPart = absoultePathPart;
        files = new HashMap<>();
    }

    void setAbsoultePathPart(String absoultePathPart) {
        this.absoultePathPart = absoultePathPart;
    }

    /**
     * 检测文件是否存在
     * @return
     */
    boolean checkFileExist() {
        if (absoultePathPart == null) {
            System.out.println("请设置AbsoultePartPath!!!");
            return false;
        }
        return isFileExist();
    }

    /**
     * 检测文件是否存在
     * @return
     */
    private boolean isFileExist() {
        boolean isAllExist = true;
        for (ResourceFile rsFile : files.values()) {
            String absoultePath = absoultePathPart + rsFile.getFilePath();
            File file = new File(absoultePath);
            if (file.exists()) {
                if (file.length() == rsFile.getLength()) {
                    rsFile.setFinished();
                    continue;
                }
            }
            isAllExist = false;
        }

        return isAllExist;
    }

    void addFile(int fileId, long fileSize, String filePath) {
        ResourceFile file = new ResourceFile(fileSize);
        file.setFilePath(filePath);
        file.setFileId(fileId);
        files.put(fileId, file);
    }

    /**
     * 接收一个文件片段
     * @param fileId 文件编号
     * @param offset 偏移量
     * @param fileBlock 文件的一个片段
     */
    void receivedOneFileSection(int fileId, long offset, byte[] fileBlock) {
        int len = fileBlock.length;

        ResourceFile file = files.get(fileId);
        String fileAbsoultePath = absoultePathPart + file.getFilePath();

        RandomAccessFile raf = FileHandlePool.getFileHandle(fileAbsoultePath, "rw");
        file.receivedOneSection(raf, fileBlock, offset, len);
        //如果结束了就把文件句柄池关闭
        if (file.isFinished()) {
            FileHandlePool.closeFileHandle(fileAbsoultePath);
        }
    }

    /**
     * 发送一个文件片段
     * @param fileId 文件编号
     * @param offset 文件偏移量
     * @param len 文件片段的长度
     * @return
     */
    byte[] sentOneFileSection(int fileId, long offset, int len) {
        ResourceFile file = files.get(fileId);
        String fileAbsoultePath = absoultePathPart + file.getFilePath();
        RandomAccessFile raf = FileHandlePool.getFileHandle(fileAbsoultePath, "r");

        return file.readFileSection(raf, offset, len);
    }

    int getFilesCount() {
        return files.size();
    }

    Iterator<ResourceFile> getFiles() {
        return files.values().iterator();
    }

    public ResourceFile getFile(int fileId) {
        return files.get(fileId);
    }

    @Override
    public String toString() {
        return "ResourceFileSet{" +
//                "absoultePathPart='" + absoultePathPart + '\'' +
                ", files=" + files +
                '}';
    }
}
