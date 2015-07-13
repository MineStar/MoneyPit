package de.minestar.moneypit.commands;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.bukkit.gemo.patchworking.GuestGroup;

import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.PlayerState;
import de.minestar.moneypit.manager.GroupManager;

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
        if (args[0].startsWith("g:")) {
            String groupName = args[0].replaceFirst("g:", "");
            GuestGroup group = GroupManager.getGroup(player.getName(), groupName);
            if (group == null) {
                PlayerUtils.sendError(player, MoneyPitCore.NAME, "Group '" + groupName + "' not found.");
                listGroups(player);
                return;
            }
            MoneyPitCore.playerManager.setGuestList(player.getName(), groupName);

            // send info
            PlayerUtils.sendMessage(player, ChatColor.DARK_AQUA, MoneyPitCore.NAME, "Click on a private protection to invite the group '" + group.getName() + "'");

            // set states
            MoneyPitCore.playerManager.setState(player.getName(), PlayerState.PROTECTION_INVITE);
        } else {
            // create guestList
            HashSet<String> guestList = cmd_cinvite.parseGuestList(args);
            MoneyPitCore.playerManager.setGuestList(player.getName(), guestList);

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
            MoneyPitCore.playerManager.setState(player.getName(), PlayerState.PROTECTION_INVITE);
        }
    }

    private void listGroups(Player player) {
        HashSet<GuestGroup> groups = GroupManager.getGroups(player.getName());
        if (groups == null) {
            PlayerUtils.sendInfo(player, "You have no groups yet. Create one with /cgroup...");
        } else {
            PlayerUtils.sendMessage(player, ChatColor.GRAY, "-------------------");
            PlayerUtils.sendMessage(player, ChatColor.GRAY, "Your groups:");
            PlayerUtils.sendMessage(player, ChatColor.GRAY, "-------------------");
            for (GuestGroup group : groups) {
                PlayerUtils.sendMessage(player, ChatColor.AQUA, " - '" + group.getName() + "'");
            }
            PlayerUtils.sendMessage(player, ChatColor.GRAY, "-------------------");
        }
    }
}