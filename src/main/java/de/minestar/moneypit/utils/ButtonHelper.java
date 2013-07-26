package de.minestar.moneypit.utils;

import com.bukkit.gemo.patchworking.BlockVector;

public class ButtonHelper {
    public static BlockVector getAnchor(BlockVector vector, final byte subData) {
        byte data = (byte) (subData | 0x8);
        switch (data) {
            case 9 : {
                return vector.getRelative(-1, 0, 0);
            }
            case 10 : {
                return vector.getRelative(+1, 0, 0);
            }
            case 11 : {
                return vector.getRelative(0, 0, -1);
            }
            case 12 : {
                return vector.getRelative(0, 0, +1);
            }
            default : {
                return vector.getRelative(0, 0, 0);
            }
        }
    }
}
