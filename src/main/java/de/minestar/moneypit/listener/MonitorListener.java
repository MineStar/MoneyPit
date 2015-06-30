package de.minestar.moneypit.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.bukkit.gemo.patchworking.BlockVector;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.manager.QueueManager;
import de.minestar.moneypit.queues.Queue;
import de.minestar.moneypit.queues.entity.EntityQueue;

public class MonitorListener implements Listener {

    private QueueManager queueManager;
    private BlockVector vector;
    private Queue addQueue, removeQueue, interactQueue;

    public MonitorListener() {
        this.queueManager = MoneyPitCore.queueManager;
        this.vector = new BlockVector("", 0, 0, 0);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        // update the BlockVector
        this.vector.update(event.getBlock().getLocation());

        // get the AddQueue
        this.addQueue = this.queueManager.getAndRemoveQueue(this.vector);
        if (this.addQueue != null) {
            // execute the queue, if the event was not cancelled
            if (!event.isCancelled()) {
                if (!this.addQueue.execute()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        // update the BlockVector
        this.vector.update(event.getBlock().getLocation());

        // get the AddQueue
        this.removeQueue = this.queueManager.getAndRemoveQueue(this.vector);
        if (this.removeQueue != null) {
            // execute the queue, if the event was not cancelled
            if (!event.isCancelled()) {
                if (!this.removeQueue.execute()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHangingBreak(HangingBreakEvent event) {
        // Only handle ItemFrames & Paintings
        if (!event.getEntity().getType().equals(EntityType.ITEM_FRAME) && !event.getEntity().getType().equals(EntityType.PAINTING)) {
            return;
        }

        // update the BlockVector
        this.vector.update(event.getEntity().getLocation());

        // get the AddQueue
        this.removeQueue = this.queueManager.getAndRemoveQueue(this.vector);
        if (this.removeQueue != null) {
            // execute the queue, if the event was not cancelled
            if (!event.isCancelled()) {
                if (!this.removeQueue.execute()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        // Only handle ItemFrames & Paintings
        if (!event.getEntity().getType().equals(EntityType.ITEM_FRAME) && !event.getEntity().getType().equals(EntityType.PAINTING)) {
            return;
        }

        // update the BlockVector
        this.vector.update(event.getEntity().getLocation());

        // get the AddQueue
        this.removeQueue = this.queueManager.getAndRemoveQueue(this.vector);
        if (this.removeQueue != null) {
            // execute the queue, if the event was not cancelled
            if (!event.isCancelled()) {
                if (!this.removeQueue.execute()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHangingInteract(PlayerInteractEntityEvent event) {
        // Only handle ItemFrames & Paintings
        if (!event.getRightClicked().getType().equals(EntityType.ITEM_FRAME) && !event.getRightClicked().getType().equals(EntityType.PAINTING)) {
            return;
        }

        // update the BlockVector
        this.vector.update(event.getRightClicked().getLocation());

        // get the AddQueue
        this.interactQueue = this.queueManager.getAndRemoveQueue(this.vector);
        if (this.interactQueue != null) {
            // execute the queue, if the event was not cancelled
            if (!event.isCancelled()) {
                // execute the event
                if (!this.interactQueue.execute()) {
                    event.setCancelled(true);
                }

                // cancel the event
                event.setCancelled(true);
            } else {
                PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "Could not complete your interact request!");
                PlayerUtils.sendInfo(event.getPlayer(), "The event was cancelled by another plugin.");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {

        // Only handle Left- & Right-Click on a block
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // update the BlockVector
        this.vector.update(event.getClickedBlock().getLocation());

        // get the AddQueue
        this.interactQueue = this.queueManager.getAndRemoveQueue(this.vector);
        if (this.interactQueue != null) {
            // execute the queue, if the event was not cancelled
            if (!event.isCancelled()) {
                // execute the event
                if (!this.interactQueue.execute()) {
                    event.setCancelled(true);
                }

                // cancel the event
                event.setCancelled(true);
            } else {
                PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "Could not complete your interact request!");
                PlayerUtils.sendInfo(event.getPlayer(), "The event was cancelled by another plugin.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity interactedEntity = event.getRightClicked();
        Player player = event.getPlayer();

        // we need an entity and a player
        if (interactedEntity == null || player == null) {
            return;
        }

        // get the queue
        EntityQueue queue = this.queueManager.getAndRemoveEntityQueue(interactedEntity.getUniqueId());
        if (queue != null) {
            // execute the queue, if the event was not cancelled
            if (!event.isCancelled()) {
                // execute the event
                if (!queue.execute()) {
                    event.setCancelled(true);
                }

                // cancel the event
                event.setCancelled(true);
            } else {
                PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "Could not complete your interact request!");
                PlayerUtils.sendInfo(event.getPlayer(), "The event was cancelled by another plugin.");
            }
        }
    }

}
