package de.minestar.moneypit.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;

import com.bukkit.gemo.utils.BlockUtils;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.protection.ProtectionType;
import de.minestar.moneypit.data.subprotection.SubProtection;
import de.minestar.moneypit.manager.ModuleManager;

public class Module_Chest extends Module {

    private final String NAME = "chest";

    public Module_Chest(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_Chest(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.CHEST.getId(), NAME);
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

    @Override
    public void onNeighbourPlace(Location neighbourLocation) {
        // TODO Auto-generated method stub

    }
}
