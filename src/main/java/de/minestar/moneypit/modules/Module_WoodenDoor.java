package de.minestar.moneypit.modules;

import org.bukkit.Material;
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

public class Module_WoodenDoor extends Module {

    private final String NAME = "wooddoor";

    public Module_WoodenDoor(YamlConfiguration ymlFile) {
        this.writeDefaultConfig(NAME, ymlFile);
    }

    public Module_WoodenDoor(ModuleManager moduleManager, YamlConfiguration ymlFile) {
        super();
        this.init(moduleManager, ymlFile, Material.WOODEN_DOOR.getId(), NAME);
        this.setDoNeighbourCheck(true);
        this.setBlockRedstone(ymlFile.getBoolean("protect." + NAME + ".handleRedstone", true));
    }

    @Override
    protected void writeExtraConfig(String moduleName, YamlConfiguration ymlFile) {
        ymlFile.set("protect." + NAME + ".handleRedstone", true);
    }

    @Override
    public void addProtection(Protection protection, byte subData) {
        // protect the block below
        SubProtection subProtection = new SubProtection(protection.getVector().getRelative(0, -1, 0), protection);
        protection.addSubProtection(subProtection);

        // protect the block above
        subProtection = new SubProtection(protection.getVector().getRelative(0, 1, 0), protection);
        protection.addSubProtection(subProtection);

        // FETCH SAND & GRAVEL
        BlockVector tempVector = protection.getVector().getRelative(0, -1, 0);
        if (this.isBlockNonSolid(tempVector.getLocation().getBlock().getTypeId())) {
            int distance = 1;
            tempVector = tempVector.getRelative(0, -1, 0);
            // search all needed blocks
            while (this.isBlockNonSolid(tempVector.getLocation().getBlock().getTypeId())) {
                ++distance;
                tempVector = tempVector.getRelative(0, -1, 0);
            }

            // finally protect the blocks
            tempVector = protection.getVector().getRelative(0, -1, 0);
            for (int i = 0; i < distance; i++) {
                // protect the blocks
                subProtection = new SubProtection(tempVector.getRelative(0, -1 - i, 0), protection);
                protection.addSubProtection(subProtection);
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

        // send info
        PlayerUtils.sendInfo(player, Core.NAME, "Subprotection created.");

        // return true to abort the event
        return new EventResult(false, true, protection);
    }
}
