package de.minestar.moneypit.modules.trapdoor;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_TrapDoor_Iron extends Module_TrapDoor_Abstract {

    private static final String NAME = "trapdoor_iron";
    private static final int TYPE_ID = Material.IRON_TRAPDOOR.getId();

    public Module_TrapDoor_Iron(YamlConfiguration ymlFile) {
        super(ymlFile, NAME);
    }

    public Module_TrapDoor_Iron(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE_ID);
    }
}
