package com.mec.mfct.file;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 创建文件随机访问对象
 */
class FileHandlePool {
    private static Map<String, RandomAccessFile> fileMap;

    static {
        fileMap = new ConcurrentHashMap<>();
    }

    /**
     * 创建文件目录
     * @param fileAbsoultePath 文件绝对路径
     */
    private static void creatDirs(String fileAbsoultePath) {
        int index = fileAbsoultePath.lastIndexOf("\\");
        fileAbsoultePath = fileAbsoultePath.substring(0, index);
        File filePath = new File(fileAbsoultePath);
        filePath.mkdirs(); //创建文件
    }

    /**
     * 如果文件访问对象没有创建，则创建一个文件随机访问对象
     * @param fileAbsoultPath
     * @param mode
     * @return
     */
    static RandomAccessFile getFileHandle(String fileAbsoultPath, String mode) {
        if (!fileMap.containsKey(fileAbsoultPath)) {
            synchronized (FileHandlePool.class) {
                if (!fileMap.containsKey(fileAbsoultPath)) {
                    try {
                        if (mode.contains("w")) {
                            creatDirs(fileAbsoultPath);//如果是写的模式，创建文件夹
                        }
                        RandomAccessFile raf = new RandomAccessFile(fileAbsoultPath, mode);
                        fileMap.put(fileAbsoultPath, raf);
                        return raf;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        RandomAccessFile raf = fileMap.get(fileAbsoultPath);

        return raf;
    }

    /**
     * 关闭资源
     * @param fieAbsoultPath
     */
    static void closeFileHandle(String fieAbsoultPath) {
        RandomAccessFile raf = fileMap.remove(fieAbsoultPath);
        if (raf == null) {
            return;
        }
        try {
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            raf = null;
        }
    }

    /**
     * 清空map
     */
    static void clearMap() {
        Set<String> set = fileMap.keySet();
        for (String id : set) {
            RandomAccessFile raf = fileMap.get(id);
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileMap.clear();
    }

}
