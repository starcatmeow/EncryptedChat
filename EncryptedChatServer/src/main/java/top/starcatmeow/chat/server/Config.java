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

    public Config(int strength){
        fileconfig.rsastrength = strength;
    }

    public Config() {
        ObjectMapper om = new ObjectMapper();                                                                           //新建一个解析器
        File configdir = new File("config/");                                                               //新建config目录
        configdir.mkdir();
        File configjson = new File("config/config.json");
        if (!configjson.exists()) {                                                                                     //不存在配置文件则创建
            loggerinfo.warn(getConsoleString.get("hasnotconfigjson"));                                                  //写入警告日志
            try {
                om.writeValue(configjson, fileconfig);
            } catch (IOException e) {
                loggererror.error(getConsoleString.get("cannotwriteConfig"));                                           //写入错误日志
                for (StackTraceElement ste : e.getStackTrace())
                    loggererror.error(ste.toString());

            }
        }
        try {
            fileconfig = om.readValue(configjson, Config_fs.class);                                                     //读取配置文件
        } catch (IOException e) {
            loggererror.error(getConsoleString.get("cannotreadConfig"));                                                //写入错误日志
            for (StackTraceElement ste : e.getStackTrace())
                loggererror.error(ste.toString());

        }
        loggerinfo.info(getConsoleString.get("readConfigsuccess"));                                                     //写入日志

        if (fileconfig.useMysql)
            initMySQL();                                                                                                //初始化MySQL连接
        else
            importAccountsfromFile();                                                                                   //从文件导入账户信息
    }

    public boolean isValid(Account account) {
        if (fileconfig.useMysql) {
            try {
                state.setString(1, account.getUsername());                                              //将待验证的用户名代入预编译好的查询指令中
                state.setString(2, account.getPassword());                                              //将待验证的密码代入预编译好的查询指令中
                ResultSet rs = state.executeQuery();                                                                    //执行查询

                state.setString(1, "");
                state.setString(2, "");
                if (rs.next()) {                                                                                        //判断是否存在此账户
                    return true;
                }
            } catch (SQLException e) {
                loggererror.error(getConsoleString.get("cannotconnectmySQL"));                                          //写入错误日志
                for (StackTraceElement ste : e.getStackTrace())
                    loggererror.error(ste.toString());

            }
            return false;
        } else {
            for (Account a : accountList) {                                                                             //遍历账户列表
                if (account.equals(a))                                                                                  //判断是否匹配
                    return true;
            }
            return false;
        }
    }

    public void initMySQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");                                                                     //反射MySQL JDBC
            loggerinfo.info(getConsoleString.get("loginmySQL"));                                                        //写入日志

            conn = DriverManager.getConnection("jdbc:mysql://" + fileconfig.mysqlData.mySQLHost + ":" + fileconfig.mysqlData.mySQLPort + "/" + fileconfig.mysqlData.mySQLDatabase + "?characterEncoding=utf8&useSSL=" + fileconfig.mysqlData.useSSL, fileconfig.mysqlData.mySQLUsername, fileconfig.mysqlData.mySQLPassword);
            //连接至数据库
            loggerinfo.info(getConsoleString.get("loginsuccess"));                                                      //写入日志
            state = conn.prepareStatement("SELECT * FROM accounts WHERE username=? and password=?");            //预编译查询指令，以防止数据库注入

        } catch (ClassNotFoundException e) {
            loggererror.error(getConsoleString.get("cannotloaddriver"));                                                //写入错误日志
            for (StackTraceElement ste : e.getStackTrace())
                loggererror.error(ste.toString());

        } catch (SQLException e) {
            loggererror.error(getConsoleString.get("cannotloginmySQL"));                                                //写入错误日志
            for (StackTraceElement ste : e.getStackTrace())
                loggererror.error(ste.toString());

        }
    }

    public void importAccountsfromFile() {
        File accountjson = new File("config/" + fileconfig.accountFile);
        if (!accountjson.exists()) {                                                                                    //没有配置文件则创建
            loggerinfo.warn(getConsoleString.get("hasnotaccountjson"));                                                 //写入警告日志
            List<Account> examplelist = new ArrayList<Account>();
            examplelist.add(new Account("test", "testpassword"));                                 //放入默认账户
            examplelist.add(new Account("test1", "testpassword1"));
            examplelist.add(new Account("中文测试用户", "中文测试密码"));
            try {
                new ObjectMapper().writeValue(accountjson, examplelist);                                                //格式化后写入配置文件
            } catch (IOException e) {
                loggererror.error(getConsoleString.get("cannotwriteConfig"));                                           //写入错误日志
                for (StackTraceElement ste : e.getStackTrace())
                    loggererror.error(ste.toString());
            }
        }
        try {
            accountList = new ObjectMapper().readValue(accountjson, new ObjectMapper().getTypeFactory().constructParametricType(ArrayList.class, Account.class));
            //读取账户至账户列表
            loggerinfo.info(getConsoleString.get("readAccountsuccess"));                                                //写入日志
        } catch (IOException e) {
            for (StackTraceElement ste : e.getStackTrace())                                                             //写入错误日志
                loggererror.error(ste.toString());

        }
    }
}
