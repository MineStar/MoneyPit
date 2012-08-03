package de.minestar.moneypit.manager;

import java.util.HashMap;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.queues.AddQueue;

public class QueueManager {

    private HashMap<BlockVector, AddQueue> placeEvents = new HashMap<BlockVector, AddQueue>();

    public void addBlockPlace(AddQueue addQueue) {
        this.placeEvents.put(addQueue.getVector(), addQueue);
    }

    public AddQueue getAndRemoveAddQueue(BlockVector vector) {
        AddQueue queue = this.placeEvents.get(vector);
        this.placeEvents.remove(vector);
        return queue;
    }

}
