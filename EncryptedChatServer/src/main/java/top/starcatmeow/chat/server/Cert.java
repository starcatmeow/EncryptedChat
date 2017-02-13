package top.starcatmeow.chat.server;

import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.*;

/**
 * Created by root on 2017/1/15.
 */
public class Cert implements Runnable{
    Socket socket = null;
    public Cert(Socket socket){this.socket = socket;}

    @Override
    public void run() {
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        KeyPair kp = RSA.getInstance().RSAKeyGen();
        try {
            dos.writeUTF(new BASE64Encoder().encode(kp.getPublic().getEncoded()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String username = null;
        String password = null;
        try {
            username = RSA.getInstance().decrypt(dis.readUTF(),kp.getPrivate());
            password = RSA.getInstance().decrypt(dis.readUTF(),kp.getPrivate());
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        PublicKey pk1 = null;
        try {
            String t = dis.readUTF();
            pk1 = RSA.getInstance().getPublicKey(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(username.equals("test") && password.equals("testpassword")){
            try {
                dos.writeUTF(RSA.getInstance().encrypt("kf*MxU2|)+bsPCm:",pk1));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
            try {
                dos.writeUTF(RSA.getInstance().encrypt(new byte[]{-13, -27, -99, 71, -42, -64, -28, -78, 19, -103, -126, 7, -34, 33, 61, 44},pk1));
            } catch (Exception e) {
                e.printStackTrace();
            }
            MessageSender.Send(socket,"已连接至服务器，目前有 "+(++Main.OnlineCount)+" 人在线");
            new Thread(new MessageHandler(socket)).start();
        }else{
            try {
                dos.writeUTF(RSA.getInstance().encrypt(":N~n04$-rVhS=KxF",pk1));
                System.out.println(socket.getInetAddress().getHostAddress()+":"+socket.getPort()+"尝试登陆，已拒绝，所输入的账号为："+username+"   密码为："+password);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        }
    }
}
