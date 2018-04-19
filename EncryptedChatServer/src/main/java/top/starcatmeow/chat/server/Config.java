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
        File configdir = new File("config/");
        configdir.mkdir();
        File configjson = new File("config/config.json");
        if (!configjson.exists()) {
            System.out.println(getConsoleString.get("hasnotconfigjson"));
            try {
                om.writeValue(configjson, fileconfig);
            } catch (IOException e) {
                System.out.println(getConsoleString.get("cannotwriteConfig"));
                e.printStackTrace();
            }
        }
        try {
            fileconfig = om.readValue(configjson, Config_fs.class);
        } catch (IOException e) {
            System.out.println(getConsoleString.get("cannotreadConfig"));
            e.printStackTrace();
        }
        System.out.println(getConsoleString.get("readConfigsuccess"));

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
                System.out.println(getConsoleString.get("cannotconnectmySQL"));
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
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println(getConsoleString.get("loginmySQL"));

            conn = DriverManager.getConnection("jdbc:mysql://" + fileconfig.mysqlData.mySQLHost + ":" + fileconfig.mysqlData.mySQLPort + "/" + fileconfig.mysqlData.mySQLDatabase + "?characterEncoding=utf8&useSSL=" + fileconfig.mysqlData.useSSL, fileconfig.mysqlData.mySQLUsername, fileconfig.mysqlData.mySQLPassword);
            System.out.println(getConsoleString.get("loginsuccess"));
            state = conn.prepareStatement("SELECT * FROM accounts WHERE username=? and password=?");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println(getConsoleString.get("cannotloaddriver"));
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(getConsoleString.get("cannotloginmySQL"));
        }
    }

    public void importAccountsfromFile() {
        File accountjson = new File("config/" + fileconfig.accountFile);
        if (!accountjson.exists()) {
            System.out.println(getConsoleString.get("hasnotaccountjson"));
            List<Account> examplelist = new ArrayList<Account>();
            examplelist.add(new Account("test", "testpassword"));
            examplelist.add(new Account("test1", "testpassword1"));
            examplelist.add(new Account("中文测试用户", "中文测试密码"));
            try {
                new ObjectMapper().writeValue(accountjson, examplelist);
            } catch (IOException e) {
                System.out.println(getConsoleString.get("cannotwriteConfig"));
                e.printStackTrace();
            }
        }
        try {
            accountList = new ObjectMapper().readValue(accountjson, new ObjectMapper().getTypeFactory().constructParametricType(ArrayList.class, Account.class));
            System.out.println(getConsoleString.get("readAccountsuccess"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
