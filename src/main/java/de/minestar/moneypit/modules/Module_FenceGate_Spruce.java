package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_FenceGate_Spruce extends Module_FenceGate_Abstract {

    private static final String NAME = "fencegate_spruce";
    private static final int TYPE_ID = Material.SPRUCE_FENCE_GATE.getId();

    public Module_FenceGate_Spruce(YamlConfiguration ymlFile) {
        super(ymlFile, NAME);
    }

    public Module_FenceGate_Spruce(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE_ID);
    }
}
