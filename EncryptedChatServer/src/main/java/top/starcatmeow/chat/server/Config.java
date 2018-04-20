package top.starcatmeow.chat.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    Logger loggerinfo = LogManager.getLogger(Config.class);
    Logger loggererror = LogManager.getLogger("errorslogger");

    public Config() {
        ObjectMapper om = new ObjectMapper();
        File configdir = new File("config/");
        configdir.mkdir();
        File configjson = new File("config/config.json");
        if (!configjson.exists()) {
            loggerinfo.warn(getConsoleString.get("hasnotconfigjson"));
            try {
                om.writeValue(configjson, fileconfig);
            } catch (IOException e) {
                loggererror.error(getConsoleString.get("cannotwriteConfig"));
                for (StackTraceElement ste : e.getStackTrace())
                    loggererror.error(ste.toString());

            }
        }
        try {
            fileconfig = om.readValue(configjson, Config_fs.class);
        } catch (IOException e) {
            loggererror.error(getConsoleString.get("cannotreadConfig"));
            for (StackTraceElement ste : e.getStackTrace())
                loggererror.error(ste.toString());

        }
        loggerinfo.info(getConsoleString.get("readConfigsuccess"));

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
                loggererror.error(getConsoleString.get("cannotconnectmySQL"));
                for (StackTraceElement ste : e.getStackTrace())
                    loggererror.error(ste.toString());

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
            loggerinfo.info(getConsoleString.get("loginmySQL"));

            conn = DriverManager.getConnection("jdbc:mysql://" + fileconfig.mysqlData.mySQLHost + ":" + fileconfig.mysqlData.mySQLPort + "/" + fileconfig.mysqlData.mySQLDatabase + "?characterEncoding=utf8&useSSL=" + fileconfig.mysqlData.useSSL, fileconfig.mysqlData.mySQLUsername, fileconfig.mysqlData.mySQLPassword);
            loggerinfo.info(getConsoleString.get("loginsuccess"));
            state = conn.prepareStatement("SELECT * FROM accounts WHERE username=? and password=?");

        } catch (ClassNotFoundException e) {
            loggererror.error(getConsoleString.get("cannotloaddriver"));
            for (StackTraceElement ste : e.getStackTrace())
                loggererror.error(ste.toString());

        } catch (SQLException e) {
            loggererror.error(getConsoleString.get("cannotloginmySQL"));
            for (StackTraceElement ste : e.getStackTrace())
                loggererror.error(ste.toString());

        }
    }

    public void importAccountsfromFile() {
        File accountjson = new File("config/" + fileconfig.accountFile);
        if (!accountjson.exists()) {
            loggerinfo.warn(getConsoleString.get("hasnotaccountjson"));
            List<Account> examplelist = new ArrayList<Account>();
            examplelist.add(new Account("test", "testpassword"));
            examplelist.add(new Account("test1", "testpassword1"));
            examplelist.add(new Account("中文测试用户", "中文测试密码"));
            try {
                new ObjectMapper().writeValue(accountjson, examplelist);
            } catch (IOException e) {
                loggererror.error(getConsoleString.get("cannotwriteConfig"));
                for (StackTraceElement ste : e.getStackTrace())
                    loggererror.error(ste.toString());
            }
        }
        try {
            accountList = new ObjectMapper().readValue(accountjson, new ObjectMapper().getTypeFactory().constructParametricType(ArrayList.class, Account.class));
            loggerinfo.info(getConsoleString.get("readAccountsuccess"));
        } catch (IOException e) {
            for (StackTraceElement ste : e.getStackTrace())
                loggererror.error(ste.toString());

        }
    }
}
