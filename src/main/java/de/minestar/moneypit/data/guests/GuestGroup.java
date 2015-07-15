package de.minestar.moneypit.data.guests;

import com.bukkit.gemo.patchworking.Guest;

public class GuestGroup extends Guest {

    private Group group;

    public GuestGroup(String owner, String groupName) {
        super(owner, groupName);
        group = GroupManager.getGroup(getOwner(), getName());
    }

    @Override
    public boolean isValid() {
        return group != null && !group.isDirty();
    }

    @Override
    public boolean hasAccess(String playerName) {
        return group.hasPlayer(playerName);
    }
}
