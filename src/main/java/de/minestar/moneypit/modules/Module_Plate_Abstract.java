package de.minestar.moneypit.modules;

import org.bukkit.configuration.file.YamlConfiguration;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;

import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.utils.PhysicsHelper;

public class Module_Plate_Abstract extends Module {

    private final String _name;

    public Module_Plate_Abstract(YamlConfiguration ymlFile, String name) {
        _name = name;
        this.writeDefaultConfig(_name, ymlFile);
    }

    public Module_Plate_Abstract(ModuleManager moduleManager, YamlConfiguration ymlFile, String name, int typeId) {
        super();
        _name = name;
        this.init(moduleManager, ymlFile, typeId, _name);
    }

    @Override
    public final boolean addProtection(IProtection protection, byte subData, boolean saveToDatabase) {
        // get the anchor
        BlockVector anchor = protection.getVector().getRelative(0, -1, 0);

        // protect the block below
        IProtection subProtection = new Protection(protection.getVector().getRelative(0, -1, 0), protection);
        protection.addSubProtection(subProtection);
        MoneyPitCore.databaseManager.createSubProtection(subProtection, saveToDatabase);

        // fetch non-solid-blocks
        PhysicsHelper.protectNonSolidBlocks(protection, anchor, saveToDatabase);

        // register the protection
        return getProtectionManager().addProtection(protection);
    }
}
