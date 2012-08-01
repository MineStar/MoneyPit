package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.protection.ProtectionType;
import de.minestar.moneypit.data.subprotection.SubProtection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.utils.DoorHelper;

public class Module_TrapDoor extends Module {

    private final String NAME = "trapdoor";

    public Module_TrapDoor(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_TrapDoor(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.TRAP_DOOR.getId(), NAME);
        this.setHandleRedstone(ymlFile.getBoolean("protect." + NAME + ".handleRedstone", true));
    }

    @Override
    protected void writeExtraConfig(String moduleName, YamlConfiguration ymlFile) {
        ymlFile.set("protect." + NAME + ".handleRedstone", true);
    }

    @Override
    public void addProtection(int ID, BlockVector vector, String owner, ProtectionType type, byte subData) {
        // create the protection
        Protection protection = new Protection(ID, vector, owner, type);

        // protect the block below
        SubProtection subProtection = new SubProtection(DoorHelper.getTrapDoorAnchor(vector, subData), protection);
        protection.addSubProtection(subProtection);

        // FETCH SAND & GRAVEL
        BlockVector tempVector = DoorHelper.getTrapDoorAnchor(vector, subData);
        if (tempVector.getLocation().getBlock().getTypeId() == Material.SAND.getId() || tempVector.getLocation().getBlock().getTypeId() == Material.GRAVEL.getId()) {
            int distance = 1;
            tempVector = tempVector.getRelative(0, -1, 0);
            // search all needed blocks
            while (tempVector.getLocation().getBlock().getTypeId() == Material.SAND.getId() || tempVector.getLocation().getBlock().getTypeId() == Material.GRAVEL.getId()) {
                ++distance;
                tempVector = tempVector.getRelative(0, -1, 0);
            }

            // finally protect the blocks
            tempVector = DoorHelper.getTrapDoorAnchor(vector, subData);
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
