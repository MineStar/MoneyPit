package de.minestar.moneypit.manager;

import java.util.HashMap;

import com.bukkit.gemo.patchworking.GuestGroup;

import de.minestar.moneypit.data.protection.EntityProtection;

public class EntityProtectionManager {

    private HashMap<String, EntityProtection> protections;

    /**
     * Initialize the manager
     */
    public void init() {
        this.protections = new HashMap<String, EntityProtection>(256);
    }

    // ////////////////////////////////////////////////////////////////
    //
    // Protection
    //
    // ////////////////////////////////////////////////////////////////

    /**
     * Get the protection of an entity
     * 
     * @param uuid
     * @return the ProtectionEntity
     */
    public EntityProtection getProtection(String uuid) {
        return this.protections.get(uuid);
    }

    /**
     * Add a protection to an entity
     * 
     * @param protectedEntity
     */
    public boolean addProtection(EntityProtection protectedEntity) {
        this.protections.put(protectedEntity.getUuid().toString(), protectedEntity);
        return true;
    }

    /**
     * Remove a protection from an uuid
     * 
     * @param vector
     */
    public void removeProtection(String uuid) {
        this.protections.remove(uuid);
    }

    /**
     * Check if the given uuid is protected
     * 
     * @param vector
     * @return <b>true</b> if the uuid is protected, otherwise <b>false</b>
     */
    public boolean hasProtection(String uuid) {
        return this.protections.containsKey(uuid);
    }

    public void resetGuestList(GuestGroup group) {
        for (EntityProtection protection : protections.values()) {
            if (protection.isOwner(group.getOwner()) && protection.getGuestList().getName().equalsIgnoreCase(group.getName())) {
                protection.defaultGuestList();
            }
        }
    }

    public void transferProtections(String oldPlayername, String newPlayername) {
        for (EntityProtection protection : protections.values()) {
            if (protection.isOwner(oldPlayername)) {
                protection.setOwner(newPlayername);
            }
        }
    }
}
