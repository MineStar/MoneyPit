package de.minestar.moneypit.queues;

import de.minestar.moneypit.data.BlockVector;

public interface Queue {

    public boolean execute();

    public BlockVector getVector();
}
