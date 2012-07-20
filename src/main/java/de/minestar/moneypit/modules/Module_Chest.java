package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.block.Chest;

import com.bukkit.gemo.utils.BlockUtils;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.Protection;

public class Module_Chest extends Module {

    public Module_Chest() {
        super();
        this.init(Material.CHEST.getId(), "Module_Chest");
    }

    @Override
    public void addProtection(BlockVector vector, Protection protection, byte subData) {
        // add the normal chest
        getProtectionManager().addProtection(vector, protection);

        // search a second chest and add the protection, if found
        Chest secondChest = BlockUtils.isDoubleChest(vector.getLocation().getBlock());
        if (secondChest != null) {
            getProtectionManager().addProtection(new BlockVector(secondChest.getLocation()), protection);
        }
    }

    @Override
    public void removeProtection(BlockVector vector, Protection protection, byte subData) {
        // remove the normal chest
        getProtectionManager().removeProtection(vector, protection);

        // search a second chest and remove the protection, if found
        Chest secondChest = BlockUtils.isDoubleChest(vector.getLocation().getBlock());
        if (secondChest != null) {
            getProtectionManager().removeProtection(new BlockVector(secondChest.getLocation()), protection);
        }
    }
}
