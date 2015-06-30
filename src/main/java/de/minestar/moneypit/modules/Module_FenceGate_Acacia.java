package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_FenceGate_Acacia extends Module_FenceGate_Abstract {

    private static final String NAME = "fencegate_acacia";
    private static final int TYPE_ID = Material.ACACIA_FENCE_GATE.getId();

    public Module_FenceGate_Acacia(YamlConfiguration ymlFile) {
        super(ymlFile, NAME);
    }

    public Module_FenceGate_Acacia(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE_ID);
    }
}
