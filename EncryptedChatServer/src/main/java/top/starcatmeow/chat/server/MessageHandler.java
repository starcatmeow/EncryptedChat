package top.starcatmeow.chat.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.MessageFormat;

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
                temp = MessageFormat.format(getConsoleString.get("message"), client.getUsername(), client.getIpandport(), temp);
                MessageSender.Broadcast(temp);
            }
        } catch (IOException e) {
            Main.clients.remove(client);
            MessageSender.Broadcast(client.getUsername() + " (" + client.getIpandport() + ")" + " " + MessageFormat.format(getConsoleString.get("disconnect"), String.valueOf(--Main.OnlineCount)));
        }
    }
}
