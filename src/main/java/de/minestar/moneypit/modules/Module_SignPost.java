package de.minestar.moneypit.modules;

import org.bukkit.Material;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.Protection;
import de.minestar.moneypit.data.ProtectionType;
import de.minestar.moneypit.data.SubProtection;

public class Module_SignPost extends Module {

    public Module_SignPost() {
        super();
        this.init(Material.SIGN_POST.getId(), "signpost");
    }

    @Override
    public void addProtection(int ID, BlockVector vector, String owner, ProtectionType type, byte subData) {
        // create the protection
        Protection protection = new Protection(ID, vector, owner, type);

        // protect the block below
        SubProtection subProtection = new SubProtection(vector.getRelative(0, -1, 0), protection);
        protection.addSubProtection(subProtection);

        // register the protection
        getProtectionManager().addProtection(protection);
    }
}
