package top.starcatmeow.chat.client.biz;

import sun.misc.BASE64Encoder;
import sun.plugin2.message.Message;
import top.starcatmeow.chat.client.ui.ChatClientUI;

import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

/**
 * Created by Dongruixuan Li on 2017/1/30.
 */
public class Main {
    static Socket socket = null;
    static JLabel label = null;
    static ChatClientUI ccui = null;
    static String receiveBuffer = null;
    private static Thread messagehandlerThread = null;
    static JPanel oPanel = new JPanel(), oreceivePanel = new JPanel(), osendPanel = new JPanel();
    static JTextField oreceiveJtf = new JTextField(), osendJtf = new JTextField();
    static JButton oReceive = new JButton("确认"), osend = new JButton("拷贝");

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
                    jb2.setText("连接服务器");
                    label.setText("等待用户操作");
                }
            }
        });
        jb3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

                    ccui.getJp7().add("Center", oPanel);                                                           //加入其它连接模式需要的Panel
                    ccui.revalidate();                                                                                  //重绘界面
                    jb2.setEnabled(false);                                                                              //处理后续界面响应
                    jb3.setText("退出此模式");

                    oReceive.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (e.getActionCommand() == "确认") {
                                if (!oreceiveJtf.getText().equals("")) {
                                    receiveBuffer = oreceiveJtf.getText();
                                    oreceiveJtf.setText("");
                                }
                            }
                        }
                    });

                    label.setText("等待用户选择角色");
                    Object[] options = {"发起方", "接收方"};
                    int result = JOptionPane.showOptionDialog(null, "请选择你的角色", "选择角色", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                    //System.out.println(result);                                                                         //Debug！！！
                    if (result == 0) {

                    } else if (result == 1) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Cert.getAESKey();
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }).start();


                        messagehandlerThread = new Thread(new MessageHandler());
                        messagehandlerThread.start();

                        final JTextField jtf = ccui.getJtf1();

                        jb1.setEnabled(true);
                        jb1.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (e.getActionCommand() == "发送") {
                                    if (!jtf.getText().equals("")) {
                                        osendJtf.setText(AES.getInstance().encrypt(jtf.getText()));
                                        jtf.setText("");
                                    }
                                }
                            }
                        });
                    }
                } else if (e.getActionCommand() == "退出此模式") {
                    ccui.getJp7().remove(1);
                    ccui.revalidate();
                    jb2.setEnabled(true);
                    jb3.setText("其他连接模式");
                    label.setText("等待用户操作");
                }
            }
        });
    }

    public static String readfromOtherConnect() {
        while (receiveBuffer == null) ;
        String temp = receiveBuffer;
        receiveBuffer = null;
        return temp;
    }
}
