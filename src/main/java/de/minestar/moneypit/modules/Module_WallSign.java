package de.minestar.moneypit.modules;

import org.bukkit.Material;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.Protection;
import de.minestar.moneypit.utils.SignHelper;

public class Module_WallSign extends Module {

    public Module_WallSign() {
        super();
        this.init(Material.WALL_SIGN.getId(), "Module_WallSign");
    }

    @Override
    public void addProtection(BlockVector vector, Protection protection, byte subData) {
        // add the wallsign
        getProtectionManager().addProtection(vector, protection);

        // add the protection to the signanchor
        getProtectionManager().addProtection(SignHelper.getAnchor(vector, subData), protection);
    }

    @Override
    public void removeProtection(BlockVector vector, Protection protection, byte subData) {
        // remove the wallsign
        getProtectionManager().removeProtection(vector, protection);

        // remove the protection from the signanchor
        getProtectionManager().removeProtection(SignHelper.getAnchor(vector, subData), protection);
    }
}
