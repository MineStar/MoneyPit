package de.minestar.moneypit.manager;

import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.Core;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.Protection;
import de.minestar.moneypit.data.ProtectionType;
import de.minestar.moneypit.data.SubProtectionHolder;
import de.minestar.moneypit.modules.Module;

public class DebugListener implements Listener {

    private ModuleManager moduleManager;
    private ProtectionManager protectionManager;

    public DebugListener() {
        this.moduleManager = Core.moduleManager;
        this.protectionManager = Core.protectionManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // event is already cancelled => return
        if (event.isCancelled()) {
            return;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // event is already cancelled => return
        if (event.isCancelled()) {
            return;
        }

        Action action = event.getAction();
        if (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK) {
            ConsoleUtils.printInfo(Core.NAME, "------------------------------");

            BlockVector vector = new BlockVector(event.getClickedBlock().getLocation());

            // no sneak => print info about protections & return
            if (!event.getPlayer().isSneaking()) {
                // CHECK: Protection?
                Protection protection = this.protectionManager.getProtection(vector);
                if (protection != null) {
                    ConsoleUtils.printInfo(Core.NAME, "Block is protected!");
                    ConsoleUtils.printInfo(Core.NAME, protection.toString());
                    return;
                }

                // CHECK: SubProtection?
                SubProtectionHolder holder = this.protectionManager.getSubProtectionHolder(vector);
                if (holder != null) {
                    ConsoleUtils.printInfo(Core.NAME, "Block is a subprotection!");
                    for (int i = 0; i < holder.getSize(); i++) {
                        ConsoleUtils.printInfo(Core.NAME, "#" + (i + 1) + " : " + holder.getProtections().get(i));
                    }
                    return;
                }
                return;
            }

            // the module must be registered
            Module module = this.moduleManager.getModule(event.getClickedBlock().getTypeId());
            if (module == null) {
                return;
            }

            boolean isLeftClick = (action == Action.LEFT_CLICK_BLOCK);
            // left click => try to add a protection
            // right click => try to remove the protection
            if (isLeftClick) {
                if (!this.protectionManager.hasProtection(vector) && !this.protectionManager.hasSubProtectionHolder(vector)) {
                    Random random = new Random();
                    module.addProtection(random.nextInt(1000000), vector, event.getPlayer().getName(), ProtectionType.PRIVATE, event.getClickedBlock().getData());
                    PlayerUtils.sendSuccess(event.getPlayer(), Core.NAME, "This block is now protected!");
                } else {
                    PlayerUtils.sendError(event.getPlayer(), Core.NAME, "This block is already protected!");
                }
                return;
            } else {
                if (!this.protectionManager.hasProtection(vector)) {
                    PlayerUtils.sendError(event.getPlayer(), Core.NAME, "This block is not protected!");
                } else {
                    this.protectionManager.removeProtection(vector);
                    PlayerUtils.sendError(event.getPlayer(), Core.NAME, "This block is no longer protected!");
                }
            }
        }
    }
}
