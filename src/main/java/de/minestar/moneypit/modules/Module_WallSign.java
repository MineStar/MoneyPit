package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.protection.ProtectionType;
import de.minestar.moneypit.data.subprotection.SubProtection;
import de.minestar.moneypit.manager.ModuleManager;
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
    public void addProtection(int ID, BlockVector vector, String owner, ProtectionType type, byte subData) {
        // create the protection
        Protection protection = new Protection(ID, vector, owner, type);

        // protect the block below
        SubProtection subProtection = new SubProtection(SignHelper.getAnchor(vector, subData), protection);
        protection.addSubProtection(subProtection);

        // FETCH SAND & GRAVEL
        BlockVector tempVector = SignHelper.getAnchor(vector, subData);
        if (this.isBlockNonSolid(tempVector.getLocation().getBlock().getTypeId())) {
            int distance = 1;
            tempVector = tempVector.getRelative(0, -1, 0);
            // search all needed blocks
            while (this.isBlockNonSolid(tempVector.getLocation().getBlock().getTypeId())) {
                ++distance;
                tempVector = tempVector.getRelative(0, -1, 0);
            }

            // finally protect the blocks
            tempVector = SignHelper.getAnchor(vector, subData);
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
