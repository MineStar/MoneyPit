package de.minestar.moneypit.modules.plate;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_Plate_Stone extends Module_Plate_Abstract {

    private static final Material TYPE = Material.STONE_PLATE;
    private static final String NAME = TYPE.name();

    public Module_Plate_Stone(YamlConfiguration ymlFile) {
        super(ymlFile, NAME);
    }

    public Module_Plate_Stone(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE);
    }

}
