package de.minestar.moneypit.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.PlayerState;

public class cmd_ctoggle extends AbstractCommand {

    public cmd_ctoggle(String syntax, String arguments, String node) {
        super(MoneyPitCore.NAME, syntax, arguments, node);
        this.description = "Toggle the keepmode";
    }

    public void execute(String[] args, Player player) {
        if (MoneyPitCore.playerManager.keepsMode(player.getName())) {
            MoneyPitCore.playerManager.removeKeepMode(player.getName());
            MoneyPitCore.playerManager.setState(player.getName(), PlayerState.NORMAL);
            MoneyPitCore.playerManager.clearGuestList(player.getName());
            PlayerUtils.sendMessage(player, ChatColor.DARK_AQUA, MoneyPitCore.NAME, "You are no longer in endless mode...");
        } else {
            MoneyPitCore.playerManager.setKeepMode(player.getName());
            PlayerUtils.sendMessage(player, ChatColor.DARK_AQUA, MoneyPitCore.NAME, "You are now in endless mode...");
        }
    }
}