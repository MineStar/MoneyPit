package de.minestar.moneypit.data.guests;

import com.bukkit.gemo.patchworking.Guest;

public class GuestGroup extends Guest {

    public GuestGroup(String owner, String groupName) {
        super(owner, groupName);
    }

    @Override
    public boolean hasAccess(String playerName) {
        Group group = GroupManager.getGroup(getOwner(), getName());
        if (group == null) {
            return false;
        }
        return group.hasPlayer(playerName);
    }
}
