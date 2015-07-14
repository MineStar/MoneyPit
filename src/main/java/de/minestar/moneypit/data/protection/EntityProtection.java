package de.minestar.moneypit.data.protection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.bukkit.gemo.patchworking.Guest;
import com.bukkit.gemo.patchworking.ProtectionType;
import com.bukkit.gemo.utils.UtilPermissions;

import de.minestar.moneypit.data.guests.GuestHelper;

public class EntityProtection {
    private final UUID uuid;
    private final String owner;
    private final EntityType entityType;
    private final Map<String, Guest> guestList;
    private final ProtectionType protectionType;

    public EntityProtection(String owner, UUID uuid, EntityType type, ProtectionType protectionType) {
        this.owner = owner;
        this.uuid = uuid;
        this.entityType = type;
        this.protectionType = protectionType;
        this.guestList = new HashMap<String, Guest>(1);
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
        return this.isPublic() || this.canEdit(player) || this.isGuest(player.getName());
    }
    public boolean canEdit(Player player) {
        return this.isOwner(player.getName()) || UtilPermissions.playerCanUseCommand(player, "moneypit.admin");
    }

    public boolean isOwner(String playerName) {
        return this.owner.equalsIgnoreCase(playerName);
    }

    public void addGuest(String guestName) {
        this.guestList.put(guestName.toLowerCase(), GuestHelper.create(owner, guestName));
    }

    public void removeGuest(String guestName) {
        this.guestList.remove(guestName.toLowerCase());
    }

    public void setGuestList(Collection<String> list) {
        this.guestList.clear();
        for (String guest : list) {
            this.addGuest(guest);
        }
    }

    public boolean isGuest(String guestName) {
        Guest guest = this.guestList.get(guestName.toLowerCase());
        if (guest != null) {
            return guest.hasAccess(guestName);
        }
        return false;
    }

    public Collection<Guest> getGuestList() {
        return guestList.values();
    }

    public boolean isPublic() {
        return this.protectionType == ProtectionType.PUBLIC;
    }

    public boolean isPrivate() {
        return this.protectionType == ProtectionType.PRIVATE;
    }

    public void clearGuestList() {
        this.guestList.clear();
    }

}
