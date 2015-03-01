package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import com.bukkit.gemo.patchworking.IProtection;

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
    public boolean addProtection(IProtection protection, byte subData, boolean saveToDatabase) {
        // register the protection
        return getProtectionManager().addProtection(protection);
    }
}
