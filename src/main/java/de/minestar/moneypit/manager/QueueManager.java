package de.minestar.moneypit.manager;

import java.util.HashMap;

import org.bukkit.entity.Entity;

import com.bukkit.gemo.patchworking.BlockVector;

import de.minestar.moneypit.queues.Queue;
import de.minestar.moneypit.queues.entity.EntityQueue;

public class QueueManager {

    private HashMap<BlockVector, Queue> events = new HashMap<BlockVector, Queue>();
    private HashMap<String, EntityQueue> entityEvents = new HashMap<String, EntityQueue>();

    public void addQueue(Queue queue) {
        this.events.put(queue.getVector(), queue);
    }

    public Queue getAndRemoveQueue(BlockVector vector) {
        Queue queue = this.events.get(vector);
        this.events.remove(vector);
        return queue;
    }

    public void addEntityQueue(Entity entity, EntityQueue queue) {
        this.entityEvents.put(entity.getUniqueId().toString(), queue);
    }

    public EntityQueue getAndRemoveEntityQueue(String uuid) {
        EntityQueue queue = this.entityEvents.get(uuid);
        this.entityEvents.remove(uuid);
        return queue;
    }

}
