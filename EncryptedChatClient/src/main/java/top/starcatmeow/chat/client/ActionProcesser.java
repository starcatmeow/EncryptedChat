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
import java.text.MessageFormat;

import static top.starcatmeow.chat.client.ChatClientUI.*;


public class ActionProcesser implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == getUIString.get("connect")) {
            jb2.setEnabled(false);                                                                                      //禁用两个操作按钮
            jb3.setEnabled(false);
            label.setText(getUIString.get("wfiserverinfo"));                                                            //设置UI状态信息

            boolean b1 = true;
            while (b1) {
                String ip = JOptionPane.showInputDialog(getUIString.get("enterip"));                                    //输入服务器信息

                String port = JOptionPane.showInputDialog(getUIString.get("enterport"));
                label.setText(getUIString.get("econnection"));                                                          //设置UI状态信息
                socket = null;
                try {
                    socket = new Socket(ip, new Integer(port));                                                         //向服务器发起Socket请求
                    b1 = false;
                    label.setText(MessageFormat.format(getUIString.get("getkey"), String.valueOf(1)));                  //设置UI状态信息
                } catch (IOException e1) {
                    //进入此处代表无法连接到服务器
                    e1.printStackTrace();                                                                               //输出错误信息至控制台
                    label.setText(getUIString.get("cannotconnect"));                                                    //设置UI状态信息
                    jb2.setEnabled(true);                                                                               //启用两个操作按钮
                    jb3.setEnabled(true);
                    return;                                                                                             //停止后续操作
                } catch (NumberFormatException e1) {
                    //进入此处代表用户输入的服务器端口无法正常转换为数字
                    e1.printStackTrace();                                                                               //输出错误信息至控制台
                    label.setText(getUIString.get("cannotconnect"));                                                    //设置UI状态信息
                    jb2.setEnabled(true);                                                                               //启用两个操作按钮
                    jb3.setEnabled(true);
                    return;                                                                                             //停止后续操作
                }
            }

            try {
                if (!Cert.getAESKey(socket)) {                                                                          //转交认证模块
                    //出错进入此处
                    jb2.setEnabled(true);                                                                               //启用两个操作按钮
                    jb3.setEnabled(true);
                    return;                                                                                             //停止后续操作
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                jb2.setEnabled(true);                                                                                   //启用两个操作按钮
                jb3.setEnabled(true);
                return;                                                                                                 //停止后续操作
            }

            Main.messagehandlerThread = new Thread(new MessageHandler(socket));                                         //新建接收线程
            Main.messagehandlerThread.start();                                                                          //启动线程

            final JTextField jtf = ccui.jtf1;

            DataOutputStream dos = null;
            try {
                dos = new DataOutputStream(socket.getOutputStream());                                                   //新建输出流
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            final DataOutputStream finalDos = dos;
            jb1.setEnabled(true);                                                                                       //启用发送按钮
            jtf1.setEnabled(true);                                                                                      //启用输入框
            jb2.setEnabled(true);                                                                                       //启用断开按钮
            jb2.setText(getUIString.get("disconnect"));                                                                 //更改按钮文字为断开
            jb1.addActionListener(e14 -> {
                if (e14.getActionCommand() == getUIString.get("send")) {                                                //新建发送按钮监听器
                    if (!jtf.getText().equals("")) {
                        //输入框不为空进入此处
                        try {
                            finalDos.writeUTF(AES.getInstance().encrypt(jtf.getText()));                                //加密后发送至服务器
                            jtf.setText("");                                                                            //清空输入框
                        } catch (IOException e1) {
                            e1.printStackTrace();                                                                       //显示错误信息
                        }
                    }
                }
            });
        }
        if (e.getActionCommand() == getUIString.get("disconnect")) {
            Main.messagehandlerThread.interrupt();
            jb1.setEnabled(false);
            jtf1.setEnabled(false);
            jb2.setEnabled(false);

            try {
                socket.close();
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, getUIString.get("cannotdisconnect"));
                e1.printStackTrace();
                return;
            }
            jb2.setEnabled(true);
            jb3.setEnabled(true);
            jb2.setText(getUIString.get("connect"));
            int clear = JOptionPane.showConfirmDialog(null, getUIString.get("emptymessages"), getUIString.get("clear"), JOptionPane.YES_NO_OPTION);
            if (clear == 0) {
                jta1.setText("");
            }
            label.setText(getUIString.get("waitoperation"));
        }
        if (e.getActionCommand() == getUIString.get("otherconnectmode")) {
            oreceivePanel.setLayout(new BorderLayout(5, 5));                                         //新建一个Panel->BorderLayout，包含接收框以及确认按钮，让用户把接收到的内容输入程序中
            oreceivePanel.add("Center", oreceiveJtf);
            oreceivePanel.add("East", oReceive);
            oreceivePanel.setBorder(BorderFactory.createTitledBorder(getUIString.get("receiveddata")));

            osendPanel.setLayout(new BorderLayout(5, 5));                                            //新建一个Panel->BorderLayout，包含输出框以及拷贝按钮，让用户把程序输出的内容发送出去
            osendPanel.add("Center", osendJtf);
            osendPanel.add("East", osend);
            osendPanel.setBorder(BorderFactory.createTitledBorder(getUIString.get("datatobesent")));

            oPanel.setLayout(new GridLayout(2, 1));                                                   //新建一个Panel->GridLayout，包含以上两个Panel
            oPanel.add(oreceivePanel);
            oPanel.add(osendPanel);

            ccui.jp7.add("Center", oPanel);                                                           //加入其它连接模式需要的Panel
            ccui.revalidate();                                                                                  //重绘界面
            jb2.setEnabled(false);                                                                              //处理后续界面响应
            jb3.setText(getUIString.get("exitothermode"));


            label.setText(getUIString.get("waitchoose"));
            Object[] options = {getUIString.get("initiator"), getUIString.get("receiver")};
            int result = JOptionPane.showOptionDialog(null, getUIString.get("chooserole"), getUIString.get("choose"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            //选择角色
            if (result == 0) {
                inOther = true;
                SwingUtilities.invokeLater(() -> label.setText(getUIString.get("waitpubkey")));
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
                SwingUtilities.invokeLater(() -> label.setText(getUIString.get("normal")));
            } else if (result == 1) {
                inOther = true;
                SwingUtilities.invokeLater(() -> label.setText(getUIString.get("waitseckey")));
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
                SwingUtilities.invokeLater(() -> label.setText(getUIString.get("normal")));
            }
        }
        if (e.getActionCommand() == getUIString.get("exitothermode")) {
            inOther = false;
            ccui.jp7.remove(1);
            ccui.revalidate();
            jb1.removeActionListener(ChatClientUI.ap);
            jb1.setEnabled(false);
            jtf1.setEnabled(false);
            jb2.setEnabled(true);
            jb3.setText(getUIString.get("otherconnectmode"));

            oreceiveJtf.setText("");
            osendJtf.setText("");
            Main.messagehandlerThread.stop();
            int clear = JOptionPane.showConfirmDialog(null, getUIString.get("emptymessages"), getUIString.get("clear"), JOptionPane.YES_NO_OPTION);
            if (clear == 0) {
                jta1.setText("");
            }
            label.setText(getUIString.get("waitoperation"));
        }
        if (e.getActionCommand() == getUIString.get("copy")) {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable tText = new StringSelection(osendJtf.getText());
            clip.setContents(tText, null);
            osend.setText(getUIString.get("copied"));
            osend.setEnabled(false);
        }
        if (e.getActionCommand() == getUIString.get("confirm")) {
            if (!oreceiveJtf.getText().equals("")) {
                receiveBuffer = oreceiveJtf.getText();
                oreceiveJtf.setText("");
            }
        }
        if (e.getActionCommand() == getUIString.get("send")) {
            if (!jtf1.getText().equals("")) {
                //当输入框不为空时进入
                SwingUtilities.invokeLater(() -> {
                    String encryptedchat = AES.getInstance().encrypt(jtf1.getText());                                   //加密输入框内的内容
                    ChatClientUI.writetoosendjtf(encryptedchat);                                                        //放入加密信息输出框中
                    jta1.append(getUIString.get("yousay") + " " + jtf1.getText() + "\n");                               //在聊天框中增加内容
                    jtf1.setText("");                                                                                   //清空输入框
                });
            }
        }
    }
}
