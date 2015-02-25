package de.minestar.moneypit.commands;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.PlayerState;

public class cmd_cinvite extends AbstractExtendedCommand {

    public cmd_cinvite(String syntax, String arguments, String node) {
        super(MoneyPitCore.NAME, syntax, arguments, node);
        this.description = "Invite players to a private protection.";
    }

    public static HashSet<String> parseGuestList(String[] args) {
        // create guestList
        HashSet<String> guestList = new HashSet<String>();
        for (String name : args) {
            String correctName = PlayerUtils.getCorrectPlayerName(name);
            if (correctName == null) {
                correctName = name;
            }
            guestList.add(correctName);
        }
        return guestList;
    }

    public void execute(String[] args, Player player) {
        // create guestList
        HashSet<String> guestList = cmd_cinvite.parseGuestList(args);

        // send info
        PlayerUtils.sendMessage(player, ChatColor.DARK_AQUA, MoneyPitCore.NAME, "Click on a private protection to invite the following people:");
        String infoMessage = "";
        int i = 0;
        for (String name : guestList) {
            infoMessage += name;
            ++i;
            if (i < guestList.size()) {
                infoMessage += ", ";
            }
        }
        PlayerUtils.sendInfo(player, infoMessage);

        // set states
        MoneyPitCore.playerManager.setGuestList(player.getName(), guestList);
        MoneyPitCore.playerManager.setState(player.getName(), PlayerState.PROTECTION_INVITE);
    }
}