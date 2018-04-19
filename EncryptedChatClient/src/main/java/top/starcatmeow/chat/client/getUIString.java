package top.starcatmeow.chat.client;

import java.util.Locale;
import java.util.ResourceBundle;

public class getUIString {
    static Locale locale = Locale.getDefault();
    static ResourceBundle res = null;

    public static void init() {
        if (locale.toString().equals("zh_CN"))
            res = ResourceBundle.getBundle("lang.lang", locale);
        else {
            System.out.println(locale.toString());
            res = ResourceBundle.getBundle("lang.lang", Locale.US);
        }
    }

    public static String get(String str) {
        return res.getString(str);
    }
}
