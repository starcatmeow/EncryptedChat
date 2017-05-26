package top.starcatmeow.chat.server;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Dongruixuan Li on 2017/1/14.
 */
public class MessageHandler implements Runnable {
    Client client;
    String ip;
    String port;

    public MessageHandler(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(client.getSocket().getInputStream());
            while (true) {
                String temp = client.getAes().decrypt(dis.readUTF());
                temp = client.getIpandport() + " 说 " + temp;
                MessageSender.Broadcast(temp);
            }
        } catch (IOException e) {
            Main.clients.remove(client.getSocket());
            MessageSender.Broadcast(client.getIpandport() + " 已下线！现在有 " + (--Main.OnlineCount) + " 人在线");
        }
    }
}
