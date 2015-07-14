package de.minestar.moneypit.utils;

import java.util.Collection;
import java.util.HashSet;

import com.bukkit.gemo.patchworking.Guest;

public class ListHelper {

    private static String DELIMITER = ";";

    public static String toString(Collection<Guest> list) {
        String result = "";
        if (list == null || list.size() < 1) {
            return result;
        }

        int i = 0;
        for (Guest guest : list) {
            result += guest.getName();
            ++i;
            if (i < list.size()) {
                result += DELIMITER;
            }
        }
        return result;
    }

    public static String fromStringsToString(Collection<String> list) {
        String result = "";
        if (list == null || list.size() < 1) {
            return result;
        }

        int i = 0;
        for (String guest : list) {
            result += guest;
            ++i;
            if (i < list.size()) {
                result += DELIMITER;
            }
        }
        return result;
    }

    public static Collection<String> toList(String text) {
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
