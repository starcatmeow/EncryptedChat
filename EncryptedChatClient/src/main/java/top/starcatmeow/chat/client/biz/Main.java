package top.starcatmeow.chat.client.biz;

import sun.plugin2.message.Message;
import top.starcatmeow.chat.client.ui.ChatClientUI;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Dongruixuan Li on 2017/1/30.
 */
public class Main {
    static Socket socket = null;
    static JLabel label = null;
    static ChatClientUI ccui = null;
    private static Thread messagehandlerThread = null;

    public static void main(String[] args) {
        try {
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception e) {
            //TODO exception
        }
        UIManager.put("RootPane.setupButtonVisible", false);
        ccui = new ChatClientUI();
        ccui.setDefaultLookAndFeelDecorated(true);
        ccui.pack();
        ccui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ccui.setSize(800, 600);
        ccui.setLocationRelativeTo(null);
        ccui.setTitle("聊天系统客户端");
        ccui.setVisible(true);
        label = ccui.getJl1();
        label.setText("等待用户操作");
        JButton jb1, jb2, jb3;
        jb1 = ccui.getJb1();
        jb2 = ccui.getJb2();
        jb3 = ccui.getJb3();
        jb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand() == "连接服务器") {
                    jb2.setEnabled(false);
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
                            label.setText("连接建立失败，请检查IP或端口是否输入正确");
                        } catch (NumberFormatException e1) {
                            e1.printStackTrace();
                            label.setText("连接建立失败，请检查IP或端口是否输入正确");
                        }
                    }

                    try {
                        Cert.getAESKey(socket);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    messagehandlerThread = new Thread(new MessageHandler(socket));
                    messagehandlerThread.start();

                    final JTextField jtf = ccui.getJtf1();

                    DataOutputStream dos = null;
                    try {
                        dos = new DataOutputStream(socket.getOutputStream());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    final DataOutputStream finalDos = dos;
                    jb1.setEnabled(true);
                    jb2.setEnabled(true);
                    jb2.setText("断开连接");
                    jb1.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (e.getActionCommand() == "发送") {
                                if (!jtf.getText().equals("")) {
                                    try {
                                        finalDos.writeUTF(AES.getInstance().encrypt(jtf.getText()));
                                        jtf.setText("");
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                } else if (e.getActionCommand() == "断开连接") {
                    messagehandlerThread.stop();
                    jb1.setEnabled(false);
                    jb2.setEnabled(false);

                    try {
                        socket.close();
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, "断开失败！请尝试重启客户端！");
                        e1.printStackTrace();
                    }
                    jb2.setEnabled(true);
                    jb2.setName("连接服务器");
                }
            }
        });

    }
}
