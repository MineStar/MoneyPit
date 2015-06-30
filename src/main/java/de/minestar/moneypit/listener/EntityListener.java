package de.minestar.moneypit.listener;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.utils.UtilPermissions;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.PlayerState;
import de.minestar.moneypit.data.protection.EntityProtection;
import de.minestar.moneypit.data.protection.ProtectionInfo;
import de.minestar.moneypit.entitymodules.EntityModule;
import de.minestar.moneypit.manager.EntityModuleManager;
import de.minestar.moneypit.manager.EntityProtectionManager;
import de.minestar.moneypit.manager.PlayerManager;
import de.minestar.moneypit.manager.QueueManager;
import de.minestar.moneypit.queues.entity.RemoveEntityProtectionQueue;
import de.minestar.moneypit.utils.DoorHelper;

public class EntityListener implements Listener {

    private EntityProtectionManager entityProtectionManager;
    private EntityModuleManager entityModuleManager;
    private PlayerManager playerManager;
    private QueueManager queueManager;

    public EntityListener() {
        this.entityModuleManager = MoneyPitCore.entityModuleManager;
        this.playerManager = MoneyPitCore.playerManager;
        this.entityProtectionManager = MoneyPitCore.entityProtectionManager;
        this.queueManager = MoneyPitCore.queueManager;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity interactedEntity = event.getRightClicked();
        Player player = event.getPlayer();

        // we need an entity and a player
        if (interactedEntity == null || player == null) {
            return;
        }

        // get PlayerState
        final PlayerState state = this.playerManager.getState(event.getPlayer().getName());

        switch (state) {
            case PROTECTION_INFO : {
                // handle info
                this.handleInfoInteract(player, interactedEntity);
                event.setCancelled(true);
                break;
            }
            case PROTECTION_REMOVE : {
                // handle remove
                this.handleRemoveInteract(player, interactedEntity);
                break;
            }
            case PROTECTION_ADD_PRIVATE :
            case PROTECTION_ADD_PUBLIC : {
                // the module must be registered
                EntityModule module = this.entityModuleManager.getRegisteredModule(interactedEntity.getType());
                if (module == null) {
                    PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "Module for entity '" + interactedEntity.getType() + "' is not registered!");
                    return;
                }

                // handle add
                this.handleAddInteract(player, interactedEntity, module, state);
                break;
            }
            case PROTECTION_INVITE : {
                this.handleInviteInteract(player, interactedEntity);
                break;
            }
            case PROTECTION_UNINVITE : {
                this.handleUninviteInteract(player, interactedEntity);
                break;
            }
            case PROTECTION_UNINVITEALL : {
                this.handleUninviteAllInteract(player, interactedEntity);
                break;
            }
            default : {
                // handle normal interact
                this.handleNormalInteract(event, player, interactedEntity);
                break;
            }
        }
    }

    private void handleNormalInteract(PlayerInteractEntityEvent event, Player player, Entity interactedEntity) {
        // we need a protection to show some information about it
        EntityProtection protectedEntity = entityProtectionManager.getProtection(interactedEntity.getUniqueId());
        if (protectedEntity == null) {
            return;
        }

        boolean isAdmin = UtilPermissions.playerCanUseCommand(player, "moneypit.admin");
        // is this protection private?
        if (!protectedEntity.canAccess(player)) {
            // show information about the protection
            this.showInformation(event.getPlayer(), interactedEntity, false);
            // cancel the event
            event.setCancelled(true);
            return;
        }

        if (isAdmin) {
            // show information about the protection
            this.showInformation(player, interactedEntity, false);
        }

        return;
    }

    private void handleUninviteInteract(Player player, Entity interactedEntity) {
        // TODO Auto-generated method stub

    }

    private void handleUninviteAllInteract(Player player, Entity interactedEntity) {
        // TODO Auto-generated method stub

    }

    private void handleInviteInteract(Player player, Entity interactedEntity) {
        // TODO Auto-generated method stub

    }

    private void handleAddInteract(Player player, Entity interactedEntity, EntityModule module, PlayerState state) {
        // TODO Auto-generated method stub

    }

    private void handleRemoveInteract(Player player, Entity interactedEntity) {
        // return to normalmode
        if (!this.playerManager.keepsMode(player.getName())) {
            this.playerManager.setState(player.getName(), PlayerState.NORMAL);
        }

        // we need a protection to show some information about it
        EntityProtection protectedEntity = entityProtectionManager.getProtection(interactedEntity.getUniqueId());
        if (protectedEntity == null) {
            PlayerUtils.sendError(player, MoneyPitCore.NAME, "This '" + interactedEntity.getType() + "' is not protected.");
            return;
        }

        // check permission
        if (!protectedEntity.canEdit(player)) {
            PlayerUtils.sendError(player, MoneyPitCore.NAME, "You are not allowed to remove this protection!");
            return;
        }

        // queue the event for later use in MonitorListener
        RemoveEntityProtectionQueue queue = new RemoveEntityProtectionQueue(player, protectedEntity);
        this.queueManager.addEntityQueue(queue);
    }

    private void handleInfoInteract(Player player, Entity interactedEntity) {
        // return to normalmode
        if (!this.playerManager.keepsMode(player.getName())) {
            this.playerManager.setState(player.getName(), PlayerState.NORMAL);
        }

        // show information
        this.showExtendedInformation(player, interactedEntity);
    }

    private void showInformation(Player player, Entity interactedEntity, boolean showErrorMessage) {
        // we need a protection to show some information about it
        EntityProtection protectedEntity = entityProtectionManager.getProtection(interactedEntity.getUniqueId());
        if (protectedEntity == null) {
            if (showErrorMessage) {
                PlayerUtils.sendError(player, MoneyPitCore.NAME, "This '" + interactedEntity.getType() + "' is not protected.");
            }
            return;
        }

        String message = "This " + ChatColor.RED + protectedEntity.getProtectionType() + " " + protectedEntity.getEntityType() + ChatColor.GRAY + " is protected by " + ChatColor.YELLOW + protectedEntity.getOwner() + ".";
        PlayerUtils.sendInfo(player, message);
        return;

    }

    private void showExtendedInformation(Player player, Entity interactedEntity) {
        // we need a protection to show some information about it
        EntityProtection protectedEntity = entityProtectionManager.getProtection(interactedEntity.getUniqueId());
        if (protectedEntity == null) {
            PlayerUtils.sendError(player, MoneyPitCore.NAME, "This '" + interactedEntity.getType() + "' is not protected.");
            return;
        }

        String message = "This " + ChatColor.RED + protectedEntity.getProtectionType() + " " + protectedEntity.getEntityType() + ChatColor.GRAY + " is protected by " + ChatColor.YELLOW + protectedEntity.getOwner() + ".";
        PlayerUtils.sendInfo(player, message);

        if (protectedEntity.canAccess(player)) {
            HashSet<String> guestList = protectedEntity.getGuestList();
            if (guestList != null) {
                this.displayGuestList(player, guestList);
            }
        }
    }

    private void displayGuestList(Player player, HashSet<String> guestList) {
        PlayerUtils.sendMessage(player, ChatColor.GRAY, "-------------------");
        PlayerUtils.sendMessage(player, ChatColor.DARK_AQUA, "Guestlist:");
        for (String name : guestList) {
            PlayerUtils.sendMessage(player, ChatColor.GRAY, " - " + name);
        }
        PlayerUtils.sendMessage(player, ChatColor.GRAY, "-------------------");
    }

}
