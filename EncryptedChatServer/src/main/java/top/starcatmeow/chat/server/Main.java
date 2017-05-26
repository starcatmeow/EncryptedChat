package top.starcatmeow.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dongruixuan Li on 2017/1/14.
 */
public class Main {
    public static List<Client> clients = null;
    static ServerSocket ss = null;
    public static int OnlineCount = 0;

    public static void main(String[] args) {
        clients = new ArrayList<Client>();
        try {
            ss = new ServerSocket(2333);
            System.out.println("正在监听 2333 端口...");
        } catch (IOException e) {
            System.out.println("端口监听服务启动失败，错误：");
            e.printStackTrace();
        }
        while (true) {
            try {
                Socket tempsocket = ss.accept();
                new Thread(new Cert(new Client(tempsocket))).start();
            } catch (IOException e) {
                System.out.println("无法接受客户端的请求，错误：");
                e.printStackTrace();
            }
        }
    }
}
