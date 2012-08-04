package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.subprotection.SubProtection;
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
    public void addProtection(Protection protection, byte subData) {
        // protect the block below
        SubProtection subProtection = new SubProtection(protection.getVector().getRelative(0, -1, 0), protection);
        protection.addSubProtection(subProtection);

        // fetch non-solid-blocks
        PhysicsHelper.protectNonSolidBlocks(protection, subProtection.getVector());

        // register the protection
        getProtectionManager().addProtection(protection);
    }
}
