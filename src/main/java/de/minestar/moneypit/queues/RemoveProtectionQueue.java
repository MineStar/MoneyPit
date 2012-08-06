package de.minestar.moneypit.queues;

import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.BlockVector;

public class RemoveProtectionQueue implements Queue {

    private final Player player;
    private final BlockVector vector;

    public RemoveProtectionQueue(Player player, BlockVector vector) {
        this.player = player;
        this.vector = vector;
    }

    @Override
    public boolean execute() {
        // try to delete the protection in the database
        if (MoneyPitCore.databaseManager.deleteProtection(this.vector)) {
            // remove protection from ingamehandler
            MoneyPitCore.protectionManager.removeProtection(this.vector);

            // send info
            PlayerUtils.sendSuccess(this.player, MoneyPitCore.NAME, "Protection removed.");
            return true;
        } else {
            PlayerUtils.sendError(player, MoneyPitCore.NAME, "Could not delete protection!");
            PlayerUtils.sendInfo(player, "Please contact an admin.");
            return false;
        }
    }

    @Override
    public BlockVector getVector() {
        return this.vector;
    }
}
