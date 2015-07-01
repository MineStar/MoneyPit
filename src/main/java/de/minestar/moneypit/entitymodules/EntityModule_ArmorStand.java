package de.minestar.moneypit.entitymodules;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

public class EntityModule_ArmorStand extends EntityModule {

    public EntityModule_ArmorStand(YamlConfiguration ymlFile, boolean initialize) {
        super(EntityType.ARMOR_STAND, ymlFile, initialize, true);
    }

}
