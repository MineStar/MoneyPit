package de.minestar.moneypit.modules;

import org.bukkit.Material;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.Protection;
import de.minestar.moneypit.data.ProtectionType;
import de.minestar.moneypit.data.SubProtection;
import de.minestar.moneypit.utils.SignHelper;

public class Module_WallSign extends Module {

    public Module_WallSign() {
        super();
        this.init(Material.WALL_SIGN.getId(), "Module_WallSign");
    }

    @Override
    public void addProtection(int ID, BlockVector vector, String owner, ProtectionType type, byte subData) {
        // create the protection
        Protection protection = new Protection(ID, vector, owner, type);

        // protect the block below
        SubProtection subProtection = new SubProtection(SignHelper.getAnchor(vector, subData), protection);
        protection.addSubProtection(subProtection);

        // register the protection
        getProtectionManager().addProtection(protection);
    }
}
