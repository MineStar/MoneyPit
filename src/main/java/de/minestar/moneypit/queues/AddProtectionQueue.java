package de.minestar.moneypit.queues;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.protection.ProtectionType;
import de.minestar.moneypit.modules.Module;

public class AddProtectionQueue implements Queue {

    private final Player player;
    private final Module module;
    private final BlockVector vector;
    private final ProtectionType protectionType;

    private byte subData = -1;

    public AddProtectionQueue(Player player, Module module, BlockVector vector, ProtectionType protectionType, byte subData) {
        this.player = player;
        this.module = module;
        this.vector = vector;
        this.protectionType = protectionType;
        this.subData = subData;
    }

    public AddProtectionQueue(Player player, Module module, BlockVector vector, ProtectionType protectionType) {
        this.player = player;
        this.module = module;
        this.vector = vector;
        this.protectionType = protectionType;
    }

    @Override
    public boolean execute() {
        Block block = vector.getLocation().getBlock();
        byte data = block.getData();
        if (subData != -1) {
            data = subData;
        }
        if (player.isOnline()) {
            // handle giftprotections
            if (this.protectionType == ProtectionType.GIFT) {
                if (MoneyPitCore.protectionManager.hasGiftProtection(player.getName())) {
                    PlayerUtils.sendError(player, MoneyPitCore.NAME, "You can only have one gift protection.");
                    return false;
                }
            }

            // create protection
            Protection protection = MoneyPitCore.databaseManager.createProtection(vector, player.getName(), this.protectionType);
            if (protection == null) {
                PlayerUtils.sendError(player, MoneyPitCore.NAME, "Could not save new protection!");
                PlayerUtils.sendInfo(player, "Please contact an admin.");
                return false;
            }

            boolean result = module.addProtection(protection, data);
            if (this.protectionType == ProtectionType.PRIVATE && result) {
                PlayerUtils.sendSuccess(player, MoneyPitCore.NAME, "Private protection created.");
            } else if (this.protectionType == ProtectionType.PUBLIC && result) {
                PlayerUtils.sendSuccess(player, MoneyPitCore.NAME, "Public protection created.");
            } else if (this.protectionType == ProtectionType.GIFT) {
                PlayerUtils.sendSuccess(player, MoneyPitCore.NAME, "Gift protection created.");
            }
            return true;
        }
        return false;
    }

    @Override
    public BlockVector getVector() {
        return this.vector;
    }
}
