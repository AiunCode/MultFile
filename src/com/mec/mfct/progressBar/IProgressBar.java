package com.mec.mfct.progressBar;

import com.mec.mfct.file.Resource;
import com.mec.mfct.file.ResourceFile;

import javax.swing.*;

public interface IProgressBar {
    void createProgress(ResourceFile resourceFile);
    void setResource(Resource resource);
    void addProgressBar(JFrame jFrame);
}
