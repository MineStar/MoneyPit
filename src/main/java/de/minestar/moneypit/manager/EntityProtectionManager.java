package de.minestar.moneypit.manager;

import java.util.HashMap;
import java.util.UUID;

import de.minestar.moneypit.data.protection.EntityProtection;

public class EntityProtectionManager {

    private HashMap<UUID, EntityProtection> protections;

    /**
     * Initialize the manager
     */
    public void init() {
        this.protections = new HashMap<UUID, EntityProtection>(256);
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
    public EntityProtection getProtection(UUID uuid) {
        return this.protections.get(uuid);
    }

    /**
     * Add a protection to an entity
     * 
     * @param protectedEntity
     */
    public boolean addProtection(EntityProtection protectedEntity) {
        this.protections.put(protectedEntity.getUuid(), protectedEntity);
        return true;
    }

    /**
     * Remove a protection from an uuid
     * 
     * @param vector
     */
    public void removeProtection(UUID uuid) {
       this.protections.remove(uuid);
    }

    /**
     * Check if the given uuid is protected
     * 
     * @param vector
     * @return <b>true</b> if the uuid is protected, otherwise <b>false</b>
     */
    public boolean hasProtection(UUID uuid) {
        return this.protections.containsKey(uuid);
    }
}
