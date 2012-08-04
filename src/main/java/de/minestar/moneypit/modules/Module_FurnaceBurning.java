package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.data.protection.Protection;
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
    public void addProtection(Protection protection, byte subData) {
        // register the protection
        getProtectionManager().addProtection(protection);
    }
}
