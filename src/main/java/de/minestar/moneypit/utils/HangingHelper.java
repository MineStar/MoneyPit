package de.minestar.moneypit.utils;

import java.util.ArrayList;
import java.util.HashSet;

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

    private static int getXOffsetForPaintings(final byte subData) {
        switch (subData) {
            case 2 : {
                return -1;
            }
            case 0 : {
                return +1;
            }
            default : {
                return 0;
            }
        }
    }
    
    private static int getZOffsetForPaintings(final byte subData) {
        switch (subData) {
            case 1 : {
                return +1;
            }
            case 3 : {
                return -1;
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
        if (art.getBlockWidth() == 1 && art.getBlockHeight() == 1) {
            return blockList;
        } else if (art.getBlockWidth() == 2 && art.getBlockHeight() == 1) {
            blockList.add(anchor.getRelative(getXOffsetForPaintings(subData), 0, getZOffsetForPaintings(subData)));
        } else if (art.getBlockWidth() == 1 && art.getBlockHeight() == 2) {
            blockList.add(anchor.getRelative(0, 1, 0));
        } else if (art.getBlockWidth() == 2 && art.getBlockHeight() == 2) {
            blockList.add(anchor.getRelative(0, 1, 0));
            blockList.add(anchor.getRelative(getXOffsetForPaintings(subData), 0, getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(getXOffsetForPaintings(subData), 1, getZOffsetForPaintings(subData)));
        } else if (art.getBlockWidth() == 4 && art.getBlockHeight() == 2) {
            blockList.add(anchor.getRelative(0, 1, 0));
            blockList.add(anchor.getRelative(-getXOffsetForPaintings(subData), 0, -getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(-getXOffsetForPaintings(subData), 1, -getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(getXOffsetForPaintings(subData), 0, getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(getXOffsetForPaintings(subData), 1, getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(2 * getXOffsetForPaintings(subData), 0, 2 * getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(2 * getXOffsetForPaintings(subData), 1, 2 * getZOffsetForPaintings(subData)));
        } else if (art.getBlockWidth() == 4 && art.getBlockHeight() == 3) {
            blockList.add(anchor.getRelative(0, 1, 0));
            blockList.add(anchor.getRelative(0, -1, 0));
            blockList.add(anchor.getRelative(-getXOffsetForPaintings(subData), 0, -getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(-getXOffsetForPaintings(subData), 1, -getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(-getXOffsetForPaintings(subData), -1, -getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(getXOffsetForPaintings(subData), 0, getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(getXOffsetForPaintings(subData), 1, getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(getXOffsetForPaintings(subData), -1, getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(2 * getXOffsetForPaintings(subData), 0, 2 * getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(2 * getXOffsetForPaintings(subData), 1, 2 * getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(2 * getXOffsetForPaintings(subData), -1, 2 * getZOffsetForPaintings(subData)));
        } else if (art.getBlockWidth() == 4 && art.getBlockHeight() == 4) {
            blockList.add(anchor.getRelative(0, 1, 0));
            blockList.add(anchor.getRelative(0, 2, 0));
            blockList.add(anchor.getRelative(0, -1, 0));
            blockList.add(anchor.getRelative(-getXOffsetForPaintings(subData), 0, -getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(-getXOffsetForPaintings(subData), 1, -getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(-getXOffsetForPaintings(subData), 2, -getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(-getXOffsetForPaintings(subData), -1, -getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(getXOffsetForPaintings(subData), 0, getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(getXOffsetForPaintings(subData), 1, getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(getXOffsetForPaintings(subData), 2, getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(getXOffsetForPaintings(subData), -1, getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(2 * getXOffsetForPaintings(subData), 0, 2 * getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(2 * getXOffsetForPaintings(subData), 1, 2 * getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(2 * getXOffsetForPaintings(subData), 2, 2 * getZOffsetForPaintings(subData)));
            blockList.add(anchor.getRelative(2 * getXOffsetForPaintings(subData), -1, 2 * getZOffsetForPaintings(subData)));
        }
        return blockList;
    }
}
