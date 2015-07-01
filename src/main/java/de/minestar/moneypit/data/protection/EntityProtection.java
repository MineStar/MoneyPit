package de.minestar.moneypit.data.protection;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.bukkit.gemo.patchworking.ProtectionType;
import com.bukkit.gemo.utils.UtilPermissions;

public class EntityProtection {
    private final UUID uuid;
    private final String owner;
    private final EntityType entityType;
    private final HashSet<String> guestList;
    private final ProtectionType protectionType;

    public EntityProtection(String owner, UUID uuid, EntityType type, ProtectionType protectionType) {
        this.owner = owner;
        this.uuid = uuid;
        this.entityType = type;
        this.protectionType = protectionType;
        this.guestList = new HashSet<String>(1);
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
        return this.isPublic() || this.canEdit(player) || this.guestList.contains(player.getName().toString());
    }

    public boolean canEdit(Player player) {
        return this.isOwner(player) || UtilPermissions.playerCanUseCommand(player, "moneypit.admin");
    }

    public boolean isOwner(Player player) {
        return this.owner.equalsIgnoreCase(player.getName());
    }

    public void addGuest(String guest) {
        this.guestList.add(guest);
    }

    public void setGuestList(HashSet<String> list) {
        this.guestList.addAll(list);
    }

    public HashSet<String> getGuestList() {
        return guestList;
    }

    public boolean isPublic() {
        return this.protectionType == ProtectionType.PUBLIC;
    }

    public boolean isPrivate() {
        return this.protectionType == ProtectionType.PRIVATE;
    }

}
