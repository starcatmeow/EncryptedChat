package top.starcatmeow.chat.server;

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

/**
 * Created by Dongruixuan Li on 2017/1/15.
 */
public class Cert implements Runnable {
    String[][] accountdatabase = {
            {"test", "testpassword"},
            {"test1", "testpassword1"},
            {"测试", "测试密码"}
    };
    Account[] accounts = new Account[accountdatabase.length];
    Client client;

    public Cert(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        initAccounts();
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            dis = new DataInputStream(client.getSocket().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dos = new DataOutputStream(client.getSocket().getOutputStream());
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
            username = RSA.getInstance().decrypt(dis.readUTF(), kp.getPrivate());
            password = RSA.getInstance().decrypt(dis.readUTF(), kp.getPrivate());
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
        if (isValid(new Account(username, password))) {
            try {
                dos.writeUTF(RSA.getInstance().encrypt("kf*MxU2|)+bsPCm:", pk1));
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
                client.makeAeskey();
                dos.writeUTF(RSA.getInstance().encrypt(client.getAeskey().getEncoded(), pk1));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Main.clients.add(client);
            MessageSender.Broadcast(username + " 已连接至服务器，目前有 " + (++Main.OnlineCount) + " 人在线");
            new Thread(new MessageHandler(client)).start();
        } else {
            try {
                dos.writeUTF(RSA.getInstance().encrypt(":N~n04$-rVhS=KxF", pk1));
                System.out.println(client.getIpandport() + "尝试登陆，已拒绝，所输入的账号为：" + username + "   密码为：" + password);
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

    private void initAccounts() {
        for (int count = 0; count < accountdatabase.length; count++) {
            accounts[count] = new Account(accountdatabase[count][0], accountdatabase[count][1]);
        }
    }

    private boolean isValid(Account account) {
        for (Account rightaccount : accounts) {
            if (rightaccount.equals(account))
                return true;
        }
        return false;
    }
}
