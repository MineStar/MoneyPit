package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_Plate_Wooden extends Module_Plate_Abstract {

    private static final String NAME = "woodenplate";
    private static final int TYPE_ID = Material.WOOD_PLATE.getId();

    public Module_Plate_Wooden(YamlConfiguration ymlFile) {
        super(ymlFile, NAME);
    }

    public Module_Plate_Wooden(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE_ID);
    }

}
