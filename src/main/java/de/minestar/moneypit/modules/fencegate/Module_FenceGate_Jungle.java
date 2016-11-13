package de.minestar.moneypit.modules.fencegate;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_FenceGate_Jungle extends Module_FenceGate_Abstract {

    private static final Material TYPE = Material.JUNGLE_FENCE_GATE;
    private static final String NAME = TYPE.name();

    public Module_FenceGate_Jungle(YamlConfiguration ymlFile) {
        super(ymlFile, NAME);
    }

    public Module_FenceGate_Jungle(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE);
    }
}
