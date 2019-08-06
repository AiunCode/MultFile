package com.mec.mfct.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class FileHandleTest {
    private static int bufferCount = 20;

    public static void main(String[] args) {
        try {
            File file = new File("D:\\ProgrammeStudy\\temp\\copy.txt");
            File outFile = new File("D:\\ProgrammeStudy\\temp\\resource\\resource.txt");
//          long fileSize = outFile.length();
//
//          FileInputStream fis = new FileInputStream(outFile);
//          FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(outFile));
//            bis.skip();
            RandomAccessFile rafIn = new RandomAccessFile("D:\\ProgrammeStudy\\temp\\copy.txt", "rw");
            RandomAccessFile rafOut = new RandomAccessFile("D:\\ProgrammeStudy\\temp\\resource\\resource.txt", "r");
            byte[] bytes = new byte[bufferCount];
            long fileSize = rafOut.length();
            for (long size = fileSize; size > 0; size -= bufferCount) {
                if (size < bufferCount) {
                    int count = (int) (size);
                    bytes = new byte[count];
                }
                try {
                    rafOut.read(bytes);
                    rafIn.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(InputStream is, OutputStream os, long fileSize) {

    }
}
