package de.minestar.moneypit.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.PlayerState;

public class cmd_cuninviteall extends AbstractCommand {

    public cmd_cuninviteall(String syntax, String arguments, String node) {
        super(MoneyPitCore.NAME, syntax, arguments, node);
        this.description = "Uninvite all players from a private protection.";
    }

    public void execute(String[] args, Player player) {
        // send info
        PlayerUtils.sendMessage(player, ChatColor.DARK_AQUA, MoneyPitCore.NAME, "Click on a private protection to uninvite all players.");

        // set states
        MoneyPitCore.playerManager.setState(player.getName(), PlayerState.PROTECTION_UNINVITEALL);
    }
}