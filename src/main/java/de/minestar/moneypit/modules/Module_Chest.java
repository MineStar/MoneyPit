package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.block.Chest;

import com.bukkit.gemo.utils.BlockUtils;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.protection.ProtectionType;
import de.minestar.moneypit.data.subprotection.SubProtection;

public class Module_Chest extends Module {

    public Module_Chest() {
        super();
        this.init(Material.CHEST.getId(), "chest");
    }

    @Override
    public void addProtection(int ID, BlockVector vector, String owner, ProtectionType type, byte subData) {
        // create the protection
        Protection protection = new Protection(ID, vector, owner, type);

        // search a second chest and add the subprotection, if found
        Chest secondChest = BlockUtils.isDoubleChest(vector.getLocation().getBlock());
        if (secondChest != null) {
            SubProtection subProtection = new SubProtection(new BlockVector(secondChest.getLocation()), protection);
            protection.addSubProtection(subProtection);
        }

        // register the protection
        getProtectionManager().addProtection(protection);
    }
}
