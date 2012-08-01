package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.protection.ProtectionType;
import de.minestar.moneypit.data.subprotection.SubProtection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.utils.ButtonHelper;

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
    public void addProtection(int ID, BlockVector vector, String owner, ProtectionType type, byte subData) {
        // create the protection
        Protection protection = new Protection(ID, vector, owner, type);

        // protect the block below
        SubProtection subProtection = new SubProtection(ButtonHelper.getAnchor(vector, subData), protection);
        protection.addSubProtection(subProtection);

        // FETCH SAND & GRAVEL
        BlockVector tempVector = ButtonHelper.getAnchor(vector, subData);
        if (tempVector.getLocation().getBlock().getTypeId() == Material.SAND.getId() || tempVector.getLocation().getBlock().getTypeId() == Material.GRAVEL.getId()) {
            int distance = 1;
            tempVector = vector.getRelative(0, -2, 0);
            // search all needed blocks
            while (tempVector.getLocation().getBlock().getTypeId() == Material.SAND.getId() || tempVector.getLocation().getBlock().getTypeId() == Material.GRAVEL.getId()) {
                ++distance;
                tempVector = tempVector.getRelative(0, -1, 0);
            }

            // finally protect the blocks
            tempVector = ButtonHelper.getAnchor(vector, subData);
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
