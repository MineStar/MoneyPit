package de.minestar.moneypit.listener;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.bukkit.gemo.utils.UtilPermissions;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.Core;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.PlayerState;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.protection.ProtectionInfo;
import de.minestar.moneypit.data.protection.ProtectionType;
import de.minestar.moneypit.data.subprotection.SubProtection;
import de.minestar.moneypit.data.subprotection.SubProtectionHolder;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.manager.PlayerManager;
import de.minestar.moneypit.manager.ProtectionManager;
import de.minestar.moneypit.modules.Module;
import de.minestar.moneypit.threads.AddProtectionThread;

public class ActionListener implements Listener {

    private ModuleManager moduleManager;
    private PlayerManager playerManager;
    private ProtectionManager protectionManager;

    private BlockVector vector;
    private ProtectionInfo protectionInfo;

    public ActionListener() {
        this.moduleManager = Core.moduleManager;
        this.playerManager = Core.playerManager;
        this.protectionManager = Core.protectionManager;
        this.vector = new BlockVector("", 0, 0, 0);
        this.protectionInfo = new ProtectionInfo();
    }

    public Block[] getNeighbours(Block block) {
        Block[] blocks = new Block[6];
        blocks[0] = block.getRelative(BlockFace.UP);
        blocks[1] = blocks[0].getRelative(BlockFace.UP);
        blocks[2] = block.getRelative(BlockFace.NORTH);
        blocks[3] = block.getRelative(BlockFace.WEST);
        blocks[4] = block.getRelative(BlockFace.EAST);
        blocks[5] = block.getRelative(BlockFace.SOUTH);
        return blocks;
    }

    @EventHandler
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        // event is already cancelled => return
        if (event.getNewCurrent() == event.getOldCurrent()) {
            return;
        }

        Block[] blocks = this.getNeighbours(event.getBlock());
        Module module;
        for (Block block : blocks) {
            // update the BlockVector & the ProtectionInfo
            this.vector.update(block.getLocation());
            this.protectionInfo.update(this.vector);
            if (this.protectionInfo.hasAnyProtection()) {
                Protection protection = this.protectionInfo.getProtection();
                if (protection != null) {
                    // get the module
                    module = this.moduleManager.getRegisteredModule(block.getTypeId());
                    if (module == null) {
                        continue;
                    }
                    // check for redstone only, if the module wants it
                    if (!module.handleRedstone()) {
                        continue;
                    }
                }
                event.setNewCurrent(event.getOldCurrent());
                return;
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // event is already cancelled => return
        if (event.isCancelled()) {
            return;
        }

        // get the module
        Module module = this.moduleManager.getRegisteredModule(event.getBlockPlaced().getTypeId());
        if (module == null) {
            return;
        }

        // check for neighbours, if the module wants it
        if (module.doNeighbourCheck()) {
            if (module.onPlace(event, new BlockVector(event.getBlockPlaced().getLocation()))) {
                return;
            }
        }

        // only act, if the module is in autolockmode
        if (!module.isAutoLock()) {
            return;
        }

        // update the BlockVector & the ProtectionInfo
        this.vector.update(event.getBlock().getLocation());
        this.protectionInfo.update(this.vector);

        // add protection, if it isn't protected yet
        if (!this.protectionInfo.hasAnyProtection()) {
            // create the vector
            BlockVector tempVector = new BlockVector(event.getBlockPlaced().getLocation());
            // create thread to add the protection and start it
            AddProtectionThread thread = new AddProtectionThread(event.getPlayer(), module, tempVector, event.getBlockPlaced().getTypeId(), this.protectionInfo.clone());
            Bukkit.getScheduler().scheduleSyncDelayedTask(Core.INSTANCE, thread, 1L);
        } else {
            PlayerUtils.sendError(event.getPlayer(), Core.NAME, "Cannot create protection!");
            PlayerUtils.sendInfo(event.getPlayer(), "This block is already protected.");
        }
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
            Module module = this.moduleManager.getRegisteredModule(event.getBlock().getTypeId());
            if (module == null) {
                PlayerUtils.sendError(event.getPlayer(), Core.NAME, "Module for block '" + event.getBlock().getType().name() + "' is not registered!");
                return;
            }

            // get the protection
            Protection protection = this.protectionInfo.getProtection();

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
            PlayerUtils.sendError(event.getPlayer(), Core.NAME, "This block is a subprotection and cannot be broken.");
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
        boolean inAddMode = (state == PlayerState.PROTECTION_ADD_PRIVATE || state == PlayerState.PROTECTION_ADD_PUBLIC);
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
                SubProtectionHolder holder = this.protectionManager.getSubProtectionHolder(vector);
                boolean isOwner;
                boolean isAdmin = UtilPermissions.playerCanUseCommand(event.getPlayer(), "moneypit.admin");
                for (SubProtection subProtection : holder.getProtections()) {
                    // is this protection private?
                    if (!subProtection.getParent().isPrivate()) {
                        continue;
                    }

                    // check permission
                    isOwner = subProtection.isOwner(event.getPlayer().getName());

                    // check the access
                    if (isOwner || isAdmin) {
                        continue;
                    }

                    // cancel event
                    event.setCancelled(true);
                    PlayerUtils.sendInfo(event.getPlayer(), Core.NAME, "This block is protected.");
                    return;
                }
                PlayerUtils.sendInfo(event.getPlayer(), Core.NAME, "This block is protected.");
                return;
            }
            return;
        }

        // the module must be registered
        Module module = this.moduleManager.getRegisteredModule(event.getClickedBlock().getTypeId());
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
                // create the vector
                BlockVector tempVector = new BlockVector(event.getClickedBlock().getLocation());

                if (state == PlayerState.PROTECTION_ADD_PRIVATE) {
                    // protect private
                    Random random = new Random();
                    module.addProtection(random.nextInt(1000000), tempVector, event.getPlayer().getName(), ProtectionType.PRIVATE, event.getClickedBlock().getData());
                    PlayerUtils.sendSuccess(event.getPlayer(), Core.NAME, "Private protection created.");
                } else {
                    // protect public
                    Random random = new Random();
                    module.addProtection(random.nextInt(1000000), tempVector, event.getPlayer().getName(), ProtectionType.PUBLIC, event.getClickedBlock().getData());
                    PlayerUtils.sendSuccess(event.getPlayer(), Core.NAME, "Public protection created.");
                }
            } else {
                PlayerUtils.sendError(event.getPlayer(), Core.NAME, "Cannot create protection!");
                PlayerUtils.sendInfo(event.getPlayer(), Core.NAME, "This block is already protected.");
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
