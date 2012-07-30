package de.minestar.moneypit.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import de.minestar.moneypit.data.BlockVector;

public class DoorHelper {
    private final static BlockFace[] faceList = new BlockFace[]{BlockFace.NORTH, BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH};

    public static BlockVector getSecondWoodDoor(BlockVector vector) {
        Block door = vector.getLocation().getBlock();
        Block dDoor;
        for (BlockFace face : faceList) {
            dDoor = door.getRelative(face);
            if (dDoor.getTypeId() == Material.WOODEN_DOOR.getId()) {
                return new BlockVector(dDoor.getLocation());
            }
        }
        return null;
    }

    public static BlockVector getSecondIronDoor(BlockVector vector) {
        Block door = vector.getLocation().getBlock();
        Block dDoor;
        for (BlockFace face : faceList) {
            dDoor = door.getRelative(face);
            if (dDoor.getTypeId() == Material.IRON_DOOR_BLOCK.getId()) {
                return new BlockVector(dDoor.getLocation());
            }
        }
        return null;
    }

    public static BlockVector getTrapDoorAnchor(BlockVector vector, final byte subData) {
        byte data = (byte) (subData | 0x4);
        switch (data) {
            case 4 : {
                return vector.getRelative(0, 0, +1);
            }
            case 5 : {
                return vector.getRelative(0, 0, -1);
            }
            case 6 : {
                return vector.getRelative(+1, 0, 0);
            }
            case 7 : {
                return vector.getRelative(-1, 0, 0);
            }
            default : {
                return vector.getRelative(0, 0, 0);
            }
        }
    }
}
