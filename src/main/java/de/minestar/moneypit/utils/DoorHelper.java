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

    public static boolean validateDoorBlocks(Block[] firstDoor, Block[] secondDoor) {
        if ((firstDoor[0].getData() == secondDoor[0].getData() - 4 || firstDoor[0].getData() == secondDoor[0].getData() + 4 || firstDoor[0].getData() == secondDoor[0].getData()) && (firstDoor[1].getData() == secondDoor[1].getData() + 1 || firstDoor[1].getData() == secondDoor[1].getData() - 1))
            return true;
        return false;
    }

    public static Block getLowerDoorPart(Block block) {
        if (block.getData() < 8) {
            return block;
        } else {
            return block.getRelative(BlockFace.DOWN);
        }
    }

    public static Block getUpperDoorPart(Block block) {
        if (block.getData() < 8) {
            return block.getRelative(BlockFace.UP);
        } else {
            return block;
        }
    }

    public static void toggleDoor(Block block) {
        Block lower = DoorHelper.getLowerDoorPart(block);
        if (lower.getData() < 4) {
            DoorHelper.openDoor(lower);
        } else {
            DoorHelper.closeDoor(lower);
        }
    }

    public static void openDoor(Block block) {
        Block[] doorBlocks = DoorHelper.getDoorBlocks(block);
        if (doorBlocks[0] != null && doorBlocks[1] != null) {
            doorBlocks[0].setData((byte) (doorBlocks[0].getData() + 4), false);
            doorBlocks[1].setData((byte) (doorBlocks[1].getData()), false);
        }
    }

    public static void closeDoor(Block block) {
        Block[] doorBlocks = DoorHelper.getDoorBlocks(block);
        if (doorBlocks[0] != null && doorBlocks[1] != null) {
            doorBlocks[0].setData((byte) (doorBlocks[0].getData() - 4), false);
            doorBlocks[1].setData((byte) (doorBlocks[1].getData()), false);
        }
    }

    public static void toggleSecondDoor(Block block) {
        Block lower = DoorHelper.getLowerDoorPart(block);
        if (lower.getData() < 4) {
            DoorHelper.openSecondDoor(lower);
        } else {
            DoorHelper.closeSecondDoor(lower);
        }
    }

    public static void openSecondDoor(Block block) {
        Block[] secondDoor = DoorHelper.getOppositeDoorBlocks(block);
        if (secondDoor[0] != null && secondDoor[1] != null) {
            if (DoorHelper.validateDoorBlocks(getDoorBlocks(block), secondDoor)) {
                secondDoor[0].setData((byte) (secondDoor[0].getData() + 4), false);
                secondDoor[1].setData((byte) (secondDoor[1].getData()), false);
            }
        }
    }

    public static void closeSecondDoor(Block block) {
        Block[] secondDoor = DoorHelper.getOppositeDoorBlocks(block);
        if (secondDoor[0] != null && secondDoor[1] != null) {
            if (DoorHelper.validateDoorBlocks(getDoorBlocks(block), secondDoor)) {
                secondDoor[0].setData((byte) (secondDoor[0].getData() - 4), false);
                secondDoor[1].setData((byte) (secondDoor[1].getData()), false);
            }
        }
    }

    public static Block[] getOppositeDoorBlocks(Block block) {
        Block[] list = new Block[2];
        list[0] = DoorHelper.getOppositeLowerDoorPart(block);
        list[1] = DoorHelper.getOppositeUpperDoorPart(block);
        return list;
    }

    public static Block[] getDoorBlocks(Block block) {
        Block[] list = new Block[2];
        list[0] = DoorHelper.getLowerDoorPart(block);
        list[1] = DoorHelper.getUpperDoorPart(block);
        return list;
    }

    public static Block getOppositeUpperDoorPart(Block block) {
        Block selfLower = DoorHelper.getLowerDoorPart(block);
        Block selfUpper = DoorHelper.getUpperDoorPart(block);
        final byte dataLower = selfLower.getData();
        final byte dataUpper = selfUpper.getData();
        switch (dataLower) {
            case 0 : {
                if (dataUpper == 8)
                    return selfUpper.getRelative(0, 0, +1);
                else
                    return selfUpper.getRelative(0, 0, -1);
            }
            case 1 : {
                if (dataUpper == 8)
                    return selfUpper.getRelative(-1, 0, 0);
                else
                    return selfUpper.getRelative(+1, 0, 0);
            }
            case 2 : {
                if (dataUpper == 8)
                    return selfUpper.getRelative(0, 0, -1);
                else
                    return selfUpper.getRelative(0, 0, +1);
            }
            case 3 : {
                if (dataUpper == 8)
                    return selfUpper.getRelative(+1, 0, 0);
                else
                    return selfUpper.getRelative(-1, 0, 0);
            }
            case 4 : {
                if (dataUpper == 8)
                    return selfUpper.getRelative(0, 0, +1);
                else
                    return selfUpper.getRelative(0, 0, -1);
            }
            case 5 : {
                if (dataUpper == 8)
                    return selfUpper.getRelative(-1, 0, 0);
                else
                    return selfUpper.getRelative(+1, 0, 0);
            }
            case 6 : {
                if (dataUpper == 8)
                    return selfUpper.getRelative(0, 0, -1);
                else
                    return selfUpper.getRelative(0, 0, +1);
            }
            case 7 : {
                if (dataUpper == 8)
                    return selfUpper.getRelative(+1, 0, 0);
                else
                    return selfUpper.getRelative(-1, 0, 0);
            }
            default : {
                return null;
            }
        }
    }

    public static Block getOppositeLowerDoorPart(Block block) {
        Block upper = DoorHelper.getOppositeUpperDoorPart(block);
        if (upper == null) {
            return null;
        } else {
            return upper.getRelative(BlockFace.DOWN);
        }
    }
}
