package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.subprotection.SubProtection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.utils.LeverHelper;

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
    public void addProtection(Protection protection, byte subData) {
        // protect the block below
        SubProtection subProtection = new SubProtection(LeverHelper.getAnchor(protection.getVector(), subData), protection);
        protection.addSubProtection(subProtection);

        // FETCH SAND & GRAVEL
        BlockVector tempVector = LeverHelper.getAnchor(protection.getVector(), subData);
        if (this.isBlockNonSolid(tempVector.getLocation().getBlock().getTypeId())) {
            int distance = 1;
            tempVector = tempVector.getRelative(0, -1, 0);
            // search all needed blocks
            while (this.isBlockNonSolid(tempVector.getLocation().getBlock().getTypeId())) {
                ++distance;
                tempVector = tempVector.getRelative(0, -1, 0);
            }

            // finally protect the blocks
            tempVector = LeverHelper.getAnchor(protection.getVector(), subData);
            for (int i = 0; i < distance; i++) {
                // protect the blocks
                subProtection = new SubProtection(tempVector.getRelative(0, -1 - i, 0), protection);
                protection.addSubProtection(subProtection);
            }
        }

        // register the protection
        getProtectionManager().addProtection(protection);
    }
}
