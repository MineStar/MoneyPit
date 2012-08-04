package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.subprotection.SubProtection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.utils.ButtonHelper;
import de.minestar.moneypit.utils.PhysicsHelper;

public class Module_StoneButton extends Module {

    private final String NAME = "stonebutton";

    public Module_StoneButton(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_StoneButton(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.STONE_BUTTON.getId(), NAME);
    }

    @Override
    public void addProtection(Protection protection, byte subData) {
        // get the anchor
        BlockVector anchor = ButtonHelper.getAnchor(protection.getVector(), subData);

        // protect the block below
        SubProtection subProtection = new SubProtection(anchor, protection);
        protection.addSubProtection(subProtection);

        // fetch non-solid-blocks
        PhysicsHelper.protectNonSolidBlocks(protection, anchor);

        // register the protection
        getProtectionManager().addProtection(protection);
    }
}
