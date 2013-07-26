package de.minestar.moneypit.queues;

import com.bukkit.gemo.patchworking.BlockVector;

public interface Queue {

    public boolean execute();

    public BlockVector getVector();
}
