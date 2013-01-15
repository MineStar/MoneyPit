package de.minestar.moneypit.utils;

import de.minestar.moneypit.data.BlockVector;

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
}
