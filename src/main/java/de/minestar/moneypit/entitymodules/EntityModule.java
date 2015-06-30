package de.minestar.moneypit.entitymodules;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.manager.EntityModuleManager;

public abstract class EntityModule {

    private EntityType entityType = null;

    protected EntityModule(EntityType entityType, YamlConfiguration ymlFile, boolean initialize) {
        this.entityType = entityType;
        this.writeDefaultConfig(ymlFile);
        this.writeExtraConfig(ymlFile);
        if (initialize) {
            this.init(MoneyPitCore.entityModuleManager, ymlFile, entityType);
        }
    }

    public EntityType getEntityType() {
        return entityType;
    }

    private void init(EntityModuleManager entityModuleManager, YamlConfiguration ymlFile, EntityType entityType) {
        boolean isEnabled = ymlFile.getBoolean("protect." + this.entityType + ".enabled", true);
        if (isEnabled) {
            entityModuleManager.registerModule(this);
        }
    }

    private void writeDefaultConfig(YamlConfiguration ymlFile) {
        ymlFile.set("protect." + entityType + ".enabled", true);
        this.writeExtraConfig(ymlFile);
    }

    protected void writeExtraConfig(YamlConfiguration ymlFile) {
    }

}
