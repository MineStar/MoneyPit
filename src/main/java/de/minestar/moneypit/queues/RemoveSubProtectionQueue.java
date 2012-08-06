package de.minestar.moneypit.queues;

import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.ProtectionInfo;
import de.minestar.moneypit.data.subprotection.SubProtection;

public class RemoveSubProtectionQueue implements Queue {

    private final Player player;
    private final BlockVector vector;
    private ProtectionInfo protectionInfo;

    public RemoveSubProtectionQueue(Player player, BlockVector vector, ProtectionInfo protectionInfo) {
        this.player = player;
        this.vector = vector;
        this.protectionInfo = protectionInfo;
    }

    @Override
    public boolean execute() {
        boolean result = true;
        // try to remove all SubProtections
        SubProtection protection;
        for (int i = 0; i < this.protectionInfo.getSubProtections().getSize(); i++) {
            protection = this.protectionInfo.getSubProtections().getProtection(i);
            if (protection != null) {
                if (MoneyPitCore.databaseManager.deleteProtection(protection.getParent().getVector())) {
                    MoneyPitCore.protectionManager.removeProtection(protection.getParent().getVector());
                } else {
                    result = false;
                }
            }
        }

        // Send info
        if (result) {
            PlayerUtils.sendSuccess(this.player, MoneyPitCore.NAME, "Protection removed!");
        } else {
            PlayerUtils.sendError(player, MoneyPitCore.NAME, "Could not delete all subprotections!");
            PlayerUtils.sendInfo(player, "Please contact an admin.");
        }
        return result;
    }

    @Override
    public BlockVector getVector() {
        return this.vector;
    }
}
