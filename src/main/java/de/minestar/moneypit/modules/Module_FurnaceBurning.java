package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import com.bukkit.gemo.patchworking.IProtection;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_FurnaceBurning extends Module {

    private final String NAME = "burningfurnace";

    public Module_FurnaceBurning(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_FurnaceBurning(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.BURNING_FURNACE.getId(), NAME);
    }

    @Override
    public boolean addProtection(IProtection protection, byte subData, boolean saveToDatabase) {
        // register the protection
        return getProtectionManager().addProtection(protection);
    }
}
