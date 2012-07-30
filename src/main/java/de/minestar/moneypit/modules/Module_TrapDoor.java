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
        this.setHandleRedstone(true);
    }

    @Override
    public void addProtection(int ID, BlockVector vector, String owner, ProtectionType type, byte subData) {
        // create the protection
        Protection protection = new Protection(ID, vector, owner, type);

        // protect the block below
        SubProtection subProtection = new SubProtection(DoorHelper.getTrapDoorAnchor(vector, subData), protection);
        protection.addSubProtection(subProtection);

        // register the protection
        getProtectionManager().addProtection(protection);
    }
}
