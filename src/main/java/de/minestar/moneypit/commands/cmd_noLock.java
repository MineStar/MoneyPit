package de.minestar.moneypit.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;

public class cmd_noLock extends AbstractCommand {

    public cmd_noLock(String syntax, String arguments, String node) {
        super(MoneyPitCore.NAME, syntax, arguments, node);
        this.description = "Toggle the autolock";
    }

    public void execute(String[] args, Player player) {
        if (MoneyPitCore.playerManager.noAutoLock(player.getName())) {
            MoneyPitCore.playerManager.removeNoAutoLock(player.getName());
            PlayerUtils.sendMessage(player, ChatColor.DARK_AQUA, MoneyPitCore.NAME, "Autolock activated...");
        } else {
            MoneyPitCore.playerManager.setNoAutoLock(player.getName());
            PlayerUtils.sendMessage(player, ChatColor.DARK_AQUA, MoneyPitCore.NAME, "Autolock deactivated...");
        }
    }
}