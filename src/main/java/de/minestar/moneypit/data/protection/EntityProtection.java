package de.minestar.moneypit.data.protection;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.bukkit.gemo.patchworking.GuestGroup;
import com.bukkit.gemo.patchworking.ProtectionType;
import com.bukkit.gemo.utils.UtilPermissions;

public class EntityProtection {
    private final UUID uuid;
    private String owner;
    private final EntityType entityType;
    private final ProtectionType protectionType;
    private GuestGroup guests;

    public EntityProtection(String owner, UUID uuid, EntityType type, ProtectionType protectionType) {
        this.owner = owner;
        this.uuid = uuid;
        this.entityType = type;
        this.protectionType = protectionType;
        this.guests = new GuestGroup(GuestGroup.DEFAULT_NAME, this.owner);
    }

    public String getOwner() {
        return owner;
    }

    public UUID getUuid() {
        return uuid;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public ProtectionType getProtectionType() {
        return protectionType;
    }

    public boolean canAccess(Player player) {
        return this.isPublic() || this.canEdit(player) || this.guests.contains(player.getName().toString());
    }

    public boolean canEdit(Player player) {
        return this.isOwner(player.getName()) || UtilPermissions.playerCanUseCommand(player, "moneypit.admin");
    }

    public boolean isOwner(String playerName) {
        return this.owner.equalsIgnoreCase(playerName);
    }

    public void setOwner(String playerName) {
        this.owner = playerName;
    }

    public void addGuest(String guest) {
        this.guests.add(guest);
    }

    public void addGuests(HashSet<String> guestList) {
        this.guests.add(guestList);
    }

    public void removeGuest(String guest) {
        this.guests.remove(guest.toLowerCase());
    }

    public void setGuestList(GuestGroup group) {
        this.guests = group;
    }

    public GuestGroup getGuestList() {
        return guests;
    }

    public boolean isPublic() {
        return this.protectionType == ProtectionType.PUBLIC;
    }

    public boolean isPrivate() {
        return this.protectionType == ProtectionType.PRIVATE;
    }

    public void clearGuestList() {
        if (this.guests.isDefault()) {
            this.guests.clear();
        }
    }

    public void defaultGuestList() {
        this.guests = new GuestGroup(GuestGroup.DEFAULT_NAME, this.owner);
    }

}
