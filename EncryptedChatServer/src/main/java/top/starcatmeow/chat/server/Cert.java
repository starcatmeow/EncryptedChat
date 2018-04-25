package top.starcatmeow.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.text.MessageFormat;

/**
 * Created by Dongruixuan Li on 2017/1/15.
 */
public class Cert implements Runnable {
    Client client;
    Logger loggerinfo, loggererror;
    public Cert(Client client) {
        this.client = client;
        loggerinfo = LogManager.getLogger(Cert.class);                                                                  //初始化日志记录器
        loggererror = LogManager.getLogger("errorslogger");
    }

    @Override
    public void run() {
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            dis = new DataInputStream(client.getSocket().getInputStream());                                             //获取输入流
        } catch (IOException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                          //写入错误日志
        }
        try {
            dos = new DataOutputStream(client.getSocket().getOutputStream());                                           //获取输出流
        } catch (IOException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                          //写入错误日志
        }
        try {
            dos.writeInt(Main.config.fileconfig.rsastrength > 2048 ? Main.config.fileconfig.rsastrength : 2048);        //发送服务端配置文件中要求的RSA密钥强度（采用三元运算符保证强度不低于2048）
        } catch (IOException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                          //写入错误日志
        }
        KeyPair kp = RSA.getInstance().RSAKeyGen();                                                                     //创建一对RSA密钥
        try {
            dos.writeUTF(new BASE64Encoder().encode(kp.getPublic().getEncoded()));                                      //发送公钥以安全接受客户端认证信息
        } catch (IOException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                          //写入错误日志
        }
        String username = null;
        String password = null;
        try {
            username = RSA.getInstance().decrypt(dis.readUTF(), kp.getPrivate());                                       //获取客户端认证信息
            password = RSA.getInstance().decrypt(dis.readUTF(), kp.getPrivate());
        } catch (NoSuchPaddingException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                          //写入错误日志
        } catch (NoSuchAlgorithmException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                          //写入错误日志
        } catch (InvalidKeyException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                          //写入错误日志
        } catch (IOException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                          //写入错误日志
        } catch (BadPaddingException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                          //写入错误日志
        } catch (IllegalBlockSizeException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                          //写入错误日志
        }
        PublicKey pk1 = null;
        try {
            String t = dis.readUTF();
            pk1 = RSA.getInstance().getPublicKey(t);                                                                    //获取客户端发送的公钥
        } catch (Exception e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                          //写入错误日志
        }
        if (Main.config.isValid(new Account(username, password))) {
            try {
                dos.writeUTF(RSA.getInstance().encrypt("kf*MxU2|)+bsPCm:", pk1));                                  //返回成功信息（形似乱码以防止抓包者获取有用数据）
            } catch (IOException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                      //写入错误日志
            } catch (NoSuchPaddingException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                      //写入错误日志
            } catch (NoSuchAlgorithmException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                      //写入错误日志
            } catch (InvalidKeyException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                      //写入错误日志
            } catch (BadPaddingException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                      //写入错误日志
            } catch (IllegalBlockSizeException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                      //写入错误日志
            }
            try {
                client.setAeskey(client.getAes().AESKeygen());                                                          //为此客户端生成独自的AES密钥
                dos.writeUTF(RSA.getInstance().encrypt(client.getAeskey().getEncoded(), pk1));                          //将此AES密钥加密后发给客户端
            } catch (Exception e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                      //写入错误日志
            }
            client.setUsername(username);                                                                               //设置客户端的用户名
            Main.clients.add(client);                                                                                   //将客户端加入在线客户端列表中
            MessageSender.Broadcast(client.getUsername() + " (" + client.getIpandport() + ")" + " " + MessageFormat.format(getConsoleString.get("connected"), String.valueOf(++Main.OnlineCount)));
            //广播上线信息
            new Thread(new MessageHandler(client)).start();                                                             //新建此客户端的信息处理线程
        } else {
            try {
                dos.writeUTF(RSA.getInstance().encrypt(":N~n04$-rVhS=KxF", pk1));                                  //返回失败信息（形似乱码以防止抓包者获取有用数据）
                loggerinfo.warn(client.getIpandport() + MessageFormat.format(getConsoleString.get("refused"), username, password));
                //向日志文件中写入客户端输入的错误账户，以便管理员判断存在暴力破解的情况，采取后续措施
            } catch (IOException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                      //写入错误日志
            } catch (NoSuchPaddingException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                      //写入错误日志
            } catch (NoSuchAlgorithmException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                      //写入错误日志
            } catch (InvalidKeyException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                      //写入错误日志
            } catch (BadPaddingException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                      //写入错误日志
            } catch (IllegalBlockSizeException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());                      //写入错误日志
            }
        }
    }
}
