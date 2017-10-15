package top.starcatmeow.chat.client.biz;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Dongruixuan Li on 2017/1/30.
 */
public class RSA {
    static RSA rsa = null;
    Cipher cipher = null;
    KeyPairGenerator kpg = null;

    private RSA() {

    }

    protected PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    protected PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    protected String encrypt(String str, PublicKey pk) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pk);
        return new BASE64Encoder().encode(cipher.doFinal(str.getBytes("utf-8")));
    }

    protected String encrypt(String str, String publickey) throws Exception {
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publickey));
        return new BASE64Encoder().encode(cipher.doFinal(str.getBytes("utf-8")));
    }

    protected String encrypt(byte[] data, PublicKey publickey) throws Exception {
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publickey);
        return new BASE64Encoder().encode(cipher.doFinal(data));
    }

    protected String decrypt(String str, PrivateKey pk) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, pk);
        return new String(cipher.doFinal(new BASE64Decoder().decodeBuffer(str)), "utf-8");
    }

    protected String decrypt(String str, String privatekey) throws Exception {
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privatekey));
        return new String(cipher.doFinal(new BASE64Decoder().decodeBuffer(str)), "utf-8");
    }

    protected byte[] decrypttobyte(String str, PrivateKey pk) throws Exception {
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, pk);
        return cipher.doFinal(new BASE64Decoder().decodeBuffer(str));
    }

    protected KeyPair RSAKeyGen() {
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        kpg.initialize(2048);
        return kpg.genKeyPair();
    }

    public static RSA getInstance() {
        if (rsa == null) {
            return rsa = new RSA();
        } else {
            return rsa;
        }
    }
}
