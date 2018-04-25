package top.starcatmeow.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Dongruixuan Li on 2017/1/14.
 */
public class MessageSender {
    static Logger loggerinfo = null, loggererror = null;

    //向指定客户端发送信息
    public static void Send(Client client, String msg) {
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(client.getSocket().getOutputStream());                                           //获取输出流
            dos.writeUTF(client.getAes().encrypt(msg));                                                                 //加密后发送给客户端
        } catch (IOException e) {
            loggererror.error(getConsoleString.get("cannotsendmessage"));                                               //写入错误日志
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
        }
    }

    public static void initLogger() {                                                                                   //初始化日志记录器
        loggerinfo = LogManager.getLogger(MessageSender.class);
        loggererror = LogManager.getLogger("errorslogger");
    }

    //广播信息给所有客户端
    public static void Broadcast(String str) {
        loggerinfo.info(str);                                                                                           //写入日志
        for (Client client : Main.clients) {                                                                            //遍历在线客户端列表
            Send(client, str);                                                                                          //发送信息
        }
    }
}
