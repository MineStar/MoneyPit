package de.minestar.moneypit.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.subprotection.SubProtection;

public class PhysicsHelper {

    private static final Set<Integer> nonSolidStateBlocks = new HashSet<Integer>(Arrays.asList(6, 8, 9, 10, 11, 12, 13, 18, 26, 27, 28, 31, 32, 34, 37, 38, 39, 40, 46, 50, 51, 55, 59, 63, 64, 65, 66, 68, 69, 70, 71, 72, 75, 76, 77, 78, 79, 81, 83, 93, 94, 96, 103, 104, 105, 106, 111, 115, 127, 131, 132));

    public static boolean isBlockNonSolid(int ID) {
        return nonSolidStateBlocks.contains(ID);
    }

    public static ArrayList<SubProtection> protectNonSolidBlocks(Protection protection, BlockVector vectorBelowProtection) {
        ArrayList<SubProtection> list = new ArrayList<SubProtection>();
        SubProtection subProtection;
        BlockVector tempVector = vectorBelowProtection.getRelative(0, 0, 0);
        if (PhysicsHelper.isBlockNonSolid(tempVector.getLocation().getBlock().getTypeId())) {
            int distance = 1;
            tempVector = tempVector.getRelative(0, -1, 0);
            // search all needed blocks
            while (PhysicsHelper.isBlockNonSolid(tempVector.getLocation().getBlock().getTypeId())) {
                ++distance;
                tempVector = tempVector.getRelative(0, -1, 0);
            }

            // finally protect the blocks
            for (int i = 0; i < distance; i++) {
                // protect the blocks
                subProtection = new SubProtection(vectorBelowProtection.getRelative(0, -(i + 1), 0), protection);
                protection.addSubProtection(subProtection);
                list.add(subProtection);
            }
        }
        return list;
    }
}
