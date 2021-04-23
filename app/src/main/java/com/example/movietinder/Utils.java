package com.example.movietinder;

public class Utils {

    public static String encodeString(String string) {
        string = string.replace(".", ",");
        string = string.replace('$', '`');
        string = string.replace('#', '~');
        string = string.replace('[', '(');
        string = string.replace(']', ')');
        return string;
    }

    public static String decodeString(String string) {
        string = string.replace(",", ".");
        string = string.replace("`", "$");
        string = string.replace("~", "#");
        return string;
    }
}
