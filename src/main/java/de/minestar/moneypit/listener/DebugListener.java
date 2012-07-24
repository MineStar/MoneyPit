package de.minestar.moneypit.listener;

import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.bukkit.gemo.utils.UtilPermissions;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.Core;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.PlayerState;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.protection.ProtectionInfo;
import de.minestar.moneypit.data.protection.ProtectionType;
import de.minestar.moneypit.data.subprotection.SubProtectionHolder;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.manager.PlayerManager;
import de.minestar.moneypit.manager.ProtectionManager;
import de.minestar.moneypit.modules.Module;

public class DebugListener implements Listener {

    private ModuleManager moduleManager;
    private PlayerManager playerManager;
    private ProtectionManager protectionManager;

    private BlockVector vector;
    private ProtectionInfo protectionInfo;

    public DebugListener() {
        this.moduleManager = Core.moduleManager;
        this.playerManager = Core.playerManager;
        this.protectionManager = Core.protectionManager;
        this.vector = new BlockVector("", 0, 0, 0);
        this.protectionInfo = new ProtectionInfo();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // event is already cancelled => return
        if (event.isCancelled()) {
            return;
        }

        // update the BlockVector & the ProtectionInfo
        this.vector.update(event.getBlock().getLocation());
        this.protectionInfo.update(this.vector);

        // Block is not protected => return
        if (!this.protectionInfo.hasAnyProtection()) {
            return;
        }

        // Block is protected => check: Protection OR SubProtection
        if (this.protectionInfo.hasProtection()) {
            // we have a regular protection => get the module (must be
            // registered)
            Module module = this.moduleManager.getModule(event.getBlock().getTypeId());
            if (module == null) {
                PlayerUtils.sendError(event.getPlayer(), Core.NAME, "Module for block '" + event.getBlock().getType().name() + "' is not registered!");
                return;
            }

            // get the protection
            Protection protection = this.protectionManager.getProtection(this.vector);

            // check permission
            boolean isOwner = protection.isOwner(event.getPlayer().getName());
            boolean isAdmin = UtilPermissions.playerCanUseCommand(event.getPlayer(), "moneypit.admin");
            if (!isOwner && !isAdmin) {
                PlayerUtils.sendError(event.getPlayer(), Core.NAME, "You are not allowed to break this protected block.");
                event.setCancelled(true);
                return;
            }

            // remove protection
            this.protectionManager.removeProtection(this.vector);

            // send info
            PlayerUtils.sendSuccess(event.getPlayer(), Core.NAME, "Protection removed.");
        } else {
            // we have a SubProtection => send error & cancel the event
            PlayerUtils.sendError(event.getPlayer(), Core.NAME, "This block has a subprotection and cannot be broken.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // event is already cancelled => return
        if (event.isCancelled()) {
            return;
        }

        // Only handle Left- & Right-Click on a block
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // update the BlockVector & the ProtectionInfo
        this.vector.update(event.getClickedBlock().getLocation());
        this.protectionInfo.update(this.vector);

        // get PlayerState
        PlayerState state = this.playerManager.getState(event.getPlayer().getName());
        boolean inAddMode = (state == PlayerState.PROTECTION_ADD);
        boolean inRemoveMode = (state == PlayerState.PROTECTION_REMOVE);

        // no sneak => print info about protections & return
        if (state == PlayerState.NORMAL) {
            // CHECK: Protection?
            if (this.protectionInfo.hasProtection()) {
                // check permission
                boolean isOwner = this.protectionInfo.getProtection().isOwner(event.getPlayer().getName());
                boolean isAdmin = UtilPermissions.playerCanUseCommand(event.getPlayer(), "moneypit.admin");

                // is this protection private?
                if (this.protectionInfo.getProtection().isPrivate()) {
                    if (!isOwner && !isAdmin) {
                        PlayerUtils.sendError(event.getPlayer(), Core.NAME, "This block is protected by " + this.protectionInfo.getProtection().getOwner() + " ( " + this.protectionInfo.getProtection().getType() + " ).");
                        event.setCancelled(true);
                        return;
                    }
                }

                PlayerUtils.sendInfo(event.getPlayer(), Core.NAME, "This block is protected by " + this.protectionInfo.getProtection().getOwner() + " ( " + this.protectionInfo.getProtection().getType() + " ).");
                return;
            }

            // CHECK: SubProtection?
            if (this.protectionInfo.hasSubProtection()) {
                // cancel event
                event.setCancelled(true);

                // send info
                PlayerUtils.sendInfo(event.getPlayer(), Core.NAME, "Block is a subprotection!");
                for (int i = 0; i < this.protectionInfo.getSubProtections().getSize(); i++) {
                    PlayerUtils.sendInfo(event.getPlayer(), Core.NAME, "#" + (i + 1) + " : " + this.protectionInfo.getSubProtections().getProtections().get(i));
                }
                return;
            }

            PlayerUtils.sendInfo(event.getPlayer(), Core.NAME, "This block is not protected!");
            return;
        }

        // the module must be registered
        Module module = this.moduleManager.getModule(event.getClickedBlock().getTypeId());
        if (module == null) {
            PlayerUtils.sendError(event.getPlayer(), Core.NAME, "Module for block '" + event.getClickedBlock().getType().name() + "' is not registered!");
            return;
        }

        if (inAddMode) {
            // cancel event
            event.setCancelled(true);

            // check permissions
            if (!UtilPermissions.playerCanUseCommand(event.getPlayer(), "moneypit.protect." + module.getModuleName()) && !UtilPermissions.playerCanUseCommand(event.getPlayer(), "moneypit.admin")) {
                PlayerUtils.sendError(event.getPlayer(), Core.NAME, "You are not allowed to protect this block!");
                return;
            }

            // add protection, if it isn't protected yet
            if (!this.protectionInfo.hasAnyProtection()) {
                Random random = new Random();
                module.addProtection(random.nextInt(1000000), this.vector, event.getPlayer().getName(), ProtectionType.PRIVATE, event.getClickedBlock().getData());
                PlayerUtils.sendSuccess(event.getPlayer(), Core.NAME, "Protection set.");
            } else {
                PlayerUtils.sendError(event.getPlayer(), Core.NAME, "This block is already protected!");
            }
            return;
        } else if (inRemoveMode) {
            // cancel event
            event.setCancelled(true);

            // try to remove the protection
            if (!this.protectionInfo.hasProtection()) {
                PlayerUtils.sendError(event.getPlayer(), Core.NAME, "This block is not protected!");
            } else {
                // get protection
                Protection protection = this.protectionInfo.getProtection();

                // check permission
                boolean isOwner = protection.isOwner(event.getPlayer().getName());
                boolean isAdmin = UtilPermissions.playerCanUseCommand(event.getPlayer(), "moneypit.admin");
                if (!isOwner && !isAdmin) {
                    PlayerUtils.sendError(event.getPlayer(), Core.NAME, "You are not allowed to remove this protection!");
                    return;
                }

                // print info
                this.protectionManager.removeProtection(this.vector);
                PlayerUtils.sendSuccess(event.getPlayer(), Core.NAME, "Protection removed.");
            }
        }
    }
}
