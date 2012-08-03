package de.minestar.moneypit.queues;

import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.Core;
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
    public void execute() {
        // Remove all SubProtections
        SubProtection protection;
        for (int i = 0; i < this.protectionInfo.getSubProtections().getSize(); i++) {
            protection = this.protectionInfo.getSubProtections().getProtection(i);
            if (protection != null) {
                Core.protectionManager.removeProtection(protection.getParent().getVector());
            }
        }

        // Send info
        PlayerUtils.sendSuccess(this.player, Core.NAME, "Protection removed!");
        return;
    }

    @Override
    public BlockVector getVector() {
        return this.vector;
    }
}
