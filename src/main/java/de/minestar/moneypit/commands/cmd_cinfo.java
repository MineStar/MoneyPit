package de.minestar.moneypit.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.PlayerState;

public class cmd_cinfo extends AbstractCommand {

    public cmd_cinfo(String syntax, String arguments, String node) {
        super(MoneyPitCore.NAME, syntax, arguments, node);
        this.description = "Get some information about a protection.";
    }

    public void execute(String[] args, Player player) {
        MoneyPitCore.playerManager.setState(player.getName(), PlayerState.PROTECTION_INFO);
        PlayerUtils.sendMessage(player, ChatColor.DARK_AQUA, MoneyPitCore.NAME, "Click on a protected block to get some information!");
    }
}