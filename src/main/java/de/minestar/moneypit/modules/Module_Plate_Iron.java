package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_Plate_Iron extends Module_Plate_Abstract {

    private static final String NAME = "ironplate";
    private static final int TYPE_ID = Material.IRON_PLATE.getId();

    public Module_Plate_Iron(YamlConfiguration ymlFile) {
        super(ymlFile, NAME);
    }

    public Module_Plate_Iron(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE_ID);
    }

}
