package de.minestar.moneypit.queues;

import java.util.Random;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.Core;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.ProtectionInfo;
import de.minestar.moneypit.data.protection.ProtectionType;
import de.minestar.moneypit.modules.Module;

public class AddQueue {

    private final Player player;
    private final Module module;
    private final BlockVector vector;
    private final int wantedTypeID;
    private final ProtectionInfo protectionInfo;

    public AddQueue(Player player, Module module, BlockVector vector, int wantedTypeID, ProtectionInfo protectionInfo) {
        this.player = player;
        this.module = module;
        this.vector = vector;
        this.wantedTypeID = wantedTypeID;
        this.protectionInfo = protectionInfo;
    }

    public void execute() {
        Block block = vector.getLocation().getBlock();
        if (wantedTypeID == block.getTypeId() && player.isOnline()) {
            // add protection, if it isn't protected yet
            if (!this.protectionInfo.hasAnyProtection()) {
                // protect private
                Random random = new Random();
                module.addProtection(random.nextInt(1000000), vector, player.getName(), ProtectionType.PRIVATE, block.getData());
                PlayerUtils.sendSuccess(player, Core.NAME, "Private protection created.");
            } else {
                PlayerUtils.sendError(player, Core.NAME, "Cannot create protection!");
                PlayerUtils.sendInfo(player, "This block is already protected.");
            }
        }
    }

    public BlockVector getVector() {
        return this.vector;
    }
}
