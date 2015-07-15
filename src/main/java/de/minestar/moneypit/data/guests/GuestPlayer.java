package de.minestar.moneypit.data.guests;

import com.bukkit.gemo.patchworking.Guest;

public class GuestPlayer extends Guest {

    public GuestPlayer(String owner, String playerName) {
        super(owner, playerName);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean hasAccess(String playerName) {
        return getName().equalsIgnoreCase(playerName);
    }

}
