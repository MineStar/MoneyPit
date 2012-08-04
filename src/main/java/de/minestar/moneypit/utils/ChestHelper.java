package de.minestar.moneypit.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import de.minestar.moneypit.data.BlockVector;

public class ChestHelper {
    private final static BlockFace[] faceList = new BlockFace[]{BlockFace.NORTH, BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH};

    public static BlockVector getDoubleChest(BlockVector vector) {
        Block chest = vector.getLocation().getBlock();
        Block dChest;
        for (BlockFace face : faceList) {
            dChest = chest.getRelative(face);
            if (dChest.getTypeId() == Material.CHEST.getId()) {
                return new BlockVector(dChest.getLocation());
            }
        }
        return null;
    }
}
