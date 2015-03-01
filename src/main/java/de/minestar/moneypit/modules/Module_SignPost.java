package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import com.bukkit.gemo.patchworking.IProtection;

import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.utils.PhysicsHelper;

public class Module_SignPost extends Module {

    private final String NAME = "signpost";

    public Module_SignPost(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_SignPost(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.SIGN_POST.getId(), NAME);
    }

    @Override
    public boolean addProtection(IProtection protection, byte subData, boolean saveToDatabase) {
        // protect the block below
        IProtection subProtection = new Protection(protection.getVector().getRelative(0, -1, 0), protection);
        protection.addSubProtection(subProtection);
        MoneyPitCore.databaseManager.createSubProtection(subProtection, saveToDatabase);

        // fetch non-solid-blocks
        PhysicsHelper.protectNonSolidBlocks(protection, subProtection.getVector(), saveToDatabase);

        // register the protection
        return getProtectionManager().addProtection(protection);
    }
}
