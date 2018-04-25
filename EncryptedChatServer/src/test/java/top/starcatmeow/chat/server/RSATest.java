package top.starcatmeow.chat.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;

public class RSATest {
    final int round = 10;
    RSA rsa = null;
    KeyPair kp = null;
    PublicKey pubk = null;
    PrivateKey prik=null;
    String data = "KD-ChdBg+'?`ZaJ;#^r/gV\"MDcQ7;Lfe,)K\"1vaWX'=i8wcC`5";
    String data2="";
    long[] enctime,dectime,mktime;
    @Before
    public void RSATestinit() throws NoSuchAlgorithmException {
        Main.config = new Config(2048);
        rsa = RSA.getInstance();
        kp = rsa.RSAKeyGen();
        pubk = kp.getPublic();
        prik = kp.getPrivate();
        enctime = new long[round];
        dectime = new long[round];
        mktime=new long[round];
    }
    @Test
    public void testRSAMakekey(){
        long time;
        for(int a=0;a<round;a++){
            time = System.nanoTime();
            rsa.RSAKeyGen();
            time = System.nanoTime()-time;
            mktime[a] = time;
            System.out.println("rsa mk round "+(a+1)+" time:"+(float)(time)/1000000L+" ms");
        }
    }
    @Test
    public void testRSAEncrypt() throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, IOException, InvalidKeyException {
        long time;
        for (int a = 0; a < round; a++) {
            time = System.nanoTime();
            for (int i = 0; i < 1000; i++) {
                rsa.encrypt(data, pubk);
            }
            time = System.nanoTime() - time;
            enctime[a] = time;
            System.out.println("rsa enc round " + (a + 1) + " time:" + (float) (time) / 1000000L);
        }
    }
    @Test
    public void testRSADecrypt() throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, IOException, InvalidKeyException {
        long time;
        data2 = rsa.encrypt(data,pubk);
        for(int a=0;a<round;a++){
            time = System.nanoTime();
            for (int i = 0; i < 1000; i++) {
                rsa.decrypt(data2,prik);
            }
            time = System.nanoTime() - time;
            dectime[a] = time;
            System.out.println("rsa dec round "+a+" time:"+ (float) (time) / 1000000L);
        }
    }
    @After
    public void RSATestCalc(){
        long enctotaltime = 0,dectotaltime = 0,mktotaltime = 0;
        for(long l:enctime){
            enctotaltime += l;
        }
        for(long l:dectime){
            dectotaltime += l;
        }
        for(long l:mktime){
            mktotaltime += l;
        }
        if(enctotaltime!=0)System.out.println("rsa enc average time:"+(double)enctotaltime/round/1000000L+" ms");
        if(dectotaltime!=0)System.out.println("rsa dec average time:"+(double)dectotaltime/round/1000000L+" ms");
        if(mktotaltime!=0)System.out.println("rsa mk average time:"+(double)mktotaltime/round/1000000L+" ms");
    }
}
