package de.minestar.moneypit.entitymodules;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

public class EntityModule_Horse extends EntityModule {

    public EntityModule_Horse(YamlConfiguration ymlFile, boolean initialize) {
        super(EntityType.HORSE, ymlFile, initialize, false);
    }

}
