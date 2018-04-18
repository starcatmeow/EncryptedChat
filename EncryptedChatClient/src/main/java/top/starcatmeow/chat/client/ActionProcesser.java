package top.starcatmeow.chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static top.starcatmeow.chat.client.ChatClientUI.*;


public class ActionProcesser implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "连接服务器") {
            jb2.setEnabled(false);
            jb3.setEnabled(false);
            label.setText("等待输入服务器信息");

            boolean b1 = true;
            while (b1) {
                String ip = JOptionPane.showInputDialog("请输入IP：");

                String port = JOptionPane.showInputDialog("请输入端口：");
                label.setText("正在建立Socket连接");
                socket = null;
                try {
                    socket = new Socket(ip, new Integer(port));
                    b1 = false;
                    label.setText("Socket连接建立成功，正在从服务器取得密钥（1/5）");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    label.setText("无法连接至服务器，请尝试重新连接");
                    jb2.setEnabled(true);
                    jb3.setEnabled(true);
                    return;
                } catch (NumberFormatException e1) {
                    e1.printStackTrace();
                    label.setText("无法连接至服务器，请尝试重新连接");
                    jb2.setEnabled(true);
                    jb3.setEnabled(true);
                    return;
                }
            }

            try {
                Cert.getAESKey(socket);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            Main.messagehandlerThread = new Thread(new MessageHandler(socket));
            Main.messagehandlerThread.start();

            final JTextField jtf = ccui.jtf1;

            DataOutputStream dos = null;
            try {
                dos = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            final DataOutputStream finalDos = dos;
            jb1.setEnabled(true);
            jtf1.setEnabled(true);
            jb2.setEnabled(true);
            jb2.setText("断开连接");
            jb1.addActionListener(e14 -> {
                if (e14.getActionCommand() == "发送") {
                    if (!jtf.getText().equals("")) {
                        try {
                            finalDos.writeUTF(AES.getInstance().encrypt(jtf.getText()));
                            jtf.setText("");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        }
        if (e.getActionCommand() == "断开连接") {
            Main.messagehandlerThread.interrupt();
            jb1.setEnabled(false);
            jtf1.setEnabled(false);
            jb2.setEnabled(false);

            try {
                socket.close();
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, "断开失败！请尝试重启客户端！");
                e1.printStackTrace();
                return;
            }
            jb2.setEnabled(true);
            jb3.setEnabled(true);
            jb2.setText("连接服务器");
            int clear = JOptionPane.showConfirmDialog(null, "是否清空聊天记录？", "清屏", JOptionPane.YES_NO_OPTION);
            if (clear == 0) {
                jta1.setText("");
            }
            label.setText("等待用户操作");
        }
        if (e.getActionCommand() == "其它连接模式") {
            oreceivePanel.setLayout(new BorderLayout(5, 5));                                         //新建一个Panel->BorderLayout，包含接收框以及确认按钮，让用户把接收到的内容输入程序中
            oreceivePanel.add("Center", oreceiveJtf);
            oreceivePanel.add("East", oReceive);
            oreceivePanel.setBorder(BorderFactory.createTitledBorder("接收到的内容"));

            osendPanel.setLayout(new BorderLayout(5, 5));                                            //新建一个Panel->BorderLayout，包含输出框以及拷贝按钮，让用户把程序输出的内容发送出去
            osendPanel.add("Center", osendJtf);
            osendPanel.add("East", osend);
            osendPanel.setBorder(BorderFactory.createTitledBorder("需发送的内容"));

            oPanel.setLayout(new GridLayout(2, 1));                                                   //新建一个Panel->GridLayout，包含以上两个Panel
            oPanel.add(oreceivePanel);
            oPanel.add(osendPanel);

            ccui.jp7.add("Center", oPanel);                                                           //加入其它连接模式需要的Panel
            ccui.revalidate();                                                                                  //重绘界面
            jb2.setEnabled(false);                                                                              //处理后续界面响应
            jb3.setText("退出此模式");



            label.setText("等待用户选择角色");
            Object[] options = {"发起方", "接收方"};
            int result = JOptionPane.showOptionDialog(null, "请选择你的角色", "选择角色", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (result == 0) {
                inOther = true;
                SwingUtilities.invokeLater(() -> label.setText("等待对方发送公钥"));
//                new Thread(()->{
                try {
                    Cert.makeandsendAESKey();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
//                }).start();
                jb1.addActionListener(ap);
                Main.messagehandlerThread = new Thread(new MessageHandler());
                Main.messagehandlerThread.start();

                jb1.setEnabled(true);
                jtf1.setEnabled(true);
                SwingUtilities.invokeLater(() -> label.setText("正常"));
            } else if (result == 1) {
                inOther = true;
                SwingUtilities.invokeLater(() -> label.setText("等待对方发送密钥"));
                try {
                    Cert.getAESKey();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                jb1.addActionListener(ap);
                Main.messagehandlerThread = new Thread(new MessageHandler());
                Main.messagehandlerThread.start();

                jb1.setEnabled(true);
                jtf1.setEnabled(true);
                SwingUtilities.invokeLater(() -> label.setText("正常"));
            }
        }
        if (e.getActionCommand() == "退出此模式") {
            inOther = false;
            ccui.jp7.remove(1);
            ccui.revalidate();
            jb1.removeActionListener(ChatClientUI.ap);
            jb1.setEnabled(false);
            jtf1.setEnabled(false);
            jb2.setEnabled(true);
            jb3.setText("其它连接模式");

            oreceiveJtf.setText("");
            osendJtf.setText("");
            Main.messagehandlerThread.stop();
            int clear = JOptionPane.showConfirmDialog(null, "是否清空聊天记录？", "清屏", JOptionPane.YES_NO_OPTION);
            if (clear == 0) {
                jta1.setText("");
            }
            label.setText("等待用户操作");
        }
        if (e.getActionCommand() == "拷贝") {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection(osendJtf.getText());
            clip.setContents(tText, null);
            osend.setText("已拷贝");
            osend.setEnabled(false);
        }
        if (e.getActionCommand() == "确认") {
            if (!oreceiveJtf.getText().equals("")) {
                receiveBuffer = oreceiveJtf.getText();
                oreceiveJtf.setText("");
            }
        }
        if (e.getActionCommand() == "发送") {
            if (!jtf1.getText().equals("")) {

                SwingUtilities.invokeLater(() -> {
                    String encryptedchat = AES.getInstance().encrypt(jtf1.getText());
                    ChatClientUI.writetoosendjtf(encryptedchat);
                    jta1.append("你 说 " + jtf1.getText() + "\n");
                    jtf1.setText("");
                });

            }
        }
    }
}
