package top.starcatmeow.chat.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Dongruixuan Li on 2017/1/14.
 */
public class MessageSender {
    public static void Send(Socket socket, String msg) {
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(AES.getInstance().encrypt(msg));
        } catch (IOException e) {
            System.out.println("信息发送失败，错误：");
            e.printStackTrace();
        }
    }

    public static void Broadcast(String str) {
        for (Socket s : Main.sockets) {
            Send(s, str);
        }
    }
}
