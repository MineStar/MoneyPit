package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;

import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.utils.HangingHelper;
import de.minestar.moneypit.utils.PhysicsHelper;

public class Module_ItemFrame extends Module {

    private final String NAME = "itemframe";

    public Module_ItemFrame(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
        this.setAutolock(false);
    }

    public Module_ItemFrame(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.ITEM_FRAME.getId(), NAME);
        this.setAutolock(false);
    }

    @Override
    public boolean addProtection(IProtection protection, byte subData, boolean saveToDatabase) {
        // get the anchor
        BlockVector anchor = HangingHelper.getAnchor(protection.getVector(), subData);

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
