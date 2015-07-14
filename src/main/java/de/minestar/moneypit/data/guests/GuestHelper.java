package de.minestar.moneypit.data.guests;

import com.bukkit.gemo.patchworking.Guest;

public class GuestHelper {
    public final static String GROUP_PREFIX = "g:";

    public static Guest create(String owner, String name) {
        if (name.startsWith(GROUP_PREFIX)) {
            return new GuestGroup(owner, name);
        } else {
            return new GuestPlayer(owner, name);
        }
    }
}
