package de.minestar.moneypit.modules;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import com.bukkit.gemo.patchworking.BlockVector;

import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.subprotection.SubProtection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.utils.DoorHelper;
import de.minestar.moneypit.utils.PhysicsHelper;

public class Module_TrapDoor extends Module {

    private final String NAME = "trapdoor";

    public Module_TrapDoor(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_TrapDoor(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.TRAP_DOOR.getId(), NAME);
        this.setBlockRedstone(ymlFile.getBoolean("protect." + NAME + ".handleRedstone", true));
    }

    @Override
    protected void writeExtraConfig(String moduleName, YamlConfiguration ymlFile) {
        ymlFile.set("protect." + NAME + ".handleRedstone", true);
    }

    @Override
    public boolean addProtection(Protection protection, byte subData) {
        // get the anchor
        BlockVector anchor = DoorHelper.getTrapDoorAnchor(protection.getVector(), subData);

        // protect the block below
        SubProtection subProtection = new SubProtection(anchor, protection);
        protection.addSubProtection(subProtection);

        // fetch non-solid-blocks
        PhysicsHelper.protectNonSolidBlocks(protection, anchor);

        // register the protection
        return getProtectionManager().addProtection(protection);
    }
}
