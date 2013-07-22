package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.manager.ModuleManager;

public class Module_Hopper extends Module {

    private final String NAME = "hopper";

    public Module_Hopper(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_Hopper(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.HOPPER.getId(), NAME);
    }

    @Override
    public boolean addProtection(Protection protection, byte subData) {
        // register the protection
        return getProtectionManager().addProtection(protection);
    }
}