package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_FenceGate_Birch extends Module_FenceGate_Abstract {

    private static final String NAME = "fencegate_birch";
    private static final int TYPE_ID = Material.BIRCH_FENCE_GATE.getId();

    public Module_FenceGate_Birch(YamlConfiguration ymlFile) {
        super(ymlFile, NAME);
    }

    public Module_FenceGate_Birch(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE_ID);
    }
}
