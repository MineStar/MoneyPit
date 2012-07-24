package de.minestar.moneypit.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.protection.ProtectionType;
import de.minestar.moneypit.data.subprotection.SubProtection;
import de.minestar.moneypit.manager.ModuleManager;

public class Module_SignPost extends Module {

    private final String NAME = "signpost";

    public Module_SignPost(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_SignPost(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.SIGN_POST.getId(), NAME);
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

    @Override
    public void onNeighbourPlace(Location neighbourLocation) {
        // TODO Auto-generated method stub

    }
}
