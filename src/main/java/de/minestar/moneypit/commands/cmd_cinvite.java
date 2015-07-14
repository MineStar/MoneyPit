package de.minestar.moneypit.commands;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.PlayerState;
import de.minestar.moneypit.data.guests.Group;
import de.minestar.moneypit.data.guests.GroupManager;
import de.minestar.moneypit.data.guests.GuestHelper;

public class cmd_cinvite extends AbstractExtendedCommand {

    public cmd_cinvite(String syntax, String arguments, String node) {
        super(MoneyPitCore.NAME, syntax, arguments, node);
        this.description = "Invite players to a private protection.";
    }

    public static HashSet<String> parseGuestList(String playerName, String[] args) {
        // create guestList
        HashSet<String> guestList = new HashSet<String>();
        for (String name : args) {
            if (name.startsWith(GuestHelper.GROUP_PREFIX)) {
                if (name.matches("(" + GuestHelper.GROUP_PREFIX + ")([a-zA-Z0-9_])*")) {
                    Group group = GroupManager.getGroup(playerName, name);
                    if (group != null) {
                        guestList.add(group.getName());
                    }
                }
            } else {
                String correctName = PlayerUtils.getCorrectPlayerName(name);
                if (correctName == null) {
                    correctName = name;
                }
                guestList.add(correctName);
            }
        }
        return guestList;
    }

    public void execute(String[] args, Player player) {
        // create guestList
        HashSet<String> guestList = cmd_cinvite.parseGuestList(player.getName(), args);

        // send info
        PlayerUtils.sendMessage(player, ChatColor.DARK_AQUA, MoneyPitCore.NAME, "Click on a private protection to invite the following people:");
        String infoMessage = "";
        int i = 0;
        for (String name : guestList) {
            if (name.startsWith(GuestHelper.GROUP_PREFIX)) {
                infoMessage += name.replaceFirst(GuestHelper.GROUP_PREFIX, "group: ");
            } else {
                infoMessage += name;
            }
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