package de.minestar.moneypit.queues;

import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.Core;
import de.minestar.moneypit.data.BlockVector;

public class RemoveProtectionQueue implements Queue {

    private final Player player;
    private final BlockVector vector;

    public RemoveProtectionQueue(Player player, BlockVector vector) {
        this.player = player;
        this.vector = vector;
    }

    @Override
    public void execute() {
        // remove protection
        Core.protectionManager.removeProtection(this.vector);

        // send info
        PlayerUtils.sendSuccess(this.player, Core.NAME, "Protection removed.");
    }

    @Override
    public BlockVector getVector() {
        return this.vector;
    }
}
