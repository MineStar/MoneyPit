package de.minestar.moneypit.manager;

import java.util.HashMap;

import com.bukkit.gemo.patchworking.BlockVector;

import de.minestar.moneypit.queues.Queue;

public class QueueManager {

    private HashMap<BlockVector, Queue> events = new HashMap<BlockVector, Queue>();

    public void addQueue(Queue queue) {
        this.events.put(queue.getVector(), queue);
    }

    public Queue getAndRemoveQueue(BlockVector vector) {
        Queue queue = this.events.get(vector);
        this.events.remove(vector);
        return queue;
    }

}
