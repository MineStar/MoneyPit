package de.minestar.moneypit.modules;

import org.bukkit.configuration.file.YamlConfiguration;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;

import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.utils.DoorHelper;
import de.minestar.moneypit.utils.PhysicsHelper;

public abstract class Module_TrapDoor_Abstract extends Module {

    private final String _name;

    public Module_TrapDoor_Abstract(YamlConfiguration ymlFile, String name) {
        _name = name;
        this.writeDefaultConfig(name, ymlFile);
    }

    public Module_TrapDoor_Abstract(ModuleManager moduleManager, YamlConfiguration ymlFile, String name, int typeId) {
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
        // get the anchor
        BlockVector anchor = DoorHelper.getTrapDoorAnchor(protection.getVector(), subData);

        // protect the block below
        IProtection subProtection = new Protection(anchor, protection);
        protection.addSubProtection(subProtection);
        MoneyPitCore.databaseManager.createSubProtection(subProtection, saveToDatabase);

        // fetch non-solid-blocks
        PhysicsHelper.protectNonSolidBlocks(protection, anchor, saveToDatabase);

        // register the protection
        return getProtectionManager().addProtection(protection);
    }
}
