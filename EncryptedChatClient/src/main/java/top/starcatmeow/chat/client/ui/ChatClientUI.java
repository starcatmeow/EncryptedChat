package top.starcatmeow.chat.client.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Dongruixuan Li on 2017/1/30.
 */
public class ChatClientUI extends JFrame {
    private JPanel jp1 = null, jp2 = null, jp3 = null, jp4 = null, jp5 = null, jp6 = null;
    private JLabel jl1 = null;
    private JTextArea jta1 = null;
    private JScrollPane jsp1 = null;
    private JTextField jtf1 = null;
    private JButton jb1 = null, jb2 = null, jb3 = null;
    private JCheckBox jcb1 = null;

    public ChatClientUI() {
        jl1 = new JLabel("");
        jl1.setHorizontalAlignment(SwingConstants.CENTER);
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
        jb2 = new JButton("连接服务器");
        jb3 = new JButton("使用其它连接");
        jp1 = new JPanel();
        jp2 = new JPanel();
        jp3 = new JPanel();
        jp4 = new JPanel();
        jp5 = new JPanel();
        jp6 = new JPanel();

        jp4.setLayout(new BorderLayout(5, 5));
        jp4.add("Center", jtf1);
        jp4.add("East", jb1);

        jp3.setLayout(new BorderLayout(5, 5));
        jp3.add("Center", jsp1);
        jp3.add("South", jp4);
        jp3.setBorder(BorderFactory.createTitledBorder("聊天"));

        jp2.setLayout(new BorderLayout(5, 5));
        jp2.add(jl1);
        jp2.setBorder(BorderFactory.createTitledBorder("状态"));

        jp5.setLayout(new BorderLayout(5, 5));
        jp5.add("West", jb2);
        jp5.add("East", jb3);
        jp5.setBorder(BorderFactory.createTitledBorder("操作"));

        jp6.setLayout(new BorderLayout(5, 5));
        jp6.add("Center", jp2);
        jp6.add("East", jp5);

        jp1.setLayout(new BorderLayout(0, 0));
        jp1.add("North", jp6);
        jp1.add("Center", jp3);
        setLayout(null);
        setContentPane(jp1);
    }

    public JLabel getJl1() {
        return jl1;
    }

    public JTextArea getJta1() {
        return jta1;
    }

    public JTextField getJtf1() {
        return jtf1;
    }

    public JButton getJb1() {
        return jb1;
    }

    public JButton getJb2() {
        return jb2;
    }

    public JButton getJb3() {
        return jb3;
    }
}
