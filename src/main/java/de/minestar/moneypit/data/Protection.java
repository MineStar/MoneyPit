package de.minestar.moneypit.data;

import java.util.HashSet;

public class Protection {
    private final int ID;
    private final String owner;
    private ProtectionType type;
    private HashSet<String> guests;

    /**
     * Constructor
     * 
     * @param owner
     * @param type
     */
    public Protection(int ID, String owner, ProtectionType type) {
        this.ID = ID;
        this.owner = owner;
        this.type = type;
        this.guests = null;
    }

    /**
     * Get the ID of this protection
     * 
     * @return the unique ID
     */
    public int getID() {
        return ID;
    }

    /**
     * Get the owner of this protection
     * 
     * @return the owner;
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Get the type of this protection
     * 
     * @return the type
     */
    public ProtectionType getType() {
        return type;
    }

    /**
     * Set the type of this protection
     * 
     * @param type
     */
    public void setType(ProtectionType type) {
        this.type = type;
    }

    /**
     * Add a guest to the guestlist
     * 
     * @param guest
     */
    public void addGuest(String guest) {
        if (this.guests == null) {
            this.guests = new HashSet<String>();
        }
        this.guests.add(guest);
    }

    /**
     * Remove a guest from the guestlist
     * 
     * @param guest
     */
    public void removeGuest(String guest) {
        if (this.guests != null) {
            this.guests.remove(guest);
        }
        if (this.guests.size() < 1) {
            this.guests = null;
        }
    }

    /**
     * Check if someone is on the guestlist
     * 
     * @param guest
     * @return <b>true</b> if the name is found, otherwise <b>false</b>
     */
    public boolean isGuest(String guest) {
        if (this.guests != null) {
            return this.guests.contains(guest);
        }
        return false;
    }

    public boolean equals(Protection protection) {
        return this.ID == protection.ID;
    }
}
