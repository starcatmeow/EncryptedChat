package top.starcatmeow.chat.server;

import java.util.Locale;
import java.util.ResourceBundle;

public class getConsoleString {
    static Locale locale = Locale.getDefault();
    static ResourceBundle res = null;

    public static void init() {
        res = ResourceBundle.getBundle("lang.lang", locale);
    }

    public static String get(String str) {
        return res.getString(str);
    }
}
