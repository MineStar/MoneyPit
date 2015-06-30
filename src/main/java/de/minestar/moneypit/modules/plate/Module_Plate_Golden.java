package de.minestar.moneypit.modules.plate;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_Plate_Golden extends Module_Plate_Abstract {

    private static final String NAME = "goldenplate";
    private static final int TYPE_ID = Material.GOLD_PLATE.getId();

    public Module_Plate_Golden(YamlConfiguration ymlFile) {
        super(ymlFile, NAME);
    }

    public Module_Plate_Golden(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE_ID);
    }

}
