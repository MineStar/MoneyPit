package de.minestar.moneypit.utils;

import java.util.ArrayList;

import org.bukkit.Art;
import org.bukkit.entity.Painting;

import com.bukkit.gemo.patchworking.BlockVector;

public class HangingHelper {

    public static BlockVector getAnchor(BlockVector vector, final byte subData) {
        switch (subData) {
            case 1 : {
                return vector.getRelative(1, 0, 0);
            }
            case 3 : {
                return vector.getRelative(-1, 0, 0);
            }
            case 2 : {
                return vector.getRelative(0, 0, +1);
            }
            case 0 : {
                return vector.getRelative(0, 0, -1);
            }
            default : {
                return vector.getRelative(0, 0, 0);
            }
        }
    }

    public static int getXOffsetForPaintings(final byte subData) {
        switch (subData) {
            case 0 : {
                return +1;
            }
            case 2 : {
                return +1;
            }
            default : {
                return 0;
            }
        }
    }

    public static int getZOffsetForPaintings(final byte subData) {
        switch (subData) {
            case 1 : {
                return +1;
            }
            case 3 : {
                return +1;
            }
            default : {
                return 0;
            }
        }
    }
    public static ArrayList<BlockVector> getSubProtectedPaintingBlocks(BlockVector vector, Painting painting) {
        // create a new list
        ArrayList<BlockVector> blockList = new ArrayList<BlockVector>();

        // get the anchor
        final byte subData = (byte) painting.getAttachedFace().ordinal();
        BlockVector anchor = getAnchor(vector, subData);
        Art art = painting.getArt();
        blockList.add(anchor);
        int xOffset = getXOffsetForPaintings(subData);
        int zOffset = getZOffsetForPaintings(subData);
        int blockWidth = art.getBlockWidth();
        int blockHeight = art.getBlockHeight();
        if (blockWidth == 1 && blockHeight == 1) {
            return blockList;
        } else if (blockWidth == 2 && blockHeight == 1) {
            blockList.add(anchor.getRelative(-xOffset, 0, -zOffset));
        } else if (blockWidth == 1 && blockHeight == 2) {
            blockList.add(anchor.getRelative(0, -1, 0));
        } else if (blockWidth == 2 && blockHeight == 2) {
            blockList.add(anchor.getRelative(0, -1, 0));
            blockList.add(anchor.getRelative(-xOffset, 0, -zOffset));
            blockList.add(anchor.getRelative(-xOffset, -1, -zOffset));
        } else if (blockWidth == 4 && blockHeight == 2) {
            blockList.add(anchor.getRelative(0, -1, 0));
            blockList.add(anchor.getRelative(xOffset, 0, zOffset));
            blockList.add(anchor.getRelative(xOffset, -1, zOffset));
            blockList.add(anchor.getRelative(-xOffset, 0, -zOffset));
            blockList.add(anchor.getRelative(-xOffset, -1, -zOffset));
            blockList.add(anchor.getRelative(-2 * xOffset, 0, -2 * zOffset));
            blockList.add(anchor.getRelative(-2 * xOffset, -1, -2 * zOffset));
        } else if (blockWidth == 4 && blockHeight == 3) {
            blockList.add(anchor.getRelative(0, 1, 0));
            blockList.add(anchor.getRelative(0, -1, 0));
            blockList.add(anchor.getRelative(xOffset, 0, zOffset));
            blockList.add(anchor.getRelative(xOffset, 1, zOffset));
            blockList.add(anchor.getRelative(xOffset, -1, zOffset));
            blockList.add(anchor.getRelative(-xOffset, 0, -zOffset));
            blockList.add(anchor.getRelative(-xOffset, 1, -zOffset));
            blockList.add(anchor.getRelative(-xOffset, -1, -zOffset));
            blockList.add(anchor.getRelative(-2 * xOffset, 0, -2 * zOffset));
            blockList.add(anchor.getRelative(-2 * xOffset, 1, -2 * zOffset));
            blockList.add(anchor.getRelative(-2 * xOffset, -1, -2 * zOffset));
        } else if (blockWidth == 4 && blockHeight == 4) {
            blockList.add(anchor.getRelative(0, 1, 0));
            blockList.add(anchor.getRelative(0, -1, 0));
            blockList.add(anchor.getRelative(0, -2, 0));
            blockList.add(anchor.getRelative(xOffset, 0, zOffset));
            blockList.add(anchor.getRelative(xOffset, 1, zOffset));
            blockList.add(anchor.getRelative(xOffset, -1, zOffset));
            blockList.add(anchor.getRelative(xOffset, -2, zOffset));
            blockList.add(anchor.getRelative(-xOffset, 0, -zOffset));
            blockList.add(anchor.getRelative(-xOffset, 1, -zOffset));
            blockList.add(anchor.getRelative(-xOffset, -1, -zOffset));
            blockList.add(anchor.getRelative(-xOffset, -2, -zOffset));
            blockList.add(anchor.getRelative(-2 * xOffset, 0, -2 * zOffset));
            blockList.add(anchor.getRelative(-2 * xOffset, 1, -2 * zOffset));
            blockList.add(anchor.getRelative(-2 * xOffset, -1, -2 * zOffset));
            blockList.add(anchor.getRelative(-2 * xOffset, -2, -2 * zOffset));
        }
        return blockList;
    }
}
