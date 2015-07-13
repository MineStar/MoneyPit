package de.minestar.moneypit.commands;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.bukkit.gemo.patchworking.GuestGroup;

import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.manager.GroupManager;

public class cmd_cgroup extends AbstractExtendedCommand {

    public cmd_cgroup(String syntax, String arguments, String node) {
        super(MoneyPitCore.NAME, syntax, arguments, node);
        this.description = "Add/edit/remove a group for invites.";
    }

    public static HashSet<String> parseGuestList(String[] args) {
        // create guestList
        HashSet<String> guestList = new HashSet<String>();
        for (int i = 1; i < args.length; i++) {
            String correctName = PlayerUtils.getCorrectPlayerName(args[i]);
            if (correctName == null) {
                correctName = args[i];
            }
            guestList.add(correctName);
        }
        return guestList;
    }

    public void execute(String[] args, Player player) {
        if (args.length < 1) {
            PlayerUtils.sendError(player, MoneyPitCore.NAME, "Wrong syntax! Usage: " + this.getSyntax());
            return;
        } else if (args.length == 1) {
            // delete group
            String groupName = args[0];
            GuestGroup group = GroupManager.getGroup(player.getName(), groupName);
            if (group == null) {
                PlayerUtils.sendError(player, MoneyPitCore.NAME, "Group '" + groupName + "' not found!");
                return;
            }
            // update database
            if (!MoneyPitCore.databaseManager.deleteGroup(group)) {
                PlayerUtils.sendError(player, MoneyPitCore.NAME, "Can't delete group. Please contact an admin.");
                return;
            }

            // remove group
            GroupManager.removeGroup(group);

            // update protections...
            MoneyPitCore.protectionManager.resetGuestList(group);
            MoneyPitCore.entityProtectionManager.resetGuestList(group);
        } else {
            // edit|create group
            String groupName = args[0];
            GuestGroup group = GroupManager.getGroup(player.getName(), groupName);
            boolean groupIsNew = (group == null);
            if (groupIsNew) {
                group = new GuestGroup(groupName, player.getName());
                GroupManager.addGroup(group);
            } else {
                group.clear();
            }

            // create guestList
            group.add(cmd_cgroup.parseGuestList(args));

            // save to database
            if (groupIsNew) {
                if (!MoneyPitCore.databaseManager.createGroup(group)) {
                    PlayerUtils.sendError(player, MoneyPitCore.NAME, "Can't save group. Please contact an admin.");
                    return;
                }
            } else {
                if (!MoneyPitCore.databaseManager.updateGuestList(null, group)) {
                    PlayerUtils.sendError(player, MoneyPitCore.NAME, "Can't edit group. Please contact an admin.");
                    return;
                }
            }

            // send info
            PlayerUtils.sendMessage(player, ChatColor.DARK_AQUA, MoneyPitCore.NAME, "Players in group '" + groupName + "' set to: ");
            String infoMessage = "";
            int i = 0;
            for (String name : group.getAll()) {
                infoMessage += name;
                ++i;
                if (i < group.getAll().size()) {
                    infoMessage += ", ";
                }
            }
            PlayerUtils.sendInfo(player, infoMessage);
        }
    }
}