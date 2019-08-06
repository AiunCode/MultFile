package com.mec.mfct.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Iterator;

/**
 * 资源文件，存放着文件的详细信息
 */
public class ResourceFile implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4770971687122206413L;
    /**
     * 文件路径
     */
	private String filePath;
    private String fileName;
    private long length;
    private int fileId;
    /**
     * 文件未接收片段
     */
    private UnreceivedSection unreceivedSection;
    /**
     * 接收到的文件长度
     */
    private volatile long receviedLen;

    ResourceFile(long fileSize) {
    	unreceivedSection = new UnreceivedSection(fileSize);
    	setLength(fileSize);
    	receviedLen = 0;
	}

	public String getFileName() {
        return fileName;
    }

	void setFileId(int fileId){
        this.fileId = fileId;
    }

    public int getFileId() {
        return fileId;
    }

    void setFilePath(String filePath) {
        this.filePath = filePath;
        int index = filePath.lastIndexOf("\\");
        this.fileName = filePath.substring(index + 1);
    }

    private void setLength(long length) {
        this.length = length;
    }

    public long getLength() {
        return length;
    }

    public Iterator<OffsetLen> getSections() {
        return unreceivedSection.getSections();
    }

    String getFilePath(){
        return filePath;
    }

    void receivedOneSection(RandomAccessFile raf, byte[] fileSectionBlock, long offset, int length) {
        if (raf == null) {
            unreceivedSection.setFinished();
            return;
        }
        synchronized (ResourceFile.class) {
            try {
                raf.seek(offset);
                raf.write(fileSectionBlock);
                unreceivedSection.receiveOneSection(offset, length);
                receviedLen += length;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized  byte[] readFileSection(RandomAccessFile raf, long offset, int length) {
        byte[] res = new byte[length];
        try {
            raf.seek(offset);
            raf.readFully(res);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    public long getReceivedLenth() {
        return receviedLen;
    }

    public int getSectionsSize() {
        return unreceivedSection.getSectionSize();
    }

    public boolean isReceiving() {
        return receviedLen != 0;
    }

    public void setFinished() {
        unreceivedSection.setFinished();
    }

    boolean isFinished() {
        if (unreceivedSection.isFinished()) {
            receviedLen = length;
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "ResourceFile{" +
                "filePath='" + filePath + '\'' +
                ", unreceivedSection=" + unreceivedSection +
                '}';
    }
}
