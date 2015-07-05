package de.minestar.moneypit.entitymodules;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import de.minestar.moneypit.data.protection.EntityProtection;

public class EntityModule_ArmorStand extends EntityModule {

    public EntityModule_ArmorStand(YamlConfiguration ymlFile, boolean initialize) {
        super(EntityType.ARMOR_STAND, ymlFile, initialize, true);
    }

    @Override
    public void onAddProtection(EntityProtection protection) {
        super.onAddProtection(protection);
    }

}
