package top.starcatmeow.chat.client.biz;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Dongruixuan Li on 2017/1/30.
 */
public class MessageHandler implements Runnable {
    Socket socket = null;
    JTextArea jta = Main.ccui.getJta1();

    public MessageHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            while (true) {
                jta.append(AES.getInstance().decrypt(dis.readUTF()) + "\n");
                jta.setCaretPosition(jta.getText().length());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
