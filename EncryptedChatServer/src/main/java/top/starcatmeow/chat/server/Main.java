package top.starcatmeow.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dongruixuan Li on 2017/1/14.
 */
public class Main {
    static Logger loggerinfo = LogManager.getLogger(Main.class);
    static Logger loggererror = LogManager.getLogger("errorslogger");
    public static List<Client> clients = null;
    static ServerSocket ss = null;
    public static int OnlineCount = 0;
    public static Config config = null;
    public static void main(String[] args) {
        getConsoleString.init();
        config = new Config();
        clients = new ArrayList<Client>();
        MessageSender.initLogger();
        try {
            ss = new ServerSocket(config.fileconfig.servicePort);
            loggerinfo.info(MessageFormat.format(getConsoleString.get("listeningPort"), String.valueOf(config.fileconfig.servicePort)));
        } catch (IOException e) {
            loggererror.error(getConsoleString.get("cannotlistenPort"));
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
        }
        while (true) {
            try {
                Socket tempsocket = ss.accept();
                new Thread(new Cert(new Client(tempsocket))).start();
            } catch (IOException e) {
                loggererror.error(getConsoleString.get("cannotacceptRequest"));
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
            }
        }
    }
}
