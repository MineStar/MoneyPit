package de.minestar.moneypit.manager;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Entity;

import com.bukkit.gemo.patchworking.BlockVector;

import de.minestar.moneypit.queues.Queue;
import de.minestar.moneypit.queues.entity.EntityQueue;

public class QueueManager {

    private HashMap<BlockVector, Queue> events = new HashMap<BlockVector, Queue>();
    private HashMap<UUID, EntityQueue> entityEvents = new HashMap<UUID, EntityQueue>();

    public void addQueue(Queue queue) {
        this.events.put(queue.getVector(), queue);
    }

    public Queue getAndRemoveQueue(BlockVector vector) {
        Queue queue = this.events.get(vector);
        this.events.remove(vector);
        return queue;
    }

    public void addEntityQueue(Entity entity, EntityQueue queue) {
        this.entityEvents.put(entity.getUniqueId(), queue);
    }

    public EntityQueue getAndRemoveEntityQueue(UUID uuid) {
        EntityQueue queue = this.entityEvents.get(uuid);
        this.events.remove(uuid);
        return queue;
    }

}
