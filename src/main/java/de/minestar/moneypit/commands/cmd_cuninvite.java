package de.minestar.moneypit.commands;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.PlayerState;

public class cmd_cuninvite extends AbstractExtendedCommand {

    public cmd_cuninvite(String syntax, String arguments, String node) {
        super(MoneyPitCore.NAME, syntax, arguments, node);
        this.description = "Uninvite players from a private protection.";
    }

    public void execute(String[] args, Player player) {
        // create guestList
        HashMap<String, String> guestList = cmd_cinvite.parseGuestList(args);

        // send info
        PlayerUtils.sendMessage(player, ChatColor.DARK_AQUA, MoneyPitCore.NAME, "Click on a private protection to uninvite the following people:");
        String infoMessage = "";
        int i = 0;
        for (Entry<String, String> entry : guestList.entrySet()) {
            infoMessage += entry.getKey();
            ++i;
            if (i < guestList.size()) {
                infoMessage += ", ";
            }
        }
        PlayerUtils.sendInfo(player, infoMessage);

        // set states
        MoneyPitCore.playerManager.setGuestList(player.getName(), guestList.values());
        MoneyPitCore.playerManager.setState(player.getName(), PlayerState.PROTECTION_UNINVITE);
    }
}