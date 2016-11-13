package de.minestar.moneypit.modules.trapdoor;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_TrapDoor_Wooden extends Module_TrapDoor_Abstract {

    private static final Material TYPE = Material.TRAP_DOOR;
    private static final String NAME = TYPE.name();
    
    public Module_TrapDoor_Wooden(YamlConfiguration ymlFile) {
        super(ymlFile, NAME);
    }

    public Module_TrapDoor_Wooden(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE);
    }
}
