package de.minestar.moneypit.modules;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.Core;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.EventResult;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.subprotection.SubProtection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.utils.DoorHelper;
import de.minestar.moneypit.utils.PhysicsHelper;

public class Module_IronDoor extends Module {

    private final String NAME = "irondoor";

    public Module_IronDoor(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_IronDoor(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.IRON_DOOR_BLOCK.getId(), NAME);
        this.setDoNeighbourCheck(true);
        this.setBlockRedstone(ymlFile.getBoolean("protect." + NAME + ".handleRedstone", true));
    }

    @Override
    protected void writeExtraConfig(String moduleName, YamlConfiguration ymlFile) {
        ymlFile.set("protect." + NAME + ".handleRedstone", true);
    }

    @Override
    public void addProtection(Protection protection, byte subData) {
        // protect the block above
        SubProtection subProtection = new SubProtection(protection.getVector().getRelative(0, 1, 0), protection);
        protection.addSubProtection(subProtection);

        // protect the block below
        subProtection = new SubProtection(protection.getVector().getRelative(0, -1, 0), protection);
        protection.addSubProtection(subProtection);

        // fetch non-solid-blocks
        PhysicsHelper.protectNonSolidBlocks(protection, subProtection.getVector());

        // protect the second door
        Block[] secondDoor = DoorHelper.getOppositeDoorBlocks(protection.getVector().getLocation().getBlock());
        if (secondDoor[0] != null && secondDoor[1] != null) {
            Block[] firstDoor = DoorHelper.getDoorBlocks(protection.getVector().getLocation().getBlock());
            if (DoorHelper.validateDoorBlocks(firstDoor, secondDoor)) {
                // protect the upper block of the second door
                subProtection = new SubProtection(new BlockVector(secondDoor[1].getLocation()), protection);
                protection.addSubProtection(subProtection);

                // protect the lower block of the second door
                subProtection = new SubProtection(new BlockVector(secondDoor[0].getLocation()), protection);
                protection.addSubProtection(subProtection);

                // protect the block below
                subProtection = new SubProtection(subProtection.getVector().getRelative(0, -1, 0), protection);
                protection.addSubProtection(subProtection);

                // fetch non-solid-blocks
                PhysicsHelper.protectNonSolidBlocks(protection, subProtection.getVector());
            }
        }

        // register the protection
        getProtectionManager().addProtection(protection);
    }

    @Override
    public EventResult onPlace(Player player, BlockVector vector) {
        // search a second chest
        BlockVector doubleDoor = DoorHelper.getSecondWoodDoor(vector);
        if (doubleDoor == null) {
            return new EventResult(false, false, null);
        }

        // check if there is a protection
        Protection protection = Core.protectionManager.getProtection(doubleDoor);
        if (protection == null) {
            return new EventResult(false, false, null);
        }

        // check permissions
        if (!protection.canEdit(player)) {
            PlayerUtils.sendError(player, Core.NAME, "You cannot place a door here.");
            PlayerUtils.sendInfo(player, "The neighbour is a protected door.");
            return new EventResult(true, true, protection);
        }

        // protect the second door
        Block[] secondDoor = DoorHelper.getDoorBlocks(vector.getLocation().getBlock());
        if (secondDoor[0] != null && secondDoor[1] != null) {
            // protect the upper block of the second door
            SubProtection subProtection = new SubProtection(new BlockVector(secondDoor[1].getLocation()), protection);
            protection.addSubProtection(subProtection);
            Core.protectionManager.addSubProtection(subProtection);

            // protect the lower block of the second door
            subProtection = new SubProtection(new BlockVector(secondDoor[0].getLocation()), protection);
            protection.addSubProtection(subProtection);
            Core.protectionManager.addSubProtection(subProtection);

            // protect the block below
            subProtection = new SubProtection(subProtection.getVector().getRelative(0, -1, 0), protection);
            protection.addSubProtection(subProtection);
            Core.protectionManager.addSubProtection(subProtection);

            // fetch non-solid-blocks
            ArrayList<SubProtection> list = PhysicsHelper.protectNonSolidBlocks(protection, subProtection.getVector());
            for (SubProtection sub : list) {
                Core.protectionManager.addSubProtection(sub);
            }

            // send info
            PlayerUtils.sendInfo(player, Core.NAME, "Subprotection created.");
        }

        // return true to abort the event
        return new EventResult(false, true, protection);
    }
}
