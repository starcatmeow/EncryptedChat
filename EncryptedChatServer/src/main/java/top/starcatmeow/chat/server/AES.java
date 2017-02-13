package top.starcatmeow.chat.server;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by root on 2017/1/15.
 */
public class AES {
    private static AES aes = null;
    private SecretKey sk = null;
    private Cipher cipher;
    private AES(){
        sk = new SecretKeySpec(new byte[]{-13, -27, -99, 71, -42, -64, -28, -78, 19, -103, -126, 7, -34, 33, 61, 44},"aes");
    }
    protected String encrypt(String str){
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,sk);
            return new BASE64Encoder().encode(cipher.doFinal(str.getBytes("utf-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    protected String decrypt(String str){
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,sk);
            return new String(cipher.doFinal(new BASE64Decoder().decodeBuffer(str)),"utf-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static AES getInstance(){
        if(aes == null){
            return aes=new AES();
        }else{
            return aes;
        }
    }
}
