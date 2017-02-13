package top.starcatmeow.chat.client.biz;

import sun.misc.BASE64Encoder;

import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

public class Cert {
    public static void getAESKey(Socket socket) throws Exception {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        PublicKey pk1 = RSA.getInstance().getPublicKey(dis.readUTF());
        Main.label.setText("正在从服务器取得密钥（2/5）");
        dos.writeUTF(RSA.getInstance().encrypt(JOptionPane.showInputDialog("请输入认证用户名："), pk1));
        dos.writeUTF(RSA.getInstance().encrypt(JOptionPane.showInputDialog("请输入认证密码："), pk1));
        Main.label.setText("正在从服务器取得密钥（3/5）");
        KeyPair kp1 = RSA.getInstance().RSAKeyGen();
        PublicKey pk = kp1.getPublic();
        dos.writeUTF(new BASE64Encoder().encode(pk.getEncoded()));
        Main.label.setText("正在从服务器取得密钥（4/5）");
        String report = RSA.getInstance().decrypt(dis.readUTF(), kp1.getPrivate());
        if (report.equals("kf*MxU2|)+bsPCm:")) {
            Main.label.setText("正在从服务器取得密钥（5/5）");
            AES.getInstance().setKey(new SecretKeySpec(RSA.getInstance().decrypttobyte(dis.readUTF(), kp1.getPrivate()), "AES"));
            Main.label.setText("正常");
        } else if (report.equals(":N~n04$-rVhS=KxF")) {
            Main.label.setText("认证失败");
        } else {
            Main.label.setText("服务器返回了不正确的数据，请检查版本是否匹配：" + report);
        }
    }
}
