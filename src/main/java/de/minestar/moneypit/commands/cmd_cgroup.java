package de.minestar.moneypit.commands;

import java.util.HashSet;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.guests.Group;
import de.minestar.moneypit.data.guests.GroupManager;
import de.minestar.moneypit.data.guests.GuestHelper;

public class cmd_cgroup extends AbstractExtendedCommand {

    public cmd_cgroup(String syntax, String arguments, String node) {
        super(MoneyPitCore.NAME, syntax, arguments, node);
        this.description = "Add/edit/delete a group";
    }

    public static HashSet<String> parsePlayerList(String[] args) {
        HashSet<String> playerList = new HashSet<String>();
        for (int i = 2; i < args.length; i++) {
            String correctName = PlayerUtils.getCorrectPlayerName(args[i]);
            if (correctName == null) {
                correctName = args[i];
            }
            playerList.add(correctName);
        }
        return playerList;
    }

    public void execute(String[] args, Player player) {
        // append the group-prefix
        String canonicalGroupName = "";
        String internalGroupName = "";
        if (args.length > 0) {
            if (!args[0].matches("([a-zA-Z0-9_]).*")) {
                PlayerUtils.sendError(player, MoneyPitCore.NAME, "Groupnames can only contain ['a'-'Z', '0'-'9' and '_']!");
                return;
            }
            canonicalGroupName = args[0];
            internalGroupName = GuestHelper.GROUP_PREFIX + canonicalGroupName;
        }

        if (args.length < 1) {
            // print all groups
            Map<String, Group> groups = GroupManager.getGroups(player.getName());
            if (groups == null) {
                PlayerUtils.sendError(player, MoneyPitCore.NAME, "You have no groups!");
                PlayerUtils.sendInfo(player, MoneyPitCore.NAME, "Use '/cgroup <Name> + <Players...>' to create a new group.");
            } else {
                PlayerUtils.sendMessage(player, ChatColor.GRAY, "-------------------");
                PlayerUtils.sendMessage(player, ChatColor.DARK_AQUA, "Your groups:");
                for (Group group : groups.values()) {
                    PlayerUtils.sendMessage(player, ChatColor.GRAY, " - " + group.getName().replaceFirst(GuestHelper.GROUP_PREFIX, ""));
                }
                PlayerUtils.sendMessage(player, ChatColor.GRAY, "-------------------");
            }
            return;
        } else if (args.length == 1) {
            // print info about one specific group
            Group group = GroupManager.getGroup(player.getName(), internalGroupName);
            if (group == null) {
                PlayerUtils.sendError(player, MoneyPitCore.NAME, "Group '" + canonicalGroupName + "' not found!");
                return;
            }
            PlayerUtils.sendMessage(player, ChatColor.GRAY, "-------------------");
            PlayerUtils.sendMessage(player, ChatColor.DARK_AQUA, "Members of '" + group.getName().replaceFirst(GuestHelper.GROUP_PREFIX, "") + "' :");
            int index = 0;
            String playerList = "";
            for (String playerName : group.getPlayerList()) {
                index++;
                playerList += playerName;
                if (index < group.getPlayerList().size()) {
                    playerList += ", ";
                }
            }
            PlayerUtils.sendMessage(player, ChatColor.GRAY, playerList);
            PlayerUtils.sendMessage(player, ChatColor.GRAY, "-------------------");
        } else if (args.length == 2) {
            // handle "--": delete the group
            if (args[1].equalsIgnoreCase("--")) {
                // get the group
                Group group = GroupManager.getGroup(player.getName(), internalGroupName);
                if (group == null) {
                    PlayerUtils.sendError(player, MoneyPitCore.NAME, "Group '" + canonicalGroupName + "' not found!");
                    return;
                }

                // remove from database
                if (!MoneyPitCore.databaseManager.deleteGroup(group)) {
                    PlayerUtils.sendError(player, MoneyPitCore.NAME, "Could not delete '" + group.getName().replaceFirst(GuestHelper.GROUP_PREFIX, "") + "'!");
                    PlayerUtils.sendInfo(player, MoneyPitCore.NAME, "Please contact an admin.");
                    return;
                }

                // remove from GroupManager & send success
                GroupManager.removeGroup(player.getName(), group.getName());
                PlayerUtils.sendSuccess(player, MoneyPitCore.NAME, "Group '" + group.getName().replaceFirst(GuestHelper.GROUP_PREFIX, "") + "' deleted!");
            } else {
                PlayerUtils.sendError(player, MoneyPitCore.NAME, "Wrong syntax! Usage: " + this.getSyntax());
                PlayerUtils.sendInfo(player, MoneyPitCore.NAME, "Use '/cgroup <Name> + <Players...>' to add players.");
                PlayerUtils.sendInfo(player, MoneyPitCore.NAME, "Use '/cgroup <Name> - <Players...>' to remove players.");
            }
        } else if (args.length > 2) {
            if (args[1].equalsIgnoreCase("-") || args[1].equalsIgnoreCase("+")) {
                // get the group
                Group group;
                if (args[1].equalsIgnoreCase("-")) {
                    group = GroupManager.getGroup(player.getName(), internalGroupName);
                    if (group == null) {
                        PlayerUtils.sendError(player, MoneyPitCore.NAME, "Group '" + canonicalGroupName + "' not found!");
                        return;
                    }
                } else {
                    // create group, if is doesn't exist
                    if (!GroupManager.hasGroup(player.getName(), internalGroupName)) {
                        group = GroupManager.createGroup(player.getName(), internalGroupName);

                        // create in database
                        if (!MoneyPitCore.databaseManager.addGroup(group)) {
                            PlayerUtils.sendError(player, MoneyPitCore.NAME, "Could not save '" + group.getName().replaceFirst(GuestHelper.GROUP_PREFIX, "") + "' in database!");
                            PlayerUtils.sendInfo(player, MoneyPitCore.NAME, "Please contact an admin.");
                            return;
                        }
                    } else {
                        // otherwise: check the name
                        group = GroupManager.getGroup(player.getName(), internalGroupName);
                        if (group == null) {
                            PlayerUtils.sendError(player, MoneyPitCore.NAME, "Group '" + canonicalGroupName + "' not found!");
                            return;
                        }
                    }
                }

                // update the guestlist
                HashSet<String> playerList = parsePlayerList(args);
                if (args[1].equalsIgnoreCase("-")) {
                    group.removePlayers(playerList);
                } else {
                    group.addPlayers(playerList);
                }

                // update in database
                if (!MoneyPitCore.databaseManager.updateGroupGuestList(group)) {
                    PlayerUtils.sendError(player, MoneyPitCore.NAME, "Could not update '" + group.getName().replaceFirst(GuestHelper.GROUP_PREFIX, "") + "' in database!");
                    PlayerUtils.sendInfo(player, MoneyPitCore.NAME, "Please contact an admin.");
                    return;
                }

                // remove empty groups
                if (group.getPlayerList().isEmpty()) {
                    // remove from database
                    if (!MoneyPitCore.databaseManager.deleteGroup(group)) {
                        PlayerUtils.sendError(player, MoneyPitCore.NAME, "Could not delete '" + group.getName().replaceFirst(GuestHelper.GROUP_PREFIX, "") + "'!");
                        PlayerUtils.sendInfo(player, MoneyPitCore.NAME, "Please contact an admin.");
                        return;
                    } else {
                        PlayerUtils.sendSuccess(player, MoneyPitCore.NAME, "Group '" + group.getName().replaceFirst(GuestHelper.GROUP_PREFIX, "") + "' deleted!");
                        return;
                    }
                }

                // send success
                int index = 0;
                String playersInGroup = "";
                for (String playerName : group.getPlayerList()) {
                    index++;
                    playersInGroup += playerName;
                    if (index < group.getPlayerList().size()) {
                        playersInGroup += ", ";
                    }
                }
                PlayerUtils.sendSuccess(player, MoneyPitCore.NAME, "Group '" + group.getName().replaceFirst(GuestHelper.GROUP_PREFIX, "") + "' updated!");
                PlayerUtils.sendInfo(player, MoneyPitCore.NAME, "Players in group: " + playersInGroup);
            } else {
                PlayerUtils.sendError(player, MoneyPitCore.NAME, "Wrong syntax! Usage: " + this.getSyntax());
                PlayerUtils.sendInfo(player, MoneyPitCore.NAME, "Use '/cgroup <Name> + <Players...>' to add players.");
                PlayerUtils.sendInfo(player, MoneyPitCore.NAME, "Use '/cgroup <Name> - <Players...>' to remove players.");
            }
        }
    }
}