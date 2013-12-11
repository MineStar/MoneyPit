package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import com.bukkit.gemo.patchworking.BlockVector;

import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.subprotection.SubProtection;
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
    public boolean addProtection(Protection protection, byte subData) {
        // get the anchor
        BlockVector anchor = HangingHelper.getAnchor(protection.getVector(), subData);

        // protect the block below
        SubProtection subProtection = new SubProtection(anchor, protection);
        protection.addSubProtection(subProtection);

        // fetch non-solid-blocks
        PhysicsHelper.protectNonSolidBlocks(protection, anchor);

        // register the protection
        return getProtectionManager().addProtection(protection);
    }
}
