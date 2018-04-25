package top.starcatmeow.chat.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RSAandAESTest {
    final int round = 10;
    RSA rsa = null;
    AES aes = null;
    KeyPair kp = null;
    PublicKey pubk = null;
    PrivateKey prik = null;
    SecretKey sk = null;
    String data = "KD-ChdBg+'?`ZaJ;#^r/gV\"MDcQ7;Lfe,)K\"1vaWX'=i8wcC`5";
    String data2="";
    long[] enctime,dectime,mktime;

    @Before
    public void init() throws Exception {
        Main.config = new Config(2048);
        rsa = RSA.getInstance();
        aes = new AES();
        kp = rsa.RSAKeyGen();
        sk = aes.AESKeygen();
        aes.setKey(sk);
        rsa.decrypt(rsa.encrypt(sk.getEncoded(),kp.getPublic()),kp.getPrivate());
        enctime = new long[round];
        dectime = new long[round];
        mktime = new long[round];
    }

    @Test
    public void testMakekey() throws Exception {
        long time;
        for(int a=0;a<round;a++){
            time = System.nanoTime();
            kp = rsa.RSAKeyGen();
            sk = aes.AESKeygen();
            rsa.decrypt(rsa.encrypt(sk.getEncoded(),kp.getPublic()),kp.getPrivate());
            time = System.nanoTime()-time;
            mktime[a] = time;
            System.out.println("r&a mk round "+(a+1)+" time:"+(float)(time)/1000000L+" ms");
        }
    }
    @Test
    public void testEncrypt(){
        long time;
        for(int a=0;a<round;a++){
            time = System.nanoTime();
            for (int i = 0; i < 1000; i++) {
                aes.encrypt(data);
            }
            time = System.nanoTime() - time;
            enctime[a] = time;
            System.out.println("r&a enc round " + (a + 1) + " time:" + (float) (time) / 1000000L + " ms");
        }
    }
    @Test
    public void testDecrypt(){
        long time;
        data2 = aes.encrypt(data);
        for(int a=0;a<round;a++){
            time = System.nanoTime();
            for (int i = 0; i < 1000; i++) {
                aes.decrypt(data2);
            }
            time = System.nanoTime() - time;
            dectime[a] = time;
            System.out.println("r&a dec round "+a+" time:"+ (float) (time) / 1000000L+" ms");
        }
    }

    @After
    public void calc(){
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
        if(enctotaltime!=0)System.out.println("r&a enc average time:"+(double)enctotaltime/round/1000000L+" ms");
        if(dectotaltime!=0)System.out.println("r&a dec average time:"+(double)dectotaltime/round/1000000L+" ms");
        if(mktotaltime!=0)System.out.println("r&a mk average time:"+(double)mktotaltime/round/1000000L+" ms");
    }
}
