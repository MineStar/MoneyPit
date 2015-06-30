package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_Door_Jungle extends Module_Door_Abstract {

    private final static String NAME = "jungledoor";
    private final static int TYPE_ID = Material.JUNGLE_DOOR.getId();

    public Module_Door_Jungle(YamlConfiguration ymlFile) {
        super(ymlFile, NAME, TYPE_ID);
    }

    public Module_Door_Jungle(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE_ID);
    }
}
