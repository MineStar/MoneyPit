package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;

import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.utils.LeverHelper;
import de.minestar.moneypit.utils.PhysicsHelper;

public class Module_Lever extends Module {

    private final String NAME = "lever";

    public Module_Lever(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_Lever(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.LEVER.getId(), NAME);
    }

    @Override
    public boolean addProtection(IProtection protection, byte subData, boolean saveToDatabase) {
        // get the anchor
        BlockVector anchor = LeverHelper.getAnchor(protection.getVector(), subData);

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
