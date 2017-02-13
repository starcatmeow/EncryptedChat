package top.starcatmeow.chat.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by root on 2017/1/14.
 */
public class MessageHandler implements Runnable{
    Socket socket;
    String ip;
    String port;
    public MessageHandler(Socket socket){
        this.socket=socket;
        ip=socket.getInetAddress().getHostAddress();
        port=Integer.toString(socket.getPort());
        MessageSender.Broadcast(ip+":"+port+" 已上线！现在有 "+ Main.OnlineCount+" 人在线。");
        Main.sockets.add(socket);
    }
    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            while(true){
                String temp = AES.getInstance().decrypt(dis.readUTF());
                temp = socket.getInetAddress().getHostAddress()+":"+Integer.toString(socket.getPort())+" 说 "+temp;
                MessageSender.Broadcast(temp);
            }
        } catch (IOException e) {
            Main.sockets.remove(socket);
            MessageSender.Broadcast(ip+":"+port+" 已下线！现在有 "+(--Main.OnlineCount)+" 人在线");
        }
    }
}
