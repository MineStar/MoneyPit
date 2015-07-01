package de.minestar.moneypit.listener;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity interactedEntity = event.getEntity();
        Entity damager = event.getDamager();
        if (damager.getType().equals(EntityType.PLAYER)) {
            Player player = (Player) damager;

            // we need an entity and a player
            if (interactedEntity == null || player == null) {
                return;
            }

            // get PlayerState
            final PlayerState state = this.playerManager.getState(player.getName());

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
                        PlayerUtils.sendError(player, MoneyPitCore.NAME, "Module for entity '" + interactedEntity.getType() + "' is not registered!");
                        return;
                    }

                    // handle add
                    this.handleAddInteract(event, player, interactedEntity, module, state);
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
        } else {
            if (!this.entityModuleManager.isModuleRegistered(event.getEntityType())) {
                return;
            }

            if (this.entityProtectionManager.hasProtection(event.getEntity().getUniqueId())) {
                event.setDamage(0d);
                event.setCancelled(true);
            }
        }
    }

    private void handleNormalInteract(EntityDamageByEntityEvent event, Player player, Entity interactedEntity) {
        // we need a protection to show some information about it
        EntityProtection protectedEntity = entityProtectionManager.getProtection(interactedEntity.getUniqueId());
        if (protectedEntity == null) {
            return;
        }

        boolean isAdmin = UtilPermissions.playerCanUseCommand(player, "moneypit.admin");
        // is this protection private?
        if (!protectedEntity.canAccess(player)) {
            // show information about the protection
            this.showInformation((Player) event.getDamager(), interactedEntity, false);
            // cancel the event
            event.setCancelled(true);
            return;
        }

        if (isAdmin) {
            // show information about the protection
            this.showInformation(player, interactedEntity, false);
        }

        LivingEntity livingEntity = (LivingEntity) interactedEntity;
        if (event.getDamage() >= livingEntity.getHealth()) {
            // queue the event for later use in MonitorListener
            RemoveEntityProtectionQueue queue = new RemoveEntityProtectionQueue(player, protectedEntity);
            this.queueManager.addEntityQueue(interactedEntity, queue);
        }
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

    private void handleAddInteract(EntityDamageByEntityEvent event, Player player, Entity interactedEntity, EntityModule module, PlayerState state) {
        // return to normalmode
        if (!this.playerManager.keepsMode(player.getName())) {
            this.playerManager.setState(player.getName(), PlayerState.NORMAL);
        }

        // check permissions
        if (!UtilPermissions.playerCanUseCommand(player, "moneypit.protect." + module.getEntityType()) && !UtilPermissions.playerCanUseCommand(player, "moneypit.admin")) {
            PlayerUtils.sendError(player, MoneyPitCore.NAME, "You are not allowed to protect this entity!");
            return;
        }

        // add protection, if it isn't protected yet
        if (!this.entityProtectionManager.hasProtection(interactedEntity.getUniqueId())) {
            if (state == PlayerState.PROTECTION_ADD_PRIVATE) {
                // create a private protection
                // queue the event for later use in MonitorListener
                AddEntityProtectionQueue queue = new AddEntityProtectionQueue((Player) event.getDamager(), interactedEntity, ProtectionType.PRIVATE);
                this.queueManager.addEntityQueue(interactedEntity, queue);
            } else if (state == PlayerState.PROTECTION_ADD_PUBLIC) {
                // create a public protection
                // queue the event for later use in MonitorListener
                AddEntityProtectionQueue queue = new AddEntityProtectionQueue((Player) event.getDamager(), interactedEntity, ProtectionType.PUBLIC);
                this.queueManager.addEntityQueue(interactedEntity, queue);
            } else {
                event.setCancelled(true);
            }
        } else {
            // Send errormessage
            PlayerUtils.sendError(player, MoneyPitCore.NAME, "Entity is already protected!");
            event.setCancelled(true);

            // show information about the protection
            this.showInformation(player, interactedEntity, false);
        }
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
        this.queueManager.addEntityQueue(interactedEntity, queue);
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
