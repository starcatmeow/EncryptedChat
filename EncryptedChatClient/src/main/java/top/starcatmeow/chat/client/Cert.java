package top.starcatmeow.chat.client;

import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.text.MessageFormat;

/**
 * Created by Dongruixuan Li on 2017/1/30.
 */
public class Cert {
    public static boolean getAESKey(Socket socket) throws Exception {
        DataInputStream dis = new DataInputStream(socket.getInputStream());                                             //获取输入流
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());                                          //获取输出流
        RSA.getInstance().strength = dis.readInt();                                                                     //从服务端获取RSA密钥强度
        PublicKey pk1 = RSA.getInstance().getPublicKey(dis.readUTF());                                                  //从服务端获取公钥
        ChatClientUI.label.setText(MessageFormat.format(getUIString.get("getkey"), String.valueOf(2)));                 //更新UI上状态信息
        dos.writeUTF(RSA.getInstance().encrypt(JOptionPane.showInputDialog(getUIString.get("enterusername")), pk1));    //弹出用户名输入框，加密后发送至服务端
        dos.writeUTF(RSA.getInstance().encrypt(JOptionPane.showInputDialog(getUIString.get("enterpassword")), pk1));    //弹出密码输入框，加密后发送至服务端
        ChatClientUI.label.setText(MessageFormat.format(getUIString.get("getkey"), String.valueOf(3)));                 //更新UI上状态信息
        KeyPair kp1 = RSA.getInstance().RSAKeyGen();                                                                    //生成RSA密钥对
        PublicKey pk = kp1.getPublic();
        dos.writeUTF(new BASE64Encoder().encode(pk.getEncoded()));                                                      //发送公钥至服务端
        ChatClientUI.label.setText(MessageFormat.format(getUIString.get("getkey"), String.valueOf(4)));                 //更新UI上状态信息
        String report = RSA.getInstance().decrypt(dis.readUTF(), kp1.getPrivate());                                     //获取认证结果
        if (report.equals("kf*MxU2|)+bsPCm:")) {
            ChatClientUI.label.setText(MessageFormat.format(getUIString.get("getkey"), String.valueOf(5)));             //更新UI上状态信息
            AES.getInstance().setKey(new SecretKeySpec(RSA.getInstance().decrypttobyte(dis.readUTF(), kp1.getPrivate()), "AES"));
            //从服务端获取AES密钥
            ChatClientUI.label.setText(getUIString.get("normal"));                                                      //更新UI上状态信息
        } else if (report.equals(":N~n04$-rVhS=KxF")) {
            ChatClientUI.label.setText(getUIString.get("authfail"));                                                    //更新UI上状态信息
            return false;                                                                                               //返回失败结果

        } else {
            ChatClientUI.label.setText(getUIString.get("authbug") + report);                                            //更新UI上状态信息
            return false;                                                                                               //返回失败结果
        }
        return true;                                                                                                    //返回成功结果
    }

    public static void getAESKey() throws Exception {
        KeyPair kp1 = RSA.getInstance().RSAKeyGen();
        PublicKey pk = kp1.getPublic();
        String encodepubkey = new BASE64Encoder().encode(pk.getEncoded());
        String finalEncodepubkey = encodepubkey;
        SwingUtilities.invokeLater(() -> ChatClientUI.writetoosendjtf(finalEncodepubkey));

        String encryptedaeskey = ChatClientUI.readfromoreceivejtf();
        SwingUtilities.invokeLater(() -> ChatClientUI.label.setText(getUIString.get("decryptkey")));
        AES.getInstance().setKey(new SecretKeySpec(RSA.getInstance().decrypttobyte(encryptedaeskey, kp1.getPrivate()), "AES"));
    }

    public static void makeandsendAESKey() {
        String encodepubkey = ChatClientUI.readfromoreceivejtf();
        PublicKey pk = null;
        try {
            pk = RSA.getInstance().getPublicKey(encodepubkey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> ChatClientUI.label.setText(getUIString.get("genkey")));
        SecretKey sk = null;

        try {
            sk = AES.getInstance().AESKeygen();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        AES.getInstance().setKey(sk);
        String encryptedaeskey = null;
        try {
            encryptedaeskey = RSA.getInstance().encrypt(sk.getEncoded(), pk);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String finalEncryptedaeskey = encryptedaeskey;
        SwingUtilities.invokeLater(() -> ChatClientUI.writetoosendjtf(finalEncryptedaeskey));

    }
}
