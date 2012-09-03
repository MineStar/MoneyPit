package de.minestar.moneypit.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.server.Packet53BlockChange;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.bukkit.gemo.utils.UtilPermissions;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.EventResult;
import de.minestar.moneypit.data.PlayerState;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.protection.ProtectionInfo;
import de.minestar.moneypit.data.protection.ProtectionType;
import de.minestar.moneypit.data.subprotection.SubProtection;
import de.minestar.moneypit.data.subprotection.SubProtectionHolder;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.manager.PlayerManager;
import de.minestar.moneypit.manager.ProtectionManager;
import de.minestar.moneypit.manager.QueueManager;
import de.minestar.moneypit.modules.Module;
import de.minestar.moneypit.queues.AddProtectionQueue;
import de.minestar.moneypit.queues.RemoveProtectionQueue;
import de.minestar.moneypit.queues.RemoveSubProtectionQueue;
import de.minestar.moneypit.utils.DoorHelper;
import de.minestar.moneypit.utils.ListHelper;

public class ActionListener implements Listener {

    private static final Set<Integer> nonPushableBlocks = new HashSet<Integer>(Arrays.asList(0, 6, 7, 8, 9, 10, 11, 23, 26, 30, 31, 32, 34, 37, 38, 39, 40, 50, 51, 52, 55, 59, 61, 62, 63, 64, 65, 68, 69, 70, 71, 72, 75, 76, 77, 81, 83, 84, 86, 90, 91, 92, 93, 94, 96, 103, 104, 105, 106, 111, 115, 116, 117, 119, 120, 122, 127, 130, 131, 132));

    private ModuleManager moduleManager;
    private PlayerManager playerManager;
    private ProtectionManager protectionManager;
    private QueueManager queueManager;

    private BlockVector vector;
    private ProtectionInfo protectionInfo;

    private HashSet<BlockVector> redstoneQueuedDoors = new HashSet<BlockVector>();

    private Block[] redstoneCheckBlocks = new Block[6];

    public ActionListener() {
        this.moduleManager = MoneyPitCore.moduleManager;
        this.playerManager = MoneyPitCore.playerManager;
        this.protectionManager = MoneyPitCore.protectionManager;
        this.queueManager = MoneyPitCore.queueManager;
        this.vector = new BlockVector("", 0, 0, 0);
        this.protectionInfo = new ProtectionInfo();
    }

    private void refreshRedstoneCheckBlocks(Block block) {
        redstoneCheckBlocks[0] = block.getRelative(BlockFace.UP);
        redstoneCheckBlocks[1] = redstoneCheckBlocks[0].getRelative(BlockFace.UP);
        redstoneCheckBlocks[2] = block.getRelative(BlockFace.NORTH);
        redstoneCheckBlocks[3] = block.getRelative(BlockFace.WEST);
        redstoneCheckBlocks[4] = block.getRelative(BlockFace.EAST);
        redstoneCheckBlocks[5] = block.getRelative(BlockFace.SOUTH);
    }

    @EventHandler
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        // event is already cancelled => return
        if (event.getNewCurrent() == event.getOldCurrent()) {
            return;
        }

        this.refreshRedstoneCheckBlocks(event.getBlock());
        Module module;

        for (Block block : this.redstoneCheckBlocks) {
            // update the BlockVector & the ProtectionInfo
            this.vector.update(block.getLocation());
            this.protectionInfo.update(this.vector);
            if (this.protectionInfo.hasAnyProtection()) {
                Protection protection = this.protectionInfo.getProtection();
                if (protection != null) {
                    // normal protection

                    // only private protections are blocked
                    if (protection.isPublic()) {
                        if (block.getTypeId() == Material.WOODEN_DOOR.getId()) {
                            this.redstoneQueuedDoors.add(new BlockVector(DoorHelper.getLowerDoorPart(block).getLocation()));
                            Block bl = DoorHelper.getOppositeLowerDoorPart(block);
                            if (bl != null && bl.getTypeId() == Material.WOODEN_DOOR.getId()) {
                                this.redstoneQueuedDoors.add(new BlockVector(bl.getLocation()));
                            }
                        } else if (block.getTypeId() == Material.IRON_DOOR_BLOCK.getId()) {
                            this.redstoneQueuedDoors.add(new BlockVector(DoorHelper.getLowerDoorPart(block).getLocation()));
                            Block bl = DoorHelper.getOppositeLowerDoorPart(block);
                            if (bl != null && bl.getTypeId() == Material.IRON_DOOR_BLOCK.getId()) {
                                this.redstoneQueuedDoors.add(new BlockVector(bl.getLocation()));
                            }
                        }
                        continue;
                    }

                    // get the module
                    module = this.moduleManager.getRegisteredModule(block.getTypeId());
                    if (module == null) {
                        continue;
                    }

                    // check for redstone only, if the module wants it
                    if (!module.blockRedstone()) {
                        continue;
                    }
                    event.setNewCurrent(event.getOldCurrent());
                    return;
                } else {
                    // SubProtection here
                    // check all subprotections at this place and see if we
                    // handle the redstone-event
                    int moduleID = 0;
                    SubProtectionHolder holder = this.protectionInfo.getSubProtections();
                    for (SubProtection subProtection : holder.getProtections()) {
                        // only private protections are blocked
                        if (subProtection.getParent().isPublic()) {
                            if (block.getTypeId() == Material.WOODEN_DOOR.getId()) {
                                this.redstoneQueuedDoors.add(new BlockVector(DoorHelper.getLowerDoorPart(block).getLocation()));
                                Block bl = DoorHelper.getOppositeLowerDoorPart(block);
                                if (bl != null && bl.getTypeId() == Material.WOODEN_DOOR.getId()) {
                                    this.redstoneQueuedDoors.add(new BlockVector(bl.getLocation()));
                                }
                            } else if (block.getTypeId() == Material.IRON_DOOR_BLOCK.getId()) {
                                this.redstoneQueuedDoors.add(new BlockVector(DoorHelper.getLowerDoorPart(block).getLocation()));
                                Block bl = DoorHelper.getOppositeLowerDoorPart(block);
                                if (bl != null && bl.getTypeId() == Material.IRON_DOOR_BLOCK.getId()) {
                                    this.redstoneQueuedDoors.add(new BlockVector(bl.getLocation()));
                                }
                            }
                            continue;
                        }

                        // get the module
                        moduleID = subProtection.getModuleID();
                        module = this.moduleManager.getRegisteredModule(moduleID);
                        if (module == null) {
                            continue;
                        }

                        // check for redstone only, if the module wants it
                        if (!module.blockRedstone()) {
                            continue;
                        }
                        event.setNewCurrent(event.getOldCurrent());
                        return;
                    }
                }
            } else {
                if (block.getTypeId() == Material.WOODEN_DOOR.getId()) {
                    this.redstoneQueuedDoors.add(new BlockVector(DoorHelper.getLowerDoorPart(block).getLocation()));
                    Block bl = DoorHelper.getOppositeLowerDoorPart(block);
                    if (bl != null && bl.getTypeId() == Material.WOODEN_DOOR.getId()) {
                        this.redstoneQueuedDoors.add(new BlockVector(bl.getLocation()));
                    }
                } else if (block.getTypeId() == Material.IRON_DOOR_BLOCK.getId()) {
                    this.redstoneQueuedDoors.add(new BlockVector(DoorHelper.getLowerDoorPart(block).getLocation()));
                    Block bl = DoorHelper.getOppositeLowerDoorPart(block);
                    if (bl != null && bl.getTypeId() == Material.IRON_DOOR_BLOCK.getId()) {
                        this.redstoneQueuedDoors.add(new BlockVector(bl.getLocation()));
                    }
                }
            }
        }

        if (!this.redstoneQueuedDoors.isEmpty()) {
            boolean openDoor = event.getNewCurrent() > 0;
            for (BlockVector vector : this.redstoneQueuedDoors) {
                if (openDoor) {
                    DoorHelper.openDoor(vector.getLocation().getBlock(), true);
                } else {
                    DoorHelper.closeDoor(vector.getLocation().getBlock());
                }
            }
            this.redstoneQueuedDoors.clear();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        // event is already cancelled => return
        if (event.isCancelled() || !event.canBuild()) {
            return;
        }

        // get the module
        Module module = this.moduleManager.getRegisteredModule(event.getBlockPlaced().getTypeId());
        if (module == null) {
            return;
        }

        // check for neighbours, if the module wants it
        if (module.doNeighbourCheck()) {
            EventResult result = module.onPlace(event.getPlayer(), new BlockVector(event.getBlockPlaced().getLocation()));
            if (result.isCancelEvent()) {
                event.setBuild(false);
                event.setCancelled(true);

                BlockVector vector = result.getProtection().getVector();
                CraftWorld cWorld = (CraftWorld) vector.getLocation().getWorld();
                CraftPlayer cPlayer = (CraftPlayer) event.getPlayer();
                Packet53BlockChange packet = new Packet53BlockChange(vector.getX(), vector.getY(), vector.getZ(), cWorld.getHandle());
                cPlayer.getHandle().netServerHandler.sendPacket(packet);
            }
            if (result.isAbort()) {
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
            // check the permission
            boolean canProtect = UtilPermissions.playerCanUseCommand(event.getPlayer(), "moneypit.protect." + module.getModuleName()) || UtilPermissions.playerCanUseCommand(event.getPlayer(), "moneypit.admin");
            if (canProtect) {
                // create the vector
                BlockVector tempVector = new BlockVector(event.getBlockPlaced().getLocation());

                // queue the event for later use in MonitorListener
                AddProtectionQueue queue = new AddProtectionQueue(event.getPlayer(), module, tempVector, ProtectionType.PRIVATE);
                this.queueManager.addQueue(queue);
            } else {
                PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "You don't have permissions to protect this block.");
                return;
            }
        } else {
            if (this.protectionInfo.hasSubProtection()) {
                if (!this.protectionInfo.getSubProtections().canEditAll(event.getPlayer())) {
                    PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "You are not allowed to edit this protected block!");
                    event.setCancelled(true);
                    return;
                }
            }
            PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "Cannot create protection!");
            PlayerUtils.sendInfo(event.getPlayer(), "This block is already protected.");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
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
                PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "Module for block '" + event.getBlock().getType().name() + "' is not registered!");
                return;
            }

            // get the protection
            Protection protection = this.protectionInfo.getProtection();

            // check permission
            boolean isOwner = protection.isOwner(event.getPlayer().getName());
            boolean isAdmin = UtilPermissions.playerCanUseCommand(event.getPlayer(), "moneypit.admin");
            if (!isOwner && !isAdmin) {
                PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "You are not allowed to break this protected block.");
                event.setCancelled(true);
                return;
            }

            // create the vector
            BlockVector tempVector = new BlockVector(event.getBlock().getLocation());

            // queue the event for later use in MonitorListener
            RemoveProtectionQueue queue = new RemoveProtectionQueue(event.getPlayer(), tempVector);
            this.queueManager.addQueue(queue);
        } else {
            // we have a SubProtection => check permissions and handle it
            if (!this.protectionInfo.getSubProtections().canEditAll(event.getPlayer())) {
                PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "You are not allowed to remove this subprotection!");
                event.setCancelled(true);
                return;
            }

            // create the vector
            BlockVector tempVector = new BlockVector(event.getBlock().getLocation());

            // queue the event for later use in MonitorListener
            RemoveSubProtectionQueue queue = new RemoveSubProtectionQueue(event.getPlayer(), tempVector, this.protectionInfo.clone());
            this.queueManager.addQueue(queue);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (event.getChangedTypeId() == Material.TNT.getId()) {
            // update the BlockVector & the ProtectionInfo
            this.vector.update(event.getBlock().getLocation());
            this.protectionInfo.update(this.vector);

            if (this.protectionInfo.hasAnyProtection()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Only handle Left- & Right-Click on a block
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK && action != Action.PHYSICAL) {
            return;
        }

        // update the BlockVector & the ProtectionInfo
        this.vector.update(event.getClickedBlock().getLocation());
        this.protectionInfo.update(this.vector);

        // get PlayerState
        final PlayerState state = this.playerManager.getState(event.getPlayer().getName());

        // decide what to do
        switch (state) {
            case PROTECTION_INFO : {
                // handle info
                this.handleInfoInteract(event);
                break;
            }
            case PROTECTION_REMOVE : {
                // handle remove
                this.handleRemoveInteract(event);
                break;
            }
            case PROTECTION_ADD_PRIVATE :
            case PROTECTION_ADD_GIFT : {
                // the module must be registered
                Module module = this.moduleManager.getRegisteredModule(event.getClickedBlock().getTypeId());
                if (module == null) {
                    PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "Module for block '" + event.getClickedBlock().getType().name() + "' is not registered!");
                    return;
                }

                // handle add
                this.handleAddInteract(event, module, state, module.isGiftable());
                break;
            }
            case PROTECTION_ADD_PUBLIC : {
                // the module must be registered
                Module module = this.moduleManager.getRegisteredModule(event.getClickedBlock().getTypeId());
                if (module == null) {
                    PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "Module for block '" + event.getClickedBlock().getType().name() + "' is not registered!");
                    return;
                }

                // handle add
                this.handleAddInteract(event, module, state, module.isGiftable());
                break;
            }
            case PROTECTION_INVITE : {
                this.handleInviteInteract(event, true);
                break;
            }
            case PROTECTION_UNINVITE : {
                this.handleInviteInteract(event, false);
                break;
            }
            case PROTECTION_UNINVITEALL : {
                this.handleUninviteAllInteract(event);
                break;
            }
            default : {
                // handle normal interact
                this.handleNormalInteract(event);
                break;
            }
        }
    }

    // //////////////////////////////////////////////////////////////////////
    //
    // FROM HERE ON: METHODS TO HANDLE THE PLAYERINTERACT
    //
    // //////////////////////////////////////////////////////////////////////

    private void handleUninviteAllInteract(PlayerInteractEvent event) {
        // cancel the event
        event.setCancelled(true);

        // return to normalmode
        this.playerManager.setState(event.getPlayer().getName(), PlayerState.NORMAL);

        if (this.protectionInfo.hasProtection()) {
            // MainProtection

            if (this.protectionInfo.getProtection().isPublic()) {
                PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "You must click on a private protection.");
                this.showInformation(event.getPlayer());
                return;
            }

            boolean canEdit = this.protectionInfo.getProtection().canEdit(event.getPlayer());
            if (canEdit) {
                // clear guestlist
                this.protectionInfo.getProtection().clearGuestList();

                if (MoneyPitCore.databaseManager.updateGuestList(this.protectionInfo.getProtection(), ListHelper.toString(this.protectionInfo.getProtection().getGuestList()))) {
                    // send info
                    PlayerUtils.sendSuccess(event.getPlayer(), MoneyPitCore.NAME, "The guestlist has been cleared.");
                } else {
                    PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "Error while saving guestlist to database.");
                    PlayerUtils.sendInfo(event.getPlayer(), "Please contact an admin.");
                }
            } else {
                PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "You are not allowed to edit this protection.");
                this.showInformation(event.getPlayer());
            }
        } else if (this.protectionInfo.hasSubProtection()) {
            boolean canEdit = this.protectionInfo.getSubProtections().canEditAll(event.getPlayer());
            if (canEdit) {
                // for each SubProtection...
                boolean result = true;
                for (SubProtection subProtection : this.protectionInfo.getSubProtections().getProtections()) {
                    if (subProtection.getParent().isPrivate()) {
                        // clear guestlist
                        subProtection.getParent().clearGuestList();
                    }
                    if (!MoneyPitCore.databaseManager.updateGuestList(subProtection.getParent(), ListHelper.toString(subProtection.getParent().getGuestList()))) {
                        result = false;
                    }
                }

                // send info
                if (result) {
                    PlayerUtils.sendSuccess(event.getPlayer(), MoneyPitCore.NAME, "The guestlist has been cleared.");
                } else {
                    PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "Error while saving guestlist to database.");
                    PlayerUtils.sendInfo(event.getPlayer(), "Please contact an admin.");
                }
            } else {
                PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "You are not allowed to edit this protection.");
                this.showInformation(event.getPlayer());
            }
        } else {
            PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "This block is not protected.");
        }
    }

    private void handleInviteInteract(PlayerInteractEvent event, boolean add) {
        // cancel the event
        event.setCancelled(true);

        // return to normalmode
        this.playerManager.setState(event.getPlayer().getName(), PlayerState.NORMAL);

        if (this.protectionInfo.hasProtection()) {
            // MainProtection

            if (this.protectionInfo.getProtection().isPublic()) {
                PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "You must click on a private protection.");
                this.showInformation(event.getPlayer());
                return;
            }

            boolean canEdit = this.protectionInfo.getProtection().canEdit(event.getPlayer());
            if (canEdit) {
                // add people to guestlist
                for (String guest : this.playerManager.getGuestList(event.getPlayer().getName())) {
                    if (add) {
                        if (!this.protectionInfo.getProtection().isOwner(guest)) {
                            this.protectionInfo.getProtection().addGuest(guest);
                        }
                    } else {
                        this.protectionInfo.getProtection().removeGuest(guest);
                    }
                }
                // send info

                if (MoneyPitCore.databaseManager.updateGuestList(this.protectionInfo.getProtection(), ListHelper.toString(this.protectionInfo.getProtection().getGuestList()))) {
                    if (add)
                        PlayerUtils.sendSuccess(event.getPlayer(), MoneyPitCore.NAME, "Players have been added to the guestlist.");
                    else
                        PlayerUtils.sendSuccess(event.getPlayer(), MoneyPitCore.NAME, "Players have been removed from the guestlist.");
                } else {
                    PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "Error while saving guestlist to database.");
                    PlayerUtils.sendInfo(event.getPlayer(), "Please contact an admin.");
                }
            } else {
                PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "You are not allowed to edit this protection.");
                this.showInformation(event.getPlayer());
            }
        } else if (this.protectionInfo.hasSubProtection()) {
            boolean canEdit = this.protectionInfo.getSubProtections().canEditAll(event.getPlayer());
            if (canEdit) {
                // for each SubProtection...
                boolean result = true;
                for (SubProtection subProtection : this.protectionInfo.getSubProtections().getProtections()) {
                    if (subProtection.getParent().isPrivate()) {
                        // add people to guestlist
                        for (String guest : this.playerManager.getGuestList(event.getPlayer().getName())) {
                            if (add) {
                                if (!subProtection.isOwner(guest)) {
                                    subProtection.addGuest(guest);
                                }
                            } else {
                                subProtection.removeGuest(guest);
                            }
                        }
                    }

                    if (!MoneyPitCore.databaseManager.updateGuestList(subProtection.getParent(), ListHelper.toString(subProtection.getParent().getGuestList()))) {
                        result = false;
                    }
                }
                // send info
                if (result) {
                    if (add)
                        PlayerUtils.sendSuccess(event.getPlayer(), MoneyPitCore.NAME, "Players have been added to the guestlist.");
                    else
                        PlayerUtils.sendSuccess(event.getPlayer(), MoneyPitCore.NAME, "Players have been removed from the guestlist.");
                } else {
                    PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "Error while saving guestlist to database.");
                    PlayerUtils.sendInfo(event.getPlayer(), "Please contact an admin.");
                }
            } else {
                PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "You are not allowed to edit this protection.");
                this.showInformation(event.getPlayer());
            }
        } else {
            PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "This block is not protected.");
        }

        // clear guestlist
        this.playerManager.clearGuestList(event.getPlayer().getName());
    }

    private void showInformation(Player player) {
        this.showInformation(player, false);
    }

    private void showInformation(Player player, boolean showErrorMessage) {
        // we need a protection to show some information about it
        if (!this.protectionInfo.hasAnyProtection()) {
            if (showErrorMessage) {
                PlayerUtils.sendError(player, MoneyPitCore.NAME, "This block is not protected.");
            }
            return;
        }

        if (this.protectionInfo.hasProtection()) {
            // handle mainprotections
            String pType = " " + this.protectionInfo.getProtection().getType().toString() + " ";
            int moduleID = this.protectionInfo.getProtection().getModuleID();
            String message = "This" + ChatColor.RED + pType + Material.getMaterial(moduleID) + ChatColor.GRAY + " is protected by " + ChatColor.YELLOW + this.protectionInfo.getProtection().getOwner() + ".";
            PlayerUtils.sendInfo(player, message);
            return;
        } else {
            // handle subprotections
            if (this.protectionInfo.getSubProtections().getSize() == 1) {
                String pType = " " + this.protectionInfo.getFirstProtection().getType().toString() + " ";
                int moduleID = this.protectionInfo.getFirstProtection().getVector().getLocation().getBlock().getTypeId();
                String message = "This" + ChatColor.RED + pType + Material.getMaterial(moduleID) + ChatColor.GRAY + " is protected by " + ChatColor.YELLOW + this.protectionInfo.getFirstProtection().getOwner() + ".";
                PlayerUtils.sendInfo(player, message);
                return;
            } else if (this.protectionInfo.getSubProtections().getSize() > 1) {
                int moduleID = this.protectionInfo.getFirstProtection().getVector().getLocation().getBlock().getTypeId();
                String message = "This " + ChatColor.RED + Material.getMaterial(moduleID) + ChatColor.GRAY + " is protected with " + ChatColor.YELLOW + "multiple protections.";
                PlayerUtils.sendInfo(player, message);
                return;
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

    private void showExtendedInformation(Player player) {
        // we need a protection to show some information about it
        if (!this.protectionInfo.hasAnyProtection()) {
            PlayerUtils.sendError(player, MoneyPitCore.NAME, "This block is not protected.");
            return;
        }

        if (this.protectionInfo.hasProtection()) {
            // handle mainprotections
            String pType = " " + this.protectionInfo.getProtection().getType().toString() + " ";
            int moduleID = this.protectionInfo.getProtection().getModuleID();
            String message = "This" + ChatColor.RED + pType + Material.getMaterial(moduleID) + ChatColor.GRAY + " is protected by " + ChatColor.YELLOW + this.protectionInfo.getProtection().getOwner() + ".";

            if (this.protectionInfo.getProtection().canAccess(player)) {
                HashSet<String> guestList = this.protectionInfo.getProtection().getGuestList();
                if (guestList != null) {
                    this.displayGuestList(player, guestList);
                }
            }

            PlayerUtils.sendInfo(player, message);
            return;
        } else {
            // handle subprotections
            if (this.protectionInfo.getSubProtections().getSize() == 1) {
                String pType = " " + this.protectionInfo.getFirstProtection().getType().toString() + " ";
                int moduleID = this.protectionInfo.getFirstProtection().getVector().getLocation().getBlock().getTypeId();
                String message = "This" + ChatColor.RED + pType + Material.getMaterial(moduleID) + ChatColor.GRAY + " is protected by " + ChatColor.YELLOW + this.protectionInfo.getFirstProtection().getOwner() + ".";
                PlayerUtils.sendInfo(player, message);

                if (this.protectionInfo.getFirstProtection().canAccess(player)) {
                    HashSet<String> guestList = this.protectionInfo.getFirstProtection().getGuestList();
                    if (guestList != null) {
                        this.displayGuestList(player, guestList);
                    }
                }

                return;
            } else if (this.protectionInfo.getSubProtections().getSize() > 1) {
                int moduleID = this.protectionInfo.getFirstProtection().getVector().getLocation().getBlock().getTypeId();
                String message = "This" + ChatColor.RED + Material.getMaterial(moduleID) + ChatColor.GRAY + " is protected by " + ChatColor.YELLOW + "multiple protections" + ".";
                PlayerUtils.sendInfo(player, message);
                return;
            }
        }
    }

    private void handleInfoInteract(PlayerInteractEvent event) {
        // cancel the event
        event.setCancelled(true);

        // return to normalmode
        this.playerManager.setState(event.getPlayer().getName(), PlayerState.NORMAL);

        // show information
        this.showExtendedInformation(event.getPlayer());
    }

    private void handleRemoveInteract(PlayerInteractEvent event) {
        // return to normalmode
        this.playerManager.setState(event.getPlayer().getName(), PlayerState.NORMAL);

        // try to remove the protection
        if (!this.protectionInfo.hasAnyProtection()) {
            PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "This block is not protected!");
            return;
        } else if (this.protectionInfo.hasProtection()) {
            // get protection
            Protection protection = this.protectionInfo.getProtection();

            // check permission
            if (!protection.canEdit(event.getPlayer())) {
                PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "You are not allowed to remove this protection!");
                event.setCancelled(true);
                return;
            }

            // create the vector
            BlockVector tempVector = new BlockVector(event.getClickedBlock().getLocation());

            // queue the event for later use in MonitorListener
            RemoveProtectionQueue queue = new RemoveProtectionQueue(event.getPlayer(), tempVector);
            this.queueManager.addQueue(queue);
        } else {
            // we have a SubProtection => check permissions and handle it
            if (!this.protectionInfo.getSubProtections().canEditAll(event.getPlayer())) {
                PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "You are not allowed to remove this subprotection!");
                event.setCancelled(true);
                return;
            }

            // create the vector
            BlockVector tempVector = new BlockVector(event.getClickedBlock().getLocation());

            // queue the event for later use in MonitorListener
            RemoveSubProtectionQueue queue = new RemoveSubProtectionQueue(event.getPlayer(), tempVector, this.protectionInfo.clone());
            this.queueManager.addQueue(queue);
        }
    }

    private void handleAddInteract(PlayerInteractEvent event, Module module, PlayerState state, boolean giftable) {
        // return to normalmode
        this.playerManager.setState(event.getPlayer().getName(), PlayerState.NORMAL);

        // check permissions
        if (!UtilPermissions.playerCanUseCommand(event.getPlayer(), "moneypit.protect." + module.getModuleName()) && !UtilPermissions.playerCanUseCommand(event.getPlayer(), "moneypit.admin")) {
            PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "You are not allowed to protect this block!");
            return;
        }

        // add protection, if it isn't protected yet
        if (!this.protectionInfo.hasAnyProtection()) {
            // create the vector
            BlockVector tempVector = new BlockVector(event.getClickedBlock().getLocation());

            if (state == PlayerState.PROTECTION_ADD_PRIVATE) {
                // create a private protection
                // queue the event for later use in MonitorListener
                AddProtectionQueue queue = new AddProtectionQueue(event.getPlayer(), module, tempVector, ProtectionType.PRIVATE);
                this.queueManager.addQueue(queue);
            } else if (state == PlayerState.PROTECTION_ADD_PUBLIC) {
                // create a public protection

                // queue the event for later use in MonitorListener
                AddProtectionQueue queue = new AddProtectionQueue(event.getPlayer(), module, tempVector, ProtectionType.PUBLIC);
                this.queueManager.addQueue(queue);
            } else if (state == PlayerState.PROTECTION_ADD_GIFT) {
                // create a gift protection
                if (giftable) {
                    // queue the event for later use in MonitorListener
                    AddProtectionQueue queue = new AddProtectionQueue(event.getPlayer(), module, tempVector, ProtectionType.GIFT);
                    this.queueManager.addQueue(queue);
                } else {
                    PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "This block cannot be a gift protection!");
                    event.setCancelled(true);
                    return;
                }
            }
        } else {
            // Send errormessage
            PlayerUtils.sendError(event.getPlayer(), MoneyPitCore.NAME, "Cannot create protection!");
            event.setCancelled(true);

            // show information about the protection
            this.showInformation(event.getPlayer());
        }
    }

    private void handleNormalInteract(PlayerInteractEvent event) {
        // ---------> WORKAROUND FOR CHESTS BEING ROTATED
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getItemInHand().getTypeId() == Material.CHEST.getId()) {
            Module module = this.moduleManager.getRegisteredModule(Material.CHEST.getId());
            if (module != null) {
                EventResult result = module.onPlace(event.getPlayer(), new BlockVector(event.getClickedBlock().getRelative(event.getBlockFace()).getLocation()));
                if (result.isCancelEvent()) {
                    event.setCancelled(true);
                    event.setUseInteractedBlock(Event.Result.DENY);
                    event.setUseItemInHand(Event.Result.DENY);
                    BlockVector vector = result.getProtection().getVector();
                    CraftWorld cWorld = (CraftWorld) vector.getLocation().getWorld();
                    CraftPlayer cPlayer = (CraftPlayer) event.getPlayer();
                    Packet53BlockChange packet = new Packet53BlockChange(vector.getX(), vector.getY(), vector.getZ(), cWorld.getHandle());
                    cPlayer.getHandle().netServerHandler.sendPacket(packet);
                }
                if (result.isAbort()) {
                    return;
                }
            }
        }
        // ---------> END WORKAROUND FOR CHESTS BEING ROTATED

        // CHECK: Protection?
        if (this.protectionInfo.hasProtection()) {
            boolean isAdmin = UtilPermissions.playerCanUseCommand(event.getPlayer(), "moneypit.admin");
            // is this protection private?
            if (!this.protectionInfo.getProtection().canAccess(event.getPlayer())) {
                // show information about the protection
                this.showInformation(event.getPlayer());
                // cancel the event
                event.setCancelled(true);
                return;
            }

            if (isAdmin) {
                // show information about the protection
                this.showInformation(event.getPlayer());
            }

            // toggle both doors
            if (event.getAction() != Action.PHYSICAL) {
                if (event.getClickedBlock().getTypeId() == Material.WOODEN_DOOR.getId()) {
                    if (this.protectionInfo.getProtection().isPrivate()) {
                        MoneyPitCore.autoCloseTask.queue(event.getClickedBlock());
                        DoorHelper.toggleSecondDoor(event.getClickedBlock(), true);
                    } else {
                        DoorHelper.toggleSecondDoor(event.getClickedBlock(), false);
                    }
                } else if (event.getClickedBlock().getTypeId() == Material.IRON_DOOR_BLOCK.getId()) {
                    if (this.protectionInfo.getProtection().isPrivate()) {
                        DoorHelper.toggleDoor(event.getClickedBlock(), true);
                        DoorHelper.toggleSecondDoor(event.getClickedBlock(), true);
                    } else {
                        DoorHelper.toggleDoor(event.getClickedBlock(), false);
                        DoorHelper.toggleSecondDoor(event.getClickedBlock(), false);
                    }
                }
            }
            return;
        }

        // CHECK: SubProtection?
        if (this.protectionInfo.hasSubProtection()) {
            boolean isAdmin = UtilPermissions.playerCanUseCommand(event.getPlayer(), "moneypit.admin");
            SubProtectionHolder holder = this.protectionManager.getSubProtectionHolder(vector);
            boolean isWoodDoor = event.getClickedBlock().getTypeId() == Material.WOODEN_DOOR.getId();
            boolean isIronDoor = event.getClickedBlock().getTypeId() == Material.IRON_DOOR_BLOCK.getId();
            for (SubProtection subProtection : holder.getProtections()) {
                // is this protection private?
                if (!subProtection.getParent().isPrivate()) {
                    continue;
                }

                // check the access
                if (!subProtection.canAccess(event.getPlayer())) {
                    // cancel event
                    event.setCancelled(true);
                    // show information about the protection
                    this.showInformation(event.getPlayer());
                    return;
                }

            }

            // show information about the protection
            if (isAdmin) {
                this.showInformation(event.getPlayer());
            }

            // toggle both doors
            if (event.getAction() != Action.PHYSICAL) {
                SubProtection subProtection = this.protectionInfo.getSubProtections().getProtection(0);
                if (isWoodDoor) {
                    if (subProtection.getParent().isPrivate()) {
                        MoneyPitCore.autoCloseTask.queue(event.getClickedBlock());
                        DoorHelper.toggleSecondDoor(event.getClickedBlock(), true);
                    } else {
                        DoorHelper.toggleSecondDoor(event.getClickedBlock(), false);
                    }
                } else if (isIronDoor) {
                    if (subProtection.getParent().isPrivate()) {
                        DoorHelper.toggleDoor(event.getClickedBlock(), true);
                        DoorHelper.toggleSecondDoor(event.getClickedBlock(), true);
                    } else {
                        DoorHelper.toggleDoor(event.getClickedBlock(), false);
                        DoorHelper.toggleSecondDoor(event.getClickedBlock(), false);
                    }
                }
            }

            return;
        }

        // toggle both doors
        if (event.getAction() != Action.PHYSICAL) {
            if (event.getClickedBlock().getTypeId() == Material.WOODEN_DOOR.getId()) {
                DoorHelper.toggleSecondDoor(event.getClickedBlock(), false);
            }
        }
    }

    // //////////////////////////////////////////////////////////////////////
    //
    // FROM HERE ON: EVENTS THAT ARE NOT DIRECTLY TRIGGERED BY A PLAYER
    //
    // //////////////////////////////////////////////////////////////////////

    private ArrayList<Block> getPistonChangeBlocks(Block pistonBlock, BlockFace direction) {
        ArrayList<Block> list = new ArrayList<Block>();
        Block temp = pistonBlock;
        for (int count = 0; count < 13; count++) {
            temp = temp.getRelative(direction);
            list.add(temp);
            if (nonPushableBlocks.contains(temp.getTypeId())) {
                return list;
            }
        }
        return list;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        ArrayList<Block> changedBlocks = this.getPistonChangeBlocks(event.getBlock(), event.getDirection());
        for (Block block : changedBlocks) {
            if (this.cancelBlockEvent(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (this.cancelBlockEvent(event.getRetractLocation().getBlock())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (this.cancelBlockEvent(event.getToBlock())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (this.cancelBlockEvent(block)) {
                event.setCancelled(true);
                event.setYield(0f);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (this.cancelBlockEvent(event.getBlock())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityBreakDoor(EntityBreakDoorEvent event) {
        if (this.cancelBlockEvent(event.getBlock())) {
            event.setCancelled(true);
            return;
        }
    }

    private boolean cancelBlockEvent(Block block) {
        // update the BlockVector & the ProtectionInfo
        this.vector.update(block.getLocation());
        this.protectionInfo.update(this.vector);

        // cancel the event, if the block is protected
        if (this.protectionInfo.hasAnyProtection()) {
            return true;
        }

        return false;
    }
}