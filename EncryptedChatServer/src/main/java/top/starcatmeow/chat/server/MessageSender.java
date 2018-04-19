package top.starcatmeow.chat.server;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Dongruixuan Li on 2017/1/14.
 */
public class MessageSender {
    public static void Send(Client client, String msg) {
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(client.getSocket().getOutputStream());
            dos.writeUTF(client.getAes().encrypt(msg));
        } catch (IOException e) {
            System.out.println(getConsoleString.get("cannotsendmessage"));
            e.printStackTrace();
        }
    }

    public static void Broadcast(String str) {
        for (Client client : Main.clients) {
            Send(client, str);
        }
    }
}
