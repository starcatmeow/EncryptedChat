package top.starcatmeow.chat.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Config {
    Config_fs fileconfig = new Config_fs();
    Connection conn = null;
    PreparedStatement state = null;
    List<Account> accountList = null;

    public Config() {
        ObjectMapper om = new ObjectMapper();
        try {
            fileconfig = om.readValue(new File("config.json"), Config_fs.class);
        } catch (IOException e) {
            System.out.println("配置文件读取失败：");
            e.printStackTrace();
        }
        System.out.println("成功读取配置文件！");

        if (fileconfig.useMysql)
            initMySQL();
        else
            importAccountsfromFile();
    }

    public boolean isValid(Account account) {
        if (fileconfig.useMysql) {
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
        } else {
            for (Account a : accountList) {
                if (account.equals(a))
                    return true;
            }
            return false;
        }
    }

    public void initMySQL() {
        try {
            System.out.println("正在加载MySQL JDBC驱动...");
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("加载成功，正在登录至MySQL服务器");

            conn = DriverManager.getConnection("jdbc:mysql://" + fileconfig.mysqlData.mySQLHost + ":" + fileconfig.mysqlData.mySQLPort + "/" + fileconfig.mysqlData.mySQLDatabase + "?characterEncoding=utf8&useSSL=" + fileconfig.mysqlData.useSSL, fileconfig.mysqlData.mySQLUsername, fileconfig.mysqlData.mySQLPassword);
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

    public void importAccountsfromFile() {
        try {
            accountList = new ObjectMapper().readValue(new File(fileconfig.accountFile), new ObjectMapper().getTypeFactory().constructParametricType(ArrayList.class, Account.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
