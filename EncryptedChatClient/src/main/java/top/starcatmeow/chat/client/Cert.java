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

/**
 * Created by Dongruixuan Li on 2017/1/30.
 */
public class Cert {
    public static void getAESKey(Socket socket) throws Exception {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        PublicKey pk1 = RSA.getInstance().getPublicKey(dis.readUTF());
        ChatClientUI.label.setText("正在从服务器取得密钥（2/5）");
        dos.writeUTF(RSA.getInstance().encrypt(JOptionPane.showInputDialog("请输入认证用户名："), pk1));
        dos.writeUTF(RSA.getInstance().encrypt(JOptionPane.showInputDialog("请输入认证密码："), pk1));
        ChatClientUI.label.setText("正在从服务器取得密钥（3/5）");
        KeyPair kp1 = RSA.getInstance().RSAKeyGen();
        PublicKey pk = kp1.getPublic();
        dos.writeUTF(new BASE64Encoder().encode(pk.getEncoded()));
        ChatClientUI.label.setText("正在从服务器取得密钥（4/5）");
        String report = RSA.getInstance().decrypt(dis.readUTF(), kp1.getPrivate());
        if (report.equals("kf*MxU2|)+bsPCm:")) {
            ChatClientUI.label.setText("正在从服务器取得密钥（5/5）");
            AES.getInstance().setKey(new SecretKeySpec(RSA.getInstance().decrypttobyte(dis.readUTF(), kp1.getPrivate()), "AES"));
            ChatClientUI.label.setText("正常");
        } else if (report.equals(":N~n04$-rVhS=KxF")) {
            ChatClientUI.label.setText("认证失败");
        } else {
            ChatClientUI.label.setText("服务器返回了不正确的数据，请检查版本是否匹配：" + report);
        }
    }

    public static void getAESKey() throws Exception {
        KeyPair kp1 = RSA.getInstance().RSAKeyGen();
        PublicKey pk = kp1.getPublic();
        String encodepubkey = new BASE64Encoder().encode(pk.getEncoded());
        String finalEncodepubkey = encodepubkey;
        SwingUtilities.invokeLater(() -> ChatClientUI.writetoosendjtf(finalEncodepubkey));

        String encryptedaeskey = ChatClientUI.readfromoreceivejtf();
        SwingUtilities.invokeLater(() -> ChatClientUI.label.setText("正在解密密钥"));
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
        SwingUtilities.invokeLater(() -> ChatClientUI.label.setText("正在生成密钥"));
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
