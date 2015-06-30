package de.minestar.moneypit.modules.door;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_Door_Wooden extends Module_Door_Abstract {

    private final static String NAME = "wooddoor";
    private final static int TYPE_ID = Material.IRON_DOOR_BLOCK.getId();

    public Module_Door_Wooden(YamlConfiguration ymlFile) {
        super(ymlFile, NAME, TYPE_ID);
    }

    public Module_Door_Wooden(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE_ID);
    }
}
