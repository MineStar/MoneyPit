package de.minestar.moneypit.queues;

import de.minestar.moneypit.data.BlockVector;

public interface Queue {

    public void execute();

    public BlockVector getVector();
}
