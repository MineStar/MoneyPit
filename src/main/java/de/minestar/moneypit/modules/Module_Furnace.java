package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.manager.ModuleManager;

public class Module_Furnace extends Module {

    private final String NAME = "furnace";

    public Module_Furnace(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_Furnace(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.FURNACE.getId(), NAME);
    }

    @Override
    public void addProtection(Protection protection, byte subData) {
        // register the protection
        getProtectionManager().addProtection(protection);
    }
}
