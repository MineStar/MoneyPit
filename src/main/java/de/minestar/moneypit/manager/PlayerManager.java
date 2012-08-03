package de.minestar.moneypit.manager;

import java.util.HashMap;

import de.minestar.moneypit.data.PlayerData;
import de.minestar.moneypit.data.PlayerState;

public class PlayerManager {

    private HashMap<String, PlayerData> playerList;

    public PlayerManager() {
        this.playerList = new HashMap<String, PlayerData>(128);
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
    }

    public PlayerState getState(String playerName) {
        return this.getData(playerName).getState();
    }
}
