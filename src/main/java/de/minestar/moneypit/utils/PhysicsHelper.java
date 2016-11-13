package de.minestar.moneypit.utils;

import java.util.ArrayList;

import org.bukkit.Material;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;

import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.protection.Protection;

public class PhysicsHelper {

    public static boolean isBlockNonSolid(Material TYPE) {
        if(!TYPE.isSolid()) return true;  // non solid Blocks
        if(!TYPE.isBlock()) return true; // Liquids, etc
        if(TYPE.hasGravity()) return true;  // gravel / sand
        return false;
     }

    public static ArrayList<IProtection> protectNonSolidBlocks(IProtection protection, BlockVector vectorBelowProtection, boolean saveToDatabase) {
        ArrayList<IProtection> list = new ArrayList<IProtection>();
        IProtection subProtection;
        BlockVector tempVector = vectorBelowProtection.getRelative(0, 0, 0);
        if (PhysicsHelper.isBlockNonSolid(tempVector.getLocation().getBlock().getType())) {
            int distance = 1;
            tempVector = tempVector.getRelative(0, -1, 0);

            // search all needed blocks
            while (PhysicsHelper.isBlockNonSolid(tempVector.getLocation().getBlock().getType())) {
                ++distance;
                tempVector = tempVector.getRelative(0, -1, 0);
            }

            // finally protect the blocks
            for (int i = 0; i < distance; i++) {
                // protect the blocks
                subProtection = new Protection(vectorBelowProtection.getRelative(0, -(i + 1), 0), protection);
                protection.addSubProtection(subProtection);
                list.add(subProtection);
                MoneyPitCore.databaseManager.createSubProtection(subProtection, saveToDatabase);
            }
        }
        return list;
    }
}
