package de.minestar.moneypit.manager;

import java.util.HashMap;
import java.util.HashSet;

import de.minestar.moneypit.data.PlayerData;
import de.minestar.moneypit.data.PlayerState;

public class PlayerManager {

    private HashMap<String, PlayerData> playerList;
    private HashSet<String> keepMode;
    private HashMap<String, HashSet<String>> guestMap;

    public PlayerManager() {
        this.playerList = new HashMap<String, PlayerData>(64);
        this.guestMap = new HashMap<String, HashSet<String>>(64);
        this.keepMode = new HashSet<String>();
    }

    public PlayerData getData(String playerName) {
        PlayerData data = this.playerList.get(playerName.toLowerCase());
        if (data == null) {
            data = new PlayerData(playerName);
            this.playerList.put(playerName.toLowerCase(), data);
        }
        return data;
    }

    public void setState(String playerName, PlayerState state) {
        this.getData(playerName).setState(state);
        if (state.equals(PlayerState.NORMAL)) {
            this.removeKeepMode(playerName);
        }
    }

    public boolean keepsMode(String playerName) {
        return this.keepMode.contains(playerName);
    }

    public void removeKeepMode(String playerName) {
        this.keepMode.remove(playerName);
    }

    public void setKeepMode(String playerName) {
        this.keepMode.add(playerName);
    }

    public PlayerState getState(String playerName) {
        return this.getData(playerName).getState();
    }

    public void setGuestList(String playerName, HashSet<String> guestList) {
        this.guestMap.put(playerName, guestList);
    }

    public HashSet<String> getGuestList(String playerName) {
        HashSet<String> data = this.guestMap.get(playerName);
        if (data == null) {
            data = new HashSet<String>();
        }
        return data;
    }

    public void clearGuestList(String playerName) {
        this.guestMap.remove(playerName);
    }
}
