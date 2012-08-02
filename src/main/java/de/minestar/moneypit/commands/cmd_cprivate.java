package de.minestar.moneypit.commands;

import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.Core;
import de.minestar.moneypit.data.PlayerState;

public class cmd_cprivate extends AbstractCommand {

    public cmd_cprivate(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
        this.description = "Create a private protection.";
    }

    public void execute(String[] args, Player player) {
        Core.playerManager.setState(player.getName(), PlayerState.PROTECTION_ADD_PRIVATE);
        PlayerUtils.sendSuccess(player, Core.NAME, "Click on a block to protect it with a public protection!");
    }
}