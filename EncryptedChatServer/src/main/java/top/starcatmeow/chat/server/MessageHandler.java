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
            DataInputStream dis = new DataInputStream(client.getSocket().getInputStream());                             //获取输入流
            while (true) {
                String temp = client.getAes().decrypt(dis.readUTF());                                                   //读取客户端信息
                temp = MessageFormat.format(getConsoleString.get("message"), client.getUsername(), client.getIpandport(), temp);
                //格式化信息
                MessageSender.Broadcast(temp);                                                                          //广播信息
            }
        } catch (IOException e) {
            //IO出错即代表客户端掉线
            Main.clients.remove(client);                                                                                //从在线客户端列表中移除
            MessageSender.Broadcast(client.getUsername() + " (" + client.getIpandport() + ")" + " " + MessageFormat.format(getConsoleString.get("disconnect"), String.valueOf(--Main.OnlineCount)));
            //广播下线信息
        }
    }
}
