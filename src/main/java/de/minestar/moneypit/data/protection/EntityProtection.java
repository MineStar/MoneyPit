package de.minestar.moneypit.data.protection;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.bukkit.gemo.patchworking.ProtectionType;
import com.bukkit.gemo.utils.UtilPermissions;

public class EntityProtection {
    private final UUID uuid;
    private final String ownerUUID;
    private final EntityType entityType;
    private final HashSet<String> guestList;
    private final ProtectionType protectionType;

    public EntityProtection(String ownerUuid, UUID uuid, EntityType type, ProtectionType protectionType) {
        this.ownerUUID = ownerUuid;
        this.uuid = uuid;
        this.entityType = type;
        this.protectionType = protectionType;
        this.guestList = new HashSet<String>(1);
    }

    public String getOwnerUUID() {
        return ownerUUID;
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
        return this.isPublic() || this.canEdit(player) || this.guestList.contains(player.getUniqueId().toString());
    }

    public boolean canEdit(Player player) {
        return this.isOwner(player) || UtilPermissions.playerCanUseCommand(player, "moneypit.admin");
    }

    public boolean isOwner(Player player) {
        return this.ownerUUID.equalsIgnoreCase(player.getUniqueId().toString());
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
