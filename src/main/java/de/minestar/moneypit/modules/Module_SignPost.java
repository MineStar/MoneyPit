package de.minestar.moneypit.modules;

import org.bukkit.Material;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.Protection;
import de.minestar.moneypit.utils.SignHelper;

public class Module_SignPost extends Module {

    public Module_SignPost() {
        super();
        this.init(Material.SIGN_POST.getId(), "Module_SignPost");
    }

    @Override
    public void addProtection(BlockVector vector, Protection protection, byte subData) {
        // add the signpost
        getProtectionManager().addProtection(vector, protection);

        // add the protection to the block below
        getProtectionManager().addProtection(SignHelper.getAnchor(vector, subData), protection);
    }

    @Override
    public void removeProtection(BlockVector vector, Protection protection, byte subData) {
        // remove the signpost
        getProtectionManager().removeProtection(vector, protection);

        // remove the protection from the block below
        getProtectionManager().removeProtection(SignHelper.getAnchor(vector, subData), protection);
    }
}
