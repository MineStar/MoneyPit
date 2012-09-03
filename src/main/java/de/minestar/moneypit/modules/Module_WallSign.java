package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.subprotection.SubProtection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.utils.PhysicsHelper;
import de.minestar.moneypit.utils.SignHelper;

public class Module_WallSign extends Module {

    private final String NAME = "wallsign";

    public Module_WallSign(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_WallSign(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.WALL_SIGN.getId(), NAME);
    }

    @Override
    public boolean addProtection(Protection protection, byte subData) {
        // get the anchor
        BlockVector anchor = SignHelper.getAnchor(protection.getVector(), subData);

        // protect the block below
        SubProtection subProtection = new SubProtection(anchor, protection);
        protection.addSubProtection(subProtection);

        // fetch non-solid-blocks
        PhysicsHelper.protectNonSolidBlocks(protection, anchor);

        // register the protection
        return getProtectionManager().addProtection(protection);
    }
}
