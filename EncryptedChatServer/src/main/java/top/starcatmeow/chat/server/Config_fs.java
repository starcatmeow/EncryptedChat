package top.starcatmeow.chat.server;

public class Config_fs {
    public int rsastrength = 2048;
    public int servicePort = 7999;
    public boolean useMysql = false;
    //       MySQL_fs mySQLData;
    public String accountFile = "account.json";
    public MySQL_fs mysqlData = new MySQL_fs();
}
