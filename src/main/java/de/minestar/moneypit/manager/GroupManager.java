package de.minestar.moneypit.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.bukkit.gemo.patchworking.GuestGroup;

public class GroupManager {

    private static Map<String, HashSet<GuestGroup>> groups = new HashMap<String, HashSet<GuestGroup>>();

    public static boolean containsGroup(String owner, String groupName) {
        HashSet<GuestGroup> groupSet = groups.get(owner.toLowerCase());
        if (groupSet != null) {
            for (GuestGroup group : groupSet) {
                if (group.getName().equalsIgnoreCase(groupName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static GuestGroup getGroup(String owner, String groupName) {
        HashSet<GuestGroup> groupSet = groups.get(owner.toLowerCase());
        if (groupSet != null) {
            for (GuestGroup group : groupSet) {
                if (group.getName().equalsIgnoreCase(groupName)) {
                    return group;
                }
            }
        }
        return null;
    }

    public static boolean removeGroup(GuestGroup group) {
        HashSet<GuestGroup> groupSet = groups.get(group.getOwner().toLowerCase());
        if (groupSet != null) {
            return groupSet.remove(group);
        }
        return false;
    }

    public static void addGroup(GuestGroup group) {
        HashSet<GuestGroup> groupSet = groups.get(group.getOwner());
        if (groupSet == null) {
            groupSet = new HashSet<GuestGroup>();
            groups.put(group.getOwner().toLowerCase(), groupSet);
        }
        if (!groupSet.contains(group)) {
            groupSet.add(group);
        }
    }
}
