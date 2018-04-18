package top.starcatmeow.chat.server;

import java.sql.*;

public class Config {
    boolean useMySQL = true;
    String mySQLHost = "localhost";
    String mySQLPort = "3306";
    String mySQLUsername = "root";
    String mySQLPassword = "";
    String mySQLDatabase = "encryptedchat";
    String mySQLuseSSL = "false";
    Connection conn = null;
    PreparedStatement state = null;
    int port = 2333;

    public Config() {
        if (useMySQL)
            initMySQL();
    }

    public boolean isValid(Account account) {
        if (useMySQL) {
            try {
                state.setString(1, account.getUsername());
                state.setString(2, account.getPassword());
                ResultSet rs = state.executeQuery();

                state.setString(1, "");
                state.setString(2, "");
                if (rs.next()) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("无法连接至MySQL服务器");
            }
            return false;
        }
        return false;
    }

    public void initMySQL() {
        try {
            System.out.println("正在加载MySQL JDBC驱动...");
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("加载成功，正在登录至MySQL服务器");

            conn = DriverManager.getConnection("jdbc:mysql://" + mySQLHost + ":" + mySQLPort + "/" + mySQLDatabase + "?characterEncoding=utf8&useSSL=" + mySQLuseSSL, mySQLUsername, mySQLPassword);
            System.out.println("登录成功");
            state = conn.prepareStatement("SELECT * FROM accounts WHERE username=? and password=?");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("无法加载MySQL JDBC驱动!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("无法登录至MySQL服务器");
        }
    }
}
