package de.minestar.moneypit.utils;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;

import com.bukkit.gemo.patchworking.BlockVector;

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

    public static BlockVector getDoubleTrappedChest(BlockVector vector) {
        Block chest = vector.getLocation().getBlock();
        Block dChest;
        for (BlockFace face : faceList) {
            dChest = chest.getRelative(face);
            if (dChest.getTypeId() == Material.TRAPPED_CHEST.getId()) {
                return new BlockVector(dChest.getLocation());
            }
        }
        return null;
    }

    // GET DIRECT NEIGHBOURS
    public static ArrayList<Block> getDirectNeighbours(Block block, boolean UpAndDown) {
        ArrayList<Block> result = new ArrayList<Block>();
        result.add(block.getRelative(1, 0, 0));
        result.add(block.getRelative(-1, 0, 0));
        result.add(block.getRelative(0, 0, 1));
        result.add(block.getRelative(0, 0, -1));
        if (UpAndDown) {
            result.add(block.getRelative(0, 1, 0));
            result.add(block.getRelative(0, -1, 0));
        }
        return result;
    }

    // IS DOUBLECHEST?
    public static Chest isDoubleChest(Block chestBlock) {
        if (chestBlock.getTypeId() != Material.CHEST.getId())
            return null;

        ArrayList<Block> neighbours = getDirectNeighbours(chestBlock, false);
        for (int i = 0; i < neighbours.size(); i++) {
            if (neighbours.get(i).getTypeId() == Material.CHEST.getId())
                return (Chest) neighbours.get(i).getState();
        }
        return null;
    }

    // IS DOUBLE-TRAPPED-CHEST?
    public static Chest isDoubleTrappedChest(Block chestBlock) {
        if (chestBlock.getTypeId() != Material.TRAPPED_CHEST.getId())
            return null;

        ArrayList<Block> neighbours = getDirectNeighbours(chestBlock, false);
        for (int i = 0; i < neighbours.size(); i++) {
            if (neighbours.get(i).getTypeId() == Material.TRAPPED_CHEST.getId())
                return (Chest) neighbours.get(i).getState();
        }
        return null;
    }
}
