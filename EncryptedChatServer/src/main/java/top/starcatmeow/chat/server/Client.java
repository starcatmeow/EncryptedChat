package top.starcatmeow.chat.server;

import javax.crypto.SecretKey;
import java.net.Socket;

/**
 * Created by Dongruixuan Li on 2017/5/26.
 */
public class Client {
    private String username;
    private String ip;
    private int port;
    private String ipandport;
    private Socket socket;
    private SecretKey aeskey;
    private AES aes;

    public Client(Socket socket) {
        setSocket(socket);
        aes = new AES();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setAeskey(SecretKey key) {
        aeskey = key;
        aes.setKey(key);
    }

    public String getIpandport() {
        return ipandport;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        this.ip = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        this.ipandport = this.ip + ":" + Integer.toString(this.port);
    }

    public SecretKey getAeskey() {
        return aeskey;
    }

    public AES getAes() {
        return aes;
    }

}
