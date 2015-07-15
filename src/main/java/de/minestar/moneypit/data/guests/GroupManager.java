package de.minestar.moneypit.data.guests;

import java.util.HashMap;
import java.util.Map;

public class GroupManager {
    private static final Map<String, Map<String, Group>> GROUPS = new HashMap<String, Map<String, Group>>();

    public static void clear() {
        GROUPS.clear();
    }

    public static Group createGroup(String owner, String groupName) {
        Group group = getGroup(owner, groupName);
        if (group == null) {
            Map<String, Group> map = GROUPS.get(owner.toLowerCase());
            if (map == null) {
                map = new HashMap<String, Group>();
                GROUPS.put(owner.toLowerCase(), map);
            }

            group = new Group(owner, groupName);
            map.put(group.getName().toLowerCase(), group);
        }
        return group;
    }

    public static Map<String, Group> getGroups(String owner) {
        return GROUPS.get(owner.toLowerCase());
    }

    public static void removeGroup(String owner, String groupName) {
        Group group = getGroup(owner, groupName);
        if (group == null) {
            return;
        }
        group.markDeleted();
        GROUPS.get(owner.toLowerCase()).remove(groupName.toLowerCase());
        if (GROUPS.get(owner.toLowerCase()).isEmpty()) {
            GROUPS.remove(owner.toLowerCase());
        }
    }

    public static boolean hasGroup(String owner, String groupName) {
        return getGroup(owner, groupName) != null;
    }

    public static Group getGroup(String owner, String groupName) {
        Map<String, Group> map = GROUPS.get(owner.toLowerCase());
        if (map != null) {
            return map.get(groupName.toLowerCase());
        }
        return null;
    }
}
