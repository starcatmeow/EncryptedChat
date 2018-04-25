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
        getConsoleString.init();                                                                                        //初始化多语言支持
        config = new Config();                                                                                          //初始化配置文件
        clients = new ArrayList<Client>();                                                                              //新建在线用户列表
        MessageSender.initLogger();                                                                                     //初始化MessageSender中的日志记录器
        try {
            ss = new ServerSocket(config.fileconfig.servicePort);                                                       //监听配置文件中的端口
            loggerinfo.info(MessageFormat.format(getConsoleString.get("listeningPort"), String.valueOf(config.fileconfig.servicePort)));
            //写入日志
        } catch (IOException e) {
            loggererror.error(getConsoleString.get("cannotlistenPort"));                                                //写入错误日志
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
        }
        while (true) {
            try {
                Socket tempsocket = ss.accept();                                                                        //当接收到Socket请求就接受
                new Thread(new Cert(new Client(tempsocket))).start();                                                   //转交新线程中的处理模块处理，避免堵塞下一个客户端连接
            } catch (IOException e) {
                loggererror.error(getConsoleString.get("cannotacceptRequest"));                                         //写入错误日志
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
            }
        }
    }
}
