package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_Door_Iron extends Module_Door_Abstract {

    private final static String NAME = "irondoor";
    private final static int TYPE_ID = Material.IRON_DOOR_BLOCK.getId();

    public Module_Door_Iron(YamlConfiguration ymlFile) {
        super(ymlFile, NAME, TYPE_ID);
    }

    public Module_Door_Iron(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE_ID);
    }

}
