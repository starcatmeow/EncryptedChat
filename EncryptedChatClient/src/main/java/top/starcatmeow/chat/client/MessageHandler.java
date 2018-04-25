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
                DataInputStream dis = new DataInputStream(socket.getInputStream());                                     //获取输入流
                while (true) {
                    jta.append(AES.getInstance().decrypt(dis.readUTF()) + "\n");                                        //解密服务器发送的信息并追加至聊天框末尾
                    jta.setCaretPosition(jta.getText().length());                                                       //将光标设置到聊天框末尾，实现滚动效果
                }
            } catch (IOException e) {
                e.printStackTrace();                                                                                    //显示错误信息
            }
        } else {
            while (true) {
                String encryptedchat = ChatClientUI.readfromoreceivejtf();                                              //从加密信息输入框中读取信息
                String decryptedchat = AES.getInstance().decrypt(encryptedchat);                                        //解密信息
                jta.append(getUIString.get("othersay") + " " + decryptedchat + "\n");                                   //追加至聊天框末尾
                jta.setCaretPosition(jta.getText().length());                                                           //将光标设置到聊天框末尾，实现滚动效果
            }
        }
    }

}
