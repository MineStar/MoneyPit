package de.minestar.moneypit.queues;

import java.util.Random;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.Core;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.ProtectionType;
import de.minestar.moneypit.modules.Module;

public class AddProtectionQueue implements Queue {

    private final Player player;
    private final Module module;
    private final BlockVector vector;
    private final ProtectionType protectionType;

    public AddProtectionQueue(Player player, Module module, BlockVector vector, ProtectionType protectionType) {
        this.player = player;
        this.module = module;
        this.vector = vector;
        this.protectionType = protectionType;
    }

    @Override
    public void execute() {
        Block block = vector.getLocation().getBlock();
        if (player.isOnline()) {
            // protect private
            Random random = new Random();
            module.addProtection(random.nextInt(1000000), vector, player.getName(), this.protectionType, block.getData());
            if (this.protectionType == ProtectionType.PRIVATE) {
                PlayerUtils.sendSuccess(player, Core.NAME, "Private protection created.");
            } else {
                PlayerUtils.sendSuccess(player, Core.NAME, "Public protection created.");
            }
        }
    }

    @Override
    public BlockVector getVector() {
        return this.vector;
    }
}
