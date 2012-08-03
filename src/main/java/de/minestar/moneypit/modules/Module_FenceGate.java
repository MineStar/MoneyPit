package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.manager.ModuleManager;

public class Module_FenceGate extends Module {

    private final String NAME = "fencegate";

    public Module_FenceGate(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_FenceGate(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.FENCE_GATE.getId(), NAME);
        this.setBlockRedstone(ymlFile.getBoolean("protect." + NAME + ".handleRedstone", true));
    }

    @Override
    protected void writeExtraConfig(String moduleName, YamlConfiguration ymlFile) {
        ymlFile.set("protect." + NAME + ".handleRedstone", true);
    }

    @Override
    public void addProtection(Protection protection, byte subData) {
        // register the protection
        getProtectionManager().addProtection(protection);
    }
}
