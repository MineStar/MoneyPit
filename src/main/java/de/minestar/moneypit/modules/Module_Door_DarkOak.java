package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_Door_DarkOak extends Module_Door_Abstract {

    private final static String NAME = "darkoakdoor";
    private final static int TYPE_ID = Material.DARK_OAK_DOOR.getId();

    public Module_Door_DarkOak(YamlConfiguration ymlFile) {
        super(ymlFile, NAME, TYPE_ID);
    }

    public Module_Door_DarkOak(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE_ID);
    }
}
