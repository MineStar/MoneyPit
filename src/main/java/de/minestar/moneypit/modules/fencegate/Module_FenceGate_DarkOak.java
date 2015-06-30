package de.minestar.moneypit.modules.fencegate;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_FenceGate_DarkOak extends Module_FenceGate_Abstract {

    private static final String NAME = "fencegate_darkoak";
    private static final int TYPE_ID = Material.DARK_OAK_FENCE_GATE.getId();

    public Module_FenceGate_DarkOak(YamlConfiguration ymlFile) {
        super(ymlFile, NAME);
    }

    public Module_FenceGate_DarkOak(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super(moduleManager, ymlFile, NAME, TYPE_ID);
    }
}
