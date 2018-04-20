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
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        RSA.getInstance().strength = dis.readInt();
        PublicKey pk1 = RSA.getInstance().getPublicKey(dis.readUTF());
        ChatClientUI.label.setText(MessageFormat.format(getUIString.get("getkey"), String.valueOf(2)));
        dos.writeUTF(RSA.getInstance().encrypt(JOptionPane.showInputDialog(getUIString.get("enterusername")), pk1));
        dos.writeUTF(RSA.getInstance().encrypt(JOptionPane.showInputDialog(getUIString.get("enterpassword")), pk1));
        ChatClientUI.label.setText(MessageFormat.format(getUIString.get("getkey"), String.valueOf(3)));
        KeyPair kp1 = RSA.getInstance().RSAKeyGen();
        PublicKey pk = kp1.getPublic();
        dos.writeUTF(new BASE64Encoder().encode(pk.getEncoded()));
        ChatClientUI.label.setText(MessageFormat.format(getUIString.get("getkey"), String.valueOf(4)));
        String report = RSA.getInstance().decrypt(dis.readUTF(), kp1.getPrivate());
        if (report.equals("kf*MxU2|)+bsPCm:")) {
            ChatClientUI.label.setText(MessageFormat.format(getUIString.get("getkey"), String.valueOf(5)));
            AES.getInstance().setKey(new SecretKeySpec(RSA.getInstance().decrypttobyte(dis.readUTF(), kp1.getPrivate()), "AES"));
            ChatClientUI.label.setText(getUIString.get("normal"));
        } else if (report.equals(":N~n04$-rVhS=KxF")) {
            ChatClientUI.label.setText(getUIString.get("authfail"));
            return false;

        } else {
            ChatClientUI.label.setText(getUIString.get("authbug") + report);
            return false;
        }
        return true;
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
