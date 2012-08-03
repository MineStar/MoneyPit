package de.minestar.moneypit.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.Core;
import de.minestar.moneypit.data.PlayerState;

public class cmd_cremove extends AbstractCommand {

    public cmd_cremove(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
        this.description = "Remove a protection.";
    }

    public void execute(String[] args, Player player) {
        Core.playerManager.setState(player.getName(), PlayerState.PROTECTION_REMOVE);
        PlayerUtils.sendMessage(player, ChatColor.DARK_AQUA, Core.NAME, "Click on a block to remove the protection!");
    }
}