package de.minestar.moneypit.listener;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import com.bukkit.gemo.patchworking.ProtectionType;
import com.bukkit.gemo.utils.UtilPermissions;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.PlayerState;
import de.minestar.moneypit.data.protection.EntityProtection;
import de.minestar.moneypit.entitymodules.EntityModule;
import de.minestar.moneypit.manager.EntityModuleManager;
import de.minestar.moneypit.manager.EntityProtectionManager;
import de.minestar.moneypit.manager.PlayerManager;
import de.minestar.moneypit.manager.QueueManager;
import de.minestar.moneypit.queues.entity.AddEntityProtectionQueue;
import de.minestar.moneypit.queues.entity.RemoveEntityProtectionQueue;

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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {

        Entity interactedEntity = event.getRightClicked();
        Player player = (Player) event.getPlayer();
        if (handleEntityInteract(player, interactedEntity, false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity interactedEntity = event.getEntity();
        Entity damager = event.getDamager();

        if (damager.getType().equals(EntityType.PLAYER)) {
            Player player = (Player) damager;
            if (this.handleEntityInteract(player, interactedEntity, true)) {
                event.setDamage(0d);
                event.setCancelled(true);
                return;
            }
        } else {
            // always cancel damage by other entities
            if (!this.entityModuleManager.isModuleRegistered(event.getEntityType())) {
                return;
            }

            if (this.entityProtectionManager.hasProtection(event.getEntity().getUniqueId().toString())) {
                event.setDamage(0d);
                event.setCancelled(true);
            }
        }
    }

    private boolean handleEntityInteract(Player player, Entity interactedEntity, boolean isDamageEvent) {
        // we need an entity and a player
        if (interactedEntity == null || player == null) {
            return false;
        }

        if (!entityModuleManager.isModuleRegistered(interactedEntity.getType())) {
            return false;
        }

        // get PlayerState
        final PlayerState state = this.playerManager.getState(player.getName());

        switch (state) {
            case PROTECTION_INFO : {
                // handle info
                this.handleInfoInteract(player, interactedEntity);
                return true;
            }
            case PROTECTION_REMOVE : {
                // handle remove
                return this.handleRemoveInteract(player, interactedEntity);
            }
            case PROTECTION_ADD_PRIVATE :
            case PROTECTION_ADD_PUBLIC : {
                // the module must be registered
                EntityModule module = this.entityModuleManager.getRegisteredModule(interactedEntity.getType());
                if (module == null) {
                    PlayerUtils.sendError(player, MoneyPitCore.NAME, "Module for entity '" + interactedEntity.getType() + "' is not registered!");
                    return true;
                }

                // handle add
                return this.handleAddInteract(player, interactedEntity, module, state);
            }
            case PROTECTION_INVITE : {
                this.handleInviteInteract(player, interactedEntity);
            }
            case PROTECTION_UNINVITE : {
                return this.handleUninviteInteract(player, interactedEntity);
            }
            case PROTECTION_UNINVITEALL : {
                return this.handleUninviteAllInteract(player, interactedEntity);
            }
            default : {
                // handle normal interact
                return this.handleNormalInteract(isDamageEvent, player, interactedEntity);
            }
        }
    }

    private boolean handleNormalInteract(boolean isDamageEvent, Player player, Entity interactedEntity) {
        // we need a protection to show some information about it
        EntityProtection protectedEntity = entityProtectionManager.getProtection(interactedEntity.getUniqueId().toString());
        if (protectedEntity == null) {
            return false;
        }

        boolean isAdmin = UtilPermissions.playerCanUseCommand(player, "moneypit.admin");
        // is this protection private?
        if (!protectedEntity.canAccess(player)) {
            if (!isDamageEvent || !entityModuleManager.getRegisteredModule(protectedEntity.getEntityType()).isEntityDamageSilent()) {
                // show information about the protection
                this.showInformation(player, interactedEntity, false);
            }
            // cancel the event
            return true;
        }

        if (isAdmin) {
            if (!isDamageEvent || !entityModuleManager.getRegisteredModule(protectedEntity.getEntityType()).isEntityDamageSilent()) {
                // show information about the protection
                this.showInformation(player, interactedEntity, false);
            }
            if (isDamageEvent) {
                return true;
            }
        }

        return false;
    }

    private boolean handleUninviteInteract(Player player, Entity interactedEntity) {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean handleUninviteAllInteract(Player player, Entity interactedEntity) {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean handleInviteInteract(Player player, Entity interactedEntity) {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean handleAddInteract(Player player, Entity interactedEntity, EntityModule module, PlayerState state) {
        // return to normalmode
        if (!this.playerManager.keepsMode(player.getName())) {
            this.playerManager.setState(player.getName(), PlayerState.NORMAL);
        }

        // check permissions
        if (!UtilPermissions.playerCanUseCommand(player, "moneypit.protect." + module.getEntityType()) && !UtilPermissions.playerCanUseCommand(player, "moneypit.admin")) {
            PlayerUtils.sendError(player, MoneyPitCore.NAME, "You are not allowed to protect this entity!");
            return true;
        }

        // add protection, if it isn't protected yet
        if (!this.entityProtectionManager.hasProtection(interactedEntity.getUniqueId().toString())) {
            if (state == PlayerState.PROTECTION_ADD_PRIVATE) {
                // create a private protection
                // queue the event for later use in MonitorListener
                AddEntityProtectionQueue queue = new AddEntityProtectionQueue(player, interactedEntity, ProtectionType.PRIVATE);
                this.queueManager.addEntityQueue(interactedEntity, queue);
            } else if (state == PlayerState.PROTECTION_ADD_PUBLIC) {
                // create a public protection
                // queue the event for later use in MonitorListener
                AddEntityProtectionQueue queue = new AddEntityProtectionQueue(player, interactedEntity, ProtectionType.PUBLIC);
                this.queueManager.addEntityQueue(interactedEntity, queue);
            }
            return true;
        } else {
            // Send errormessage
            PlayerUtils.sendError(player, MoneyPitCore.NAME, "Entity is already protected!");

            // show information about the protection
            this.showInformation(player, interactedEntity, false);
            return true;
        }
    }

    private boolean handleRemoveInteract(Player player, Entity interactedEntity) {
        // return to normalmode
        if (!this.playerManager.keepsMode(player.getName())) {
            this.playerManager.setState(player.getName(), PlayerState.NORMAL);
        }

        // we need a protection to show some information about it
        EntityProtection protectedEntity = entityProtectionManager.getProtection(interactedEntity.getUniqueId().toString());
        if (protectedEntity == null) {
            PlayerUtils.sendError(player, MoneyPitCore.NAME, "This '" + interactedEntity.getType() + "' is not protected.");
            return true;
        }

        // check permission
        if (!protectedEntity.canEdit(player)) {
            PlayerUtils.sendError(player, MoneyPitCore.NAME, "You are not allowed to remove this protection!");
            return true;
        }

        // queue the event for later use in MonitorListener
        RemoveEntityProtectionQueue queue = new RemoveEntityProtectionQueue(player, protectedEntity);
        this.queueManager.addEntityQueue(interactedEntity, queue);
        return true;
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
        EntityProtection protectedEntity = entityProtectionManager.getProtection(interactedEntity.getUniqueId().toString());
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
        EntityProtection protectedEntity = entityProtectionManager.getProtection(interactedEntity.getUniqueId().toString());
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
