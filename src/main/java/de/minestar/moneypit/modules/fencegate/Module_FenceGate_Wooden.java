package de.minestar.moneypit.modules.fencegate;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_FenceGate_Wooden extends Module_FenceGate_Abstract {

    private static final Material TYPE = Material.FENCE_GATE;
    private static final String NAME = TYPE.name();

    public Module_FenceGate_Wooden(YamlConfiguration ymlFile) {
        super(ymlFile, NAME);
    }

    public Module_FenceGate_Wooden(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE);
    }
}
