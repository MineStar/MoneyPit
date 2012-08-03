package de.minestar.moneypit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import de.minestar.moneypit.Core;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.manager.QueueManager;
import de.minestar.moneypit.queues.AddQueue;

public class MonitorListener implements Listener {

    private QueueManager queueManager;
    private BlockVector vector;
    private AddQueue addQueue;

    public MonitorListener() {
        this.queueManager = Core.queueManager;
        this.vector = new BlockVector("", 0, 0, 0);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        // update the BlockVector
        this.vector.update(event.getBlock().getLocation());

        // get the AddQueue
        this.addQueue = this.queueManager.getAndRemoveAddQueue(this.vector);
        if (this.addQueue != null) {
            // execute the queue, if the event was not cancelled
            if (!event.isCancelled()) {
                this.addQueue.execute();
            }
        }
    }
}
