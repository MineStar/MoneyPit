package de.minestar.moneypit.modules;

import org.bukkit.configuration.file.YamlConfiguration;

import com.bukkit.gemo.patchworking.IProtection;

import de.minestar.moneypit.manager.ModuleManager;

public abstract class Module_FenceGate_Abstract extends Module {

    private final String _name;

    public Module_FenceGate_Abstract(YamlConfiguration ymlFile, String name) {
        _name = name;
        this.writeDefaultConfig(_name, ymlFile);
    }

    public Module_FenceGate_Abstract(ModuleManager moduleManager, YamlConfiguration ymlFile, String name, int typeId) {
        super();
        _name = name;
        this.init(moduleManager, ymlFile, typeId, _name);
        this.setBlockRedstone(ymlFile.getBoolean("protect." + _name + ".handleRedstone", true));
    }

    @Override
    protected final void writeExtraConfig(String moduleName, YamlConfiguration ymlFile) {
        ymlFile.set("protect." + _name + ".handleRedstone", true);
    }

    @Override
    public final boolean addProtection(IProtection protection, byte subData, boolean saveToDatabase) {
        // register the protection
        return getProtectionManager().addProtection(protection);
    }
}
