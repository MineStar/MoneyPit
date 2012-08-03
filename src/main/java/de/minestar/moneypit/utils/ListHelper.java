package de.minestar.moneypit.utils;

import java.util.HashSet;

public class ListHelper {

    private static String DELIMITER = ";";

    public static String toString(HashSet<String> list) {
        String result = "";
        if (list == null || list.size() < 1) {
            return result;
        }

        int i = 0;
        for (String text : list) {
            result += text;
            ++i;
            if (i < list.size()) {
                result += DELIMITER;
            }
        }
        return result;
    }

    public static HashSet<String> toList(String text) {
        HashSet<String> list = null;
        if (text == null) {
            return list;
        }

        String[] split = text.split(DELIMITER);
        if (split == null || split.length == 0) {
            return list;
        }

        list = new HashSet<String>();
        for (String singleText : split) {
            list.add(singleText);
        }

        return list;
    }
}
