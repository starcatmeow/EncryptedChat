package top.starcatmeow.chat.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class AESTest {
    final int round = 100;
    AES aes = null;
    SecretKey sk = null;
    String data = "KD-ChdBg+'?`ZaJ;#^r/gV\"MDcQ7;Lfe,)K\"1vaWX'=i8wcC`5";
    String data2="";
    long[] enctime,dectime,mktime;
    @Before
    public void AESTestinit() throws NoSuchAlgorithmException {
        aes = new AES();
        sk = aes.AESKeygen();
        aes.setKey(sk);
        enctime = new long[round];
        dectime = new long[round];
        mktime = new long[round];
    }
    @Test
    public void testAESMakekey() throws NoSuchAlgorithmException {
        long time;
        for(int a=0;a<round;a++){
            time = System.nanoTime();
            aes.AESKeygen();
            time = System.nanoTime()-time;
            mktime[a] = time;
            System.out.println("aes mk round "+(a+1)+" time:"+(float)(time)/1000000L+" ms");
        }
    }
    @Test
    public void testAESEncrypt() {
        long time;
        for (int a = 0; a < round; a++) {
            time = System.nanoTime();
            for (int i = 0; i < 1000; i++) {
                aes.encrypt(data);
            }
            time = System.nanoTime() - time;
            enctime[a] = time;
            System.out.println("aes enc round " + (a + 1) + " time:" + (float) (time) / 1000000L + " ms");
        }
    }
    @Test
    public void testAESDecrypt(){
        long time;
        data2 = aes.encrypt(data);
        for(int a=0;a<round;a++){
            time = System.nanoTime();
            for (int i = 0; i < 1000; i++) {
                aes.decrypt(data2);
            }
            time = System.nanoTime() - time;
            dectime[a] = time;
            System.out.println("aes dec round "+a+" time:"+ (float) (time) / 1000000L+" ms");
        }
    }
    @After
    public void AESTestCalc(){
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
        if(enctotaltime!=0)System.out.println("aes enc average time:"+(double)enctotaltime/round/1000000L+" ms");
        if(dectotaltime!=0)System.out.println("aes dec average time:"+(double)dectotaltime/round/1000000L+" ms");
        if(mktotaltime!=0)System.out.println("aes mk average time:"+(double)mktotaltime/round/1000000L+" ms");
    }
}
