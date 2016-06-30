package de.minestar.moneypit.modules.door;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.EventResult;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.modules.Module;
import de.minestar.moneypit.utils.DoorHelper;
import de.minestar.moneypit.utils.PhysicsHelper;

public abstract class Module_Door_Abstract extends Module {

    private final String _name;

    public Module_Door_Abstract(YamlConfiguration ymlFile, String name, Material type) {
        _name = name;
        this.writeDefaultConfig(name, ymlFile);
    }

    public Module_Door_Abstract(ModuleManager moduleManager, YamlConfiguration ymlFile, String name, Material type) {
        super();
        _name = name;
        this.init(moduleManager, ymlFile, type, name);
        this.setDoNeighbourCheck(true);
        this.setBlockRedstone(ymlFile.getBoolean("protect." + _name + ".handleRedstone", true));
    }

    @Override
    protected final void writeExtraConfig(String moduleName, YamlConfiguration ymlFile) {
        ymlFile.set("protect." + _name + ".handleRedstone", true);
    }

    @Override
    public final boolean addProtection(IProtection protection, byte subData, boolean saveToDatabase) {
        // protect the block above
        IProtection subProtection = new Protection(protection.getVector().getRelative(0, 1, 0), protection);
        protection.addSubProtection(subProtection);
        MoneyPitCore.databaseManager.createSubProtection(subProtection, saveToDatabase);

        // protect the block below
        subProtection = new Protection(protection.getVector().getRelative(0, -1, 0), protection);
        protection.addSubProtection(subProtection);
        MoneyPitCore.databaseManager.createSubProtection(subProtection, saveToDatabase);

        // fetch non-solid-blocks
        PhysicsHelper.protectNonSolidBlocks(protection, subProtection.getVector(), saveToDatabase);

        // protect the second door
        Block[] secondDoor = DoorHelper.getOppositeDoorBlocks(protection.getVector().getLocation().getBlock());
        if (secondDoor[0] != null && secondDoor[1] != null) {
            Block[] firstDoor = DoorHelper.getDoorBlocks(protection.getVector().getLocation().getBlock());
            if (DoorHelper.validateDoorBlocks(firstDoor, secondDoor)) {
                // protect the upper block of the second door
                subProtection = new Protection(new BlockVector(secondDoor[1].getLocation()), protection);
                protection.addSubProtection(subProtection);
                MoneyPitCore.databaseManager.createSubProtection(subProtection, saveToDatabase);

                // protect the lower block of the second door
                subProtection = new Protection(new BlockVector(secondDoor[0].getLocation()), protection);
                protection.addSubProtection(subProtection);
                MoneyPitCore.databaseManager.createSubProtection(subProtection, saveToDatabase);

                // protect the block below
                subProtection = new Protection(subProtection.getVector().getRelative(0, -1, 0), protection);
                protection.addSubProtection(subProtection);
                MoneyPitCore.databaseManager.createSubProtection(subProtection, saveToDatabase);

                // fetch non-solid-blocks
                PhysicsHelper.protectNonSolidBlocks(protection, subProtection.getVector(), saveToDatabase);
            }
        }

        // register the protection
        return getProtectionManager().addProtection(protection);
    }

    @Override
    public final EventResult onPlace(Player player, BlockVector vector) {
        // search a second chest
        BlockVector doubleDoor = DoorHelper.getSecondDoor(vector, this.getRegisteredType().getId());
        if (doubleDoor == null) {
            return new EventResult(false, false, null);
        }

        // check if there is a protection
        IProtection protection = MoneyPitCore.protectionManager.getProtection(doubleDoor);
        if (protection == null) {
            return new EventResult(false, false, null);
        }

        // check permissions
        if (!protection.canEdit(player)) {
            PlayerUtils.sendError(player, MoneyPitCore.NAME, "You cannot place a door here.");
            PlayerUtils.sendInfo(player, "The neighbour is a protected door.");
            return new EventResult(true, true, protection);
        }

        // protect the second door
        Block[] secondDoor = DoorHelper.getDoorBlocks(vector.getLocation().getBlock());
        if (secondDoor[0] != null && secondDoor[1] != null) {
            // protect the upper block of the second door
            IProtection subProtection = new Protection(new BlockVector(secondDoor[1].getLocation()), protection);
            protection.addSubProtection(subProtection);
            MoneyPitCore.protectionManager.addSubProtection(subProtection);
            MoneyPitCore.databaseManager.createSubProtection(subProtection, true);

            // protect the lower block of the second door
            subProtection = new Protection(new BlockVector(secondDoor[0].getLocation()), protection);
            protection.addSubProtection(subProtection);
            MoneyPitCore.protectionManager.addSubProtection(subProtection);
            MoneyPitCore.databaseManager.createSubProtection(subProtection, true);

            // protect the block below
            subProtection = new Protection(subProtection.getVector().getRelative(0, -1, 0), protection);
            protection.addSubProtection(subProtection);
            MoneyPitCore.protectionManager.addSubProtection(subProtection);
            MoneyPitCore.databaseManager.createSubProtection(subProtection, true);

            // fetch non-solid-blocks
            ArrayList<IProtection> list = PhysicsHelper.protectNonSolidBlocks(protection, subProtection.getVector(), true);
            for (IProtection sub : list) {
                MoneyPitCore.protectionManager.addSubProtection(sub);
            }

            // send info
            PlayerUtils.sendInfo(player, MoneyPitCore.NAME, "Subprotection created.");

        }

        // return true to abort the event
        return new EventResult(false, true, protection);
    }
}
