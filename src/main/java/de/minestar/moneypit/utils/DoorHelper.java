package de.minestar.moneypit.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import de.minestar.moneypit.data.BlockVector;

public class DoorHelper {
    private final static BlockFace[] faceList = new BlockFace[]{BlockFace.NORTH, BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH};

    public static BlockVector getSecondWoodDoor(BlockVector vector) {
        Block chest = vector.getLocation().getBlock();
        Block dDoor;
        for (BlockFace face : faceList) {
            dDoor = chest.getRelative(face);
            if (dDoor.getTypeId() == Material.WOODEN_DOOR.getId()) {
                return new BlockVector(dDoor.getLocation());
            }
        }
        return null;
    }

    public static BlockVector getSecondIronDoor(BlockVector vector) {
        Block chest = vector.getLocation().getBlock();
        Block dDoor;
        for (BlockFace face : faceList) {
            dDoor = chest.getRelative(face);
            if (dDoor.getTypeId() == Material.IRON_DOOR_BLOCK.getId()) {
                return new BlockVector(dDoor.getLocation());
            }
        }
        return null;
    }
}
