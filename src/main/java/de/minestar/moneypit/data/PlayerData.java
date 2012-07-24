package de.minestar.moneypit.data;

public class PlayerData {
    private final String name;
    private PlayerState state;

    public PlayerData(String playerName) {
        this.name = playerName;
        this.state = PlayerState.NORMAL;
    }

    /**
     * @return the state
     */
    public PlayerState getState() {
        return state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(PlayerState state) {
        this.state = state;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
