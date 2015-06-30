package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_Door_Birch extends Module_Door_Abstract {

    private final static String NAME = "birchdoor";
    private final static int TYPE_ID = Material.BIRCH_DOOR.getId();

    public Module_Door_Birch(YamlConfiguration ymlFile) {
        super(ymlFile, NAME, TYPE_ID);
    }

    public Module_Door_Birch(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE_ID);
    }
}
