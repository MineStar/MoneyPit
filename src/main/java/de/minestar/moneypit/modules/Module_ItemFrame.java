package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.manager.ModuleManager;

public class Module_ItemFrame extends Module {

    private final String NAME = "itemframe";

    public Module_ItemFrame(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_ItemFrame(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.ITEM_FRAME.getId(), NAME);
    }

    @Override
    public boolean addProtection(Protection protection, byte subData) {
        // register the protection
        return getProtectionManager().addProtection(protection);
    }
}
