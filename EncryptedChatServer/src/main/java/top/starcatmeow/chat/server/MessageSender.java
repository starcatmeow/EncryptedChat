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
    public static void Send(Client client, String msg) {
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(client.getSocket().getOutputStream());
            dos.writeUTF(client.getAes().encrypt(msg));
        } catch (IOException e) {
            loggererror.error(getConsoleString.get("cannotsendmessage"));
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
        }
    }

    public static void initLogger() {
        loggerinfo = LogManager.getLogger(MessageSender.class);
        loggererror = LogManager.getLogger("errorslogger");
    }
    public static void Broadcast(String str) {
        loggerinfo.info(str);
        for (Client client : Main.clients) {
            Send(client, str);
        }
    }
}
