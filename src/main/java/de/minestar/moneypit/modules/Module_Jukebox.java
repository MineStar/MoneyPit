package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import com.bukkit.gemo.patchworking.IProtection;

import de.minestar.moneypit.manager.ModuleManager;

public class Module_Jukebox extends Module {

    private final String NAME = "jukebox";

    public Module_Jukebox(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_Jukebox(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.JUKEBOX.getId(), NAME);
    }

    @Override
    public boolean addProtection(IProtection protection, byte subData, boolean saveToDatabase) {
        // register the protection
        return getProtectionManager().addProtection(protection);
    }
}
