package com.mec.mfct.mfctServer;

import com.mec.mfct.file.*;

import java.util.Iterator;

/**
 * 默认文件的分发策略
 */
public class DefaultFileDistributeStrategy implements IFileDistributeStrategy {
    /**
     * 划分文件发送
     * @param resource 文件资源
     * @param senderCount 参与发送文件的发送者个数
     * @return
     */
    @Override
    public SenderTask[] fileDistribute(Resource resource, int senderCount) {
        SenderTask[] tasks = new SenderTask[senderCount];
        initArray(tasks);
        Iterator<ResourceFile> files = resource.getFiles();
        int senderIndex = 0;
        while (files.hasNext()) {
            ResourceFile file = files.next();
            Iterator<OffsetLen> sections = file.getSections();

            int sectionsCount = 0;
            int length = 0;

            while (sections.hasNext()) {
                OffsetLen offsetLen = sections.next();
                long len = offsetLen.getLength();
                long offset = offsetLen.getOffset();


                for (long size = len; size > 0; size -= DEFAULT_SECTION_SIZE, offset += DEFAULT_SECTION_SIZE) {
                    sectionsCount++;
                    //将文件以DEFAULT_SECTION_SIZE大小分成若干片
                    if (size < DEFAULT_SECTION_SIZE) {
                        int count = (int) (size);
                        tasks[senderIndex].addTask(new MataData(file.getFileId(), offset, count));
                        //让多个文件发送者轮流发送
                        senderIndex = (senderIndex + 1) % senderCount;
                        length += count;
                        break;
                    }
                    tasks[senderIndex].addTask(new MataData(file.getFileId(), offset, DEFAULT_SECTION_SIZE));
                    senderIndex = (senderIndex + 1) % senderCount;
                    length += DEFAULT_SECTION_SIZE;
                }
            }
        }

        return tasks;
    }

    private void initArray(SenderTask[] tasks) {
        for (int index = 0 ; index < tasks.length; index++) {
            tasks[index] = new SenderTask();
        }
    }

}
