package top.starcatmeow.chat.server;

import java.io.Serializable;

/**
 * Created by Dongruixuan Li on 2017/5/26.
 */
public class Account implements Serializable {
    private String username;
    private String password;

    public Account() {
    }
    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean equals(Account account) {
        if (username.equals(account.getUsername()) && password.equals(account.getPassword())) {
            return true;
        } else {
            return false;
        }
    }
}
