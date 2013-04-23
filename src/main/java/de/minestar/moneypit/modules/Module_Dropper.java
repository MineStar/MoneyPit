package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.manager.ModuleManager;

public class Module_Dropper extends Module {

    private final String NAME = "dropper";

    public Module_Dropper(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_Dropper(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.DROPPER.getId(), NAME);
    }

    @Override
    public boolean addProtection(Protection protection, byte subData) {
        // register the protection
        return getProtectionManager().addProtection(protection);
    }
}
