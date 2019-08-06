package com.mec.mfct.progressBar;

import com.mec.mfct.file.Resource;
import com.mec.mfct.file.ResourceFile;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;

/**
 * 进度条
 */
public class ProgressBar implements IProgressBar{
    private static final NumberFormat nt = NumberFormat.getPercentInstance();
    private Resource resource;
    private JPanel panel;
    private JDialog dialog;
    private JScrollPane scrollPane;

    public ProgressBar() {
        nt.setMinimumFractionDigits(2);
    }

    @Override
    public void addProgressBar(JFrame jFrame) {
        JButton button = new JButton("测试模态框的功能~~");
        jFrame.add(button);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(button.getText());
            }
        });

        dialog = new JDialog(jFrame);
        dialog.setSize(new Dimension(500, 200));
        dialog.setLocationRelativeTo(null);
        dialog.setUndecorated(true);
        dialog.setOpacity(0.7f);
        dialog.setModal(true);

        panel = new JPanel();
        panel.setPreferredSize(new Dimension(400, 170));
        panel.setBackground(new Color(0xA7FFAB));

        TitledBorder titledBorder = new TitledBorder("文件接收中");
        titledBorder.setTitleFont(new Font("微软雅黑", Font.BOLD, 20));
        titledBorder.setTitleJustification(TitledBorder.CENTER);

        panel.setBorder(titledBorder);

        dialog.add(panel);

        scrollPane = new JScrollPane(panel);
//        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        dialog.add(scrollPane);
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public void createProgress(ResourceFile file) {
        if (!file.isReceiving()) {
            synchronized (Progress.class) {
                if (!file.isReceiving()) {
                    System.out.println("创建新的进度条");
                    new Thread(new Progress(file)).start();
                }
            }
        }

        new Thread(new DialogShow()).start();
    }

    class DialogShow implements Runnable {

        @Override
        public void run() {
            if (!dialog.isVisible()) {
                dialog.setVisible(true);
            }
        }
    }

    class Progress implements Runnable{
        private JProgressBar bar;
        private JLabel jLabel;
        private ResourceFile file;
        private long sleepTime;

        public Progress(ResourceFile file) {
            jLabel = new JLabel(file.getFileName());
            this.bar = new JProgressBar();
            this.file = file;
            sleepTime = 100;
        }

        @Override
        public void run() {
            jLabel.setPreferredSize(new Dimension(300, 13));
            panel.add(jLabel);

            bar.setBackground(new Color(0xCFFAE0));
            bar.setPreferredSize(new Dimension(150,15));
            bar.setStringPainted(true);
            bar.setIndeterminate(false);//采用确定的进度条样式
            bar.setString("文件正在接收中....");
            long fileLongLen = file.getLength();
            int rightMoveStep = (longHighestHalfByteLocation(fileLongLen) - 31);
            int fileIntLen = (int)(fileLongLen >> rightMoveStep);
            long receivedLen = 0;
            bar.setMaximum(fileIntLen);
            bar.setStringPainted(true);//设置进度条显示提示信息
            panel.add(bar);

            while(fileLongLen > receivedLen) {
                try {
                    int value;
                    if (receivedLen > 0xFFFFFFFF) {
                        value = (int)(receivedLen >> rightMoveStep);
                    } else {
                        value = (int)receivedLen;
                    }

                    bar.setValue(value);

                    float progress = value/(float)fileIntLen;

                    bar.setString("文件接受了：" + nt.format(progress));
                    Thread.sleep(sleepTime);

                }catch(Exception e) {
                    e.printStackTrace();
                }
                receivedLen = file.getReceivedLenth();
            }
            bar.setString("文件接受结束");
            try {
                Thread.sleep(100);
                bar.setVisible(false);
                jLabel.setVisible(false);
                synchronized (Progress.class) {
                    if (resource.isTransFinished()) {
                        dialog.dispose();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private int longHighestHalfByteLocation(long num) {
        if (num > 0x0FFFFFFFFFFFFFFFL){
            return 64;
        }
        if (num > 0x00FFFFFFFFFFFFFFL){
            return 60;
        }
        if (num > 0x000FFFFFFFFFFFFFL){
            return 56;
        }
        if (num > 0x0000FFFFFFFFFFFFL){
            return 52;
        }
        if (num > 0x00000FFFFFFFFFFFL){
            return 48;
        }
        if (num > 0x000000FFFFFFFFFFL){
            return 40;
        }
        if (num > 0x0000000FFFFFFFFFL) {
            return 36;
        }
        return 32;
    }
}
