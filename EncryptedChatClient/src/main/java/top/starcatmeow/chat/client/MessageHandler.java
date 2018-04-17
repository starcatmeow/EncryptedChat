package top.starcatmeow.chat.client;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Dongruixuan Li on 2017/1/30.
 */
public class MessageHandler extends Thread {
    Socket socket = null;
    JTextArea jta = Main.ui.jta1;
    boolean isSocket;

    public MessageHandler(Socket socket) {
        isSocket = true;
        this.socket = socket;
    }

    public MessageHandler() {
        isSocket = false;
    }

    @Override
    public void run() {
        if (isSocket) {
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                while (true) {
                    jta.append(AES.getInstance().decrypt(dis.readUTF()) + "\n");
                    jta.setCaretPosition(jta.getText().length());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            while (true) {
                jta.append(AES.getInstance().decrypt(ChatClientUI.readfromoreceivejtf()) + "\n");
                jta.setCaretPosition(jta.getText().length());
            }
        }
    }

}
