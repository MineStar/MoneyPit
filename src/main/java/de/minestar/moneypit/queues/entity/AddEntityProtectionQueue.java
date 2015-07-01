package de.minestar.moneypit.queues.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.bukkit.gemo.patchworking.ProtectionType;

import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.protection.EntityProtection;

public class AddEntityProtectionQueue implements EntityQueue {

    private final Player player;
    private final Entity entity;
    private final ProtectionType protectionType;

    public AddEntityProtectionQueue(Player player, Entity entity, ProtectionType protectionType) {
        this.player = player;
        this.entity = entity;
        this.protectionType = protectionType;
    }

    @Override
    public boolean execute() {
        if (player.isOnline()) {
            // create protection
            EntityProtection protection = MoneyPitCore.databaseManager.createEntityProtection(player.getName(), entity.getUniqueId(), entity.getType(), protectionType);
            if (protection == null) {
                PlayerUtils.sendError(player, MoneyPitCore.NAME, "Could not save new protection!");
                PlayerUtils.sendInfo(player, "Please contact an admin.");
                return false;
            }
            MoneyPitCore.entityProtectionManager.addProtection(protection);
            if (this.protectionType == ProtectionType.PRIVATE) {
                PlayerUtils.sendSuccess(player, MoneyPitCore.NAME, "Private protection created.");
            } else if (this.protectionType == ProtectionType.PUBLIC) {
                PlayerUtils.sendSuccess(player, MoneyPitCore.NAME, "Public protection created.");
            }
            return true;
        }
        return false;
    }
}
