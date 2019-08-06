package com.mec.mfct.file;

import java.io.Serializable;
import java.util.Objects;

/**
 * 文件的偏移量和长度
 */
public class OffsetLen implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 384222600078462509L;
	private long offset;
    private long length;

    OffsetLen(long offset, long length) {
        this.offset = offset;
        this.length = length;
    }

    public long getOffset() {
        return offset;
    }

    void setOffset(long offset) {
        this.offset = offset;
    }

    public long getLength() {
        return length;
    }

    void setLength(long length) {
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OffsetLen)) return false;
        OffsetLen that = (OffsetLen) o;
        return offset == that.offset &&
                length == that.length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, length);
    }

    @Override
    public String toString() {
        return "OffsetLen{" +
                "offset=" + offset +
                ", length=" + length +
                '}';
    }
}
