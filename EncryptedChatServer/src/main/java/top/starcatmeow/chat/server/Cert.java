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
        loggerinfo = LogManager.getLogger(Cert.class);
        loggererror = LogManager.getLogger("errorslogger");
    }

    @Override
    public void run() {
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            dis = new DataInputStream(client.getSocket().getInputStream());
        } catch (IOException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
        }
        try {
            dos = new DataOutputStream(client.getSocket().getOutputStream());
        } catch (IOException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
        }
        try {
            dos.writeInt(Main.config.fileconfig.rsastrength > 2048 ? Main.config.fileconfig.rsastrength : 2048);
        } catch (IOException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
        }
        KeyPair kp = RSA.getInstance().RSAKeyGen();
        try {
            dos.writeUTF(new BASE64Encoder().encode(kp.getPublic().getEncoded()));
        } catch (IOException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
        }
        String username = null;
        String password = null;
        try {
            username = RSA.getInstance().decrypt(dis.readUTF(), kp.getPrivate());
            password = RSA.getInstance().decrypt(dis.readUTF(), kp.getPrivate());
        } catch (NoSuchPaddingException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
        } catch (NoSuchAlgorithmException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
        } catch (InvalidKeyException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
        } catch (IOException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
        } catch (BadPaddingException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
        } catch (IllegalBlockSizeException e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
        }
        PublicKey pk1 = null;
        try {
            String t = dis.readUTF();
            pk1 = RSA.getInstance().getPublicKey(t);
        } catch (Exception e) {
            for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
        }
        if (Main.config.isValid(new Account(username, password))) {
            try {
                dos.writeUTF(RSA.getInstance().encrypt("kf*MxU2|)+bsPCm:", pk1));
            } catch (IOException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
            } catch (NoSuchPaddingException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
            } catch (NoSuchAlgorithmException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
            } catch (InvalidKeyException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
            } catch (BadPaddingException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
            } catch (IllegalBlockSizeException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
            }
            try {
                client.setAeskey(client.getAes().AESKeygen());
                dos.writeUTF(RSA.getInstance().encrypt(client.getAeskey().getEncoded(), pk1));
            } catch (Exception e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
            }
            client.setUsername(username);
            Main.clients.add(client);
            MessageSender.Broadcast(client.getUsername() + " (" + client.getIpandport() + ")" + " " + MessageFormat.format(getConsoleString.get("connected"), String.valueOf(++Main.OnlineCount)));
            new Thread(new MessageHandler(client)).start();
        } else {
            try {
                dos.writeUTF(RSA.getInstance().encrypt(":N~n04$-rVhS=KxF", pk1));
                loggerinfo.warn(client.getIpandport() + MessageFormat.format(getConsoleString.get("refused"), username, password));
            } catch (IOException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
            } catch (NoSuchPaddingException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
            } catch (NoSuchAlgorithmException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
            } catch (InvalidKeyException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
            } catch (BadPaddingException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
            } catch (IllegalBlockSizeException e) {
                for (StackTraceElement ste : e.getStackTrace()) loggererror.error(ste.toString());
            }
        }
    }
}
