package de.minestar.moneypit.utils;

import com.bukkit.gemo.patchworking.BlockVector;

public class SignHelper {

    public static BlockVector getAnchor(BlockVector vector, final byte subData) {
        switch (subData) {
            case 2 : {
                return vector.getRelative(0, 0, +1);
            }
            case 3 : {
                return vector.getRelative(0, 0, -1);
            }
            case 4 : {
                return vector.getRelative(+1, 0, 0);
            }
            case 5 : {
                return vector.getRelative(-1, 0, 0);
            }
            default : {
                return vector.getRelative(0, -1, 0);
            }
        }
    }
}
