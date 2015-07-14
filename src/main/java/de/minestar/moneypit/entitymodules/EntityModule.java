package de.minestar.moneypit.entitymodules;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.protection.EntityProtection;
import de.minestar.moneypit.manager.EntityModuleManager;

public abstract class EntityModule {

    private EntityType entityType = null;
    private boolean enabled = true;
    private boolean silentEntityDamage;

    protected EntityModule(EntityType entityType, YamlConfiguration ymlFile, boolean initialize, boolean silentEntityDamage) {
        this.entityType = entityType;
        this.silentEntityDamage = silentEntityDamage;
        if (initialize) {
            this.init(MoneyPitCore.entityModuleManager, ymlFile, entityType);
        }
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public boolean isEntityDamageSilent() {
        return silentEntityDamage;
    }

    private void init(EntityModuleManager entityModuleManager, YamlConfiguration ymlFile, EntityType entityType) {
        this.enabled = ymlFile.getBoolean("protect." + this.entityType + ".enabled", true);
        if (this.enabled) {
            entityModuleManager.registerModule(this);
        }
        this.writeDefaultConfig(ymlFile);
    }

    private void writeDefaultConfig(YamlConfiguration ymlFile) {
        ymlFile.set("protect." + entityType + ".enabled", this.enabled);
        this.writeExtraConfig(ymlFile);
    }

    protected void writeExtraConfig(YamlConfiguration ymlFile) {
    }
    
    public void onAddProtection(EntityProtection protection) {        
    }

}
