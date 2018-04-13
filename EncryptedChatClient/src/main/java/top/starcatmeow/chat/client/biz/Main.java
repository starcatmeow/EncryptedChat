package top.starcatmeow.chat.client.biz;

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

    public static void main(String[] args) {
        try {
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception e) {
            //TODO exception
        }
        ccui = new ChatClientUI();
        ccui.setDefaultLookAndFeelDecorated(true);
        ccui.pack();
        ccui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ccui.setSize(800, 600);
        ccui.setLocationRelativeTo(null);
        ccui.setTitle("聊天系统客户端");
        ccui.setVisible(true);

        label = ccui.getJl1();
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
            } catch (IOException e) {
                e.printStackTrace();
                label.setText("连接建立失败，重新输入服务器信息");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                label.setText("连接建立失败，重新输入服务器信息");
            }
        }

        try {
            Cert.getAESKey(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(new MessageHandler(socket)).start();

        final JTextField jtf = ccui.getJtf1();
        JButton jb = ccui.getJb1();
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        final DataOutputStream finalDos = dos;
        jb.addActionListener(new ActionListener() {

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
    }
}
