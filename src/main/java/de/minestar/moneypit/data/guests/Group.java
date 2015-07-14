package de.minestar.moneypit.data.guests;

import java.util.Collection;
import java.util.HashSet;

public class Group implements Comparable<Group> {
    private String _owner;
    private final String _name;
    private final HashSet<String> _playerList;

    public Group(String owner, String name) {
        _owner = owner;
        _name = name;
        _playerList = new HashSet<String>();
    }

    public String getName() {
        return _name;
    }

    public String getOwner() {
        return _owner;
    }

    public void setOwner(String owner) {
        _owner = owner;
    }

    public void addPlayer(String playerName) {
        _playerList.add(playerName.toLowerCase());
    }

    public void removePlayer(String playerName) {
        _playerList.remove(playerName.toLowerCase());
    }

    public boolean hasPlayer(String playerName) {
        return _playerList.contains(playerName.toLowerCase());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Group) {
            Group grp = (Group) obj;
            return _owner.equalsIgnoreCase(grp._owner) && _name.equalsIgnoreCase(grp._name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return _name.hashCode();
    }

    @Override
    public int compareTo(Group o) {
        return _name.compareTo(o._name);
    }

    public void addPlayers(Collection<String> list) {
        for (String playerName : list) {
            this.addPlayer(playerName);
        }
    }

    public void removePlayers(Collection<String> list) {
        for (String playerName : list) {
            this.removePlayer(playerName);
        }
    }

    public HashSet<String> getPlayerList() {
        return _playerList;
    }
}
