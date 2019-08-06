package com.mec.mfct.file;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 未接收到的文件片段
 */
class UnreceivedSection implements Serializable{
    private static final long serialVersionUID = 7016011730275260214L;
    /**
     * 存储的时未接收的文件片段
     */
    private List<OffsetLen> unreceivedList;

    UnreceivedSection(long fileSize) {
        unreceivedList = new ArrayList<>();
        unreceivedList.add(new OffsetLen(0, fileSize));
    }

    public int getSectionSize() {
        return unreceivedList.size();
    }

    boolean isFinished() {
        return unreceivedList.isEmpty();
    }

    /**
     * 接收一个文件片段
     * @param offset
     * @param length
     */
    synchronized public void receiveOneSection(long offset, long length) {
        for (int index = 0; index < unreceivedList.size(); index++) {
            OffsetLen sd = unreceivedList.get(index);
            long off = sd.getOffset();
            long len = sd.getLength();

            if (off == offset && offset + length == off + len) {
                unreceivedList.remove(sd);
                return;
            }
            if(off <= offset && offset + length <= off + len) {
                if (offset == off) {
                    sd.setOffset(offset + length);
                    sd.setLength(len - length);
                    return;
                }
                if (offset + length == off + len) {
                    sd.setLength(offset - off);
                    return;
                }
                sd.setOffset(off);
                sd.setLength(offset - off - 1);
                OffsetLen nextSd = new OffsetLen(offset + length, len - (offset - off + length));
                unreceivedList.add(index, nextSd);
                return;
            }
        }
    }

    void setFinished() {
        unreceivedList.clear();
    }

    Iterator<OffsetLen> getSections() {
        return unreceivedList.iterator();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (OffsetLen sd : unreceivedList) {
            buffer.append(sd).append("\n");
        }
        return buffer.toString();
    }
}
