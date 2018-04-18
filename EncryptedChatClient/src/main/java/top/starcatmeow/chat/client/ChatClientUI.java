package top.starcatmeow.chat.client;

import sun.misc.Lock;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.Socket;

/**
 * Created by Dongruixuan Li on 2017/1/30.
 */
public class ChatClientUI extends JFrame implements Runnable {
    static JPanel jp1 = null, jp2 = null, jp3 = null, jp4 = null, jp5 = null, jp6 = null, jp7 = null;
    static JTextArea jta1 = null;
    static JScrollPane jsp1 = null;
    static JTextField jtf1 = null;
    static JButton jb1 = null, jb2 = null, jb3 = null;
    static JCheckBox jcb1 = null;
    static Socket socket = null;
    static JLabel label = null;
    static ChatClientUI ccui = null;

    static boolean inOther = false;
    static String receiveBuffer = null;
    static JPanel oPanel = new JPanel(), oreceivePanel = new JPanel(), osendPanel = new JPanel();
    static JTextField oreceiveJtf = new JTextField(), osendJtf = new JTextField();
    static JButton oReceive = new JButton("确认"), osend = new JButton("拷贝");
    static Lock readLock = new Lock();

    protected static ActionProcesser ap = new ActionProcesser();

    public static String readfromoreceivejtf() {
        while (receiveBuffer == null) {
            try {
                Thread.sleep(100);                                                                                    //休眠防止卡死
            } catch (InterruptedException e) {
            }
        }

        String temp = receiveBuffer;
        temp = temp.replace(' ', '\n');
        receiveBuffer = null;
        return temp;
    }

    public static void writetoosendjtf(String str) {
        osendJtf.setText(str.replace('\n', ' '));
        osend.setEnabled(true);
        osend.setText("拷贝");
    }

    public ChatClientUI() {
        label = new JLabel("");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        jta1 = new JTextArea("");
        jta1.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));
        jta1.setEditable(false);
        jsp1 = new JScrollPane(jta1);
        jsp1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jtf1 = new JTextField("");
        jtf1.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyChar() == KeyEvent.VK_ENTER && (!jtf1.getText().equals(""))) {
                    jb1.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        jb1 = new JButton("发送");
        jb1.setEnabled(false);
        jtf1.setEnabled(false);
        osendJtf.setEnabled(false);
        osend.setEnabled(false);
        jb2 = new JButton("连接服务器");
        jb3 = new JButton("其它连接模式");
        jp1 = new JPanel();
        jp2 = new JPanel();
        jp3 = new JPanel();
        jp4 = new JPanel();
        jp5 = new JPanel();
        jp6 = new JPanel();
        jp7 = new JPanel();

        jp4.setLayout(new BorderLayout(5, 5));
        jp4.add("Center", jtf1);
        jp4.add("East", jb1);

        jp3.setLayout(new BorderLayout(5, 5));
        jp3.add("Center", jsp1);
        jp3.add("South", jp4);
        jp3.setBorder(BorderFactory.createTitledBorder("聊天"));

        jp2.setLayout(new BorderLayout(5, 5));
        jp2.add(label);
        jp2.setBorder(BorderFactory.createTitledBorder("状态"));

        jp5.setLayout(new GridLayout(1, 2));
        jp5.add(jb2);
        jp5.add(jb3);
        jp5.setBorder(BorderFactory.createTitledBorder("操作"));

        jp6.setLayout(new BorderLayout(5, 5));
        jp6.add("Center", jp2);
        jp6.add("East", jp5);

        jp7.setLayout(new BorderLayout(5, 5));
        jp7.add("North", jp6);

        jp1.setLayout(new BorderLayout(0, 0));
        jp1.add("North", jp7);
        jp1.add("Center", jp3);
        setLayout(null);
        setContentPane(jp1);
    }


    @Override
    public void run() {
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
        label.setText("等待用户操作");

        oreceiveJtf.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyChar() == KeyEvent.VK_ENTER && (!oreceiveJtf.getText().equals(""))) {
                    oReceive.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        jb2.addActionListener(e -> new Thread(() -> ap.actionPerformed(e)).start());
        jb3.addActionListener(e -> new Thread(() -> ap.actionPerformed(e)).start());
        osend.addActionListener(e -> new Thread(() -> ap.actionPerformed(e)).start());
        oReceive.addActionListener(e -> new Thread(() -> ap.actionPerformed(e)).start());
    }
}
