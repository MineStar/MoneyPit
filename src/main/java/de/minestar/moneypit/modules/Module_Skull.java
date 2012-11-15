package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.manager.ModuleManager;

public class Module_Skull extends Module {

    private final String NAME = "skull";

    public Module_Skull(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_Skull(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.SKULL.getId(), NAME);
    }

    @Override
    public boolean addProtection(Protection protection, byte subData) {
        // register the protection
        return getProtectionManager().addProtection(protection);
    }
}
