package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;

import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.utils.BannerHelper;
import de.minestar.moneypit.utils.PhysicsHelper;

public class Module_Banner_Hanging extends Module {
    
    private final Material TYPE = Material.WALL_BANNER;
    private final String NAME = TYPE.name();

    public Module_Banner_Hanging(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_Banner_Hanging(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, TYPE, NAME);
    }

    @Override
    public boolean addProtection(IProtection protection, byte subData, boolean saveToDatabase) {

        // get the anchor
        BlockVector anchor = BannerHelper.getAnchor(protection.getVector(), subData);

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
