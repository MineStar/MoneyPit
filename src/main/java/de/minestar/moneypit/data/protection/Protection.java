package de.minestar.moneypit.data.protection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.subprotection.SubProtection;

public class Protection {
    private final int ID;
    private final BlockVector vector;
    private final String owner;
    private ProtectionType type;
    private HashSet<String> guests;
    private HashMap<BlockVector, SubProtection> subProtections;

    /**
     * Constructor
     * 
     * @param owner
     * @param type
     */
    public Protection(int ID, BlockVector vector, String owner, ProtectionType type) {
        this.ID = ID;
        this.vector = vector;
        this.owner = owner;
        this.type = type;
        this.guests = null;
        this.subProtections = null;
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
     * Get the vector of this protection
     * 
     * @return the vector
     */
    public BlockVector getVector() {
        return this.vector;
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

    /**
     * Check if the given name is the owner of this protection
     * 
     * @param otherName
     * @return <b>true</b> if the name is the ownername, otherwise <b>false</b>
     */
    public boolean isOwner(String otherName) {
        return this.owner.equalsIgnoreCase(otherName);
    }

    /**
     * Get all SubProtection
     * 
     * @return a Collection<SubProtection> of SubProtection
     */
    public Collection<SubProtection> getSubProtections() {
        if (this.subProtections != null) {
            return this.subProtections.values();
        }
        return null;
    }

    /**
     * Check if this Protection has any SubProtections
     * 
     * @return <b>true</b> if we have at least one SubProtection, otherwise
     *         <b>false</b>
     */
    public boolean hasAnySubProtection() {
        if (this.subProtections != null) {
            return (this.subProtections.size() > 0);
        }
        return false;
    }

    /**
     * Add a SubProtection
     * 
     * @param vector
     * @param subProtection
     */
    public void addSubProtection(SubProtection subProtection) {
        if (this.subProtections == null) {
            this.subProtections = new HashMap<BlockVector, SubProtection>();
        }

        if (!this.hasSubProtection(subProtection.getVector())) {
            this.subProtections.put(subProtection.getVector(), subProtection);
        }
    }

    /**
     * Remove a SubProtection
     * 
     * @param vector
     */
    public void removeSubProtection(BlockVector vector) {
        if (this.subProtections != null) {
            this.subProtections.remove(vector);
        }

        if (!this.hasAnySubProtection()) {
            this.subProtections = null;
        }
    }

    /**
     * Check if the given Vector is a SubProtection of this protection
     * 
     * @param otherName
     * @return <b>true</b> if the name is the ownername, otherwise <b>false</b>
     */
    public boolean hasSubProtection(BlockVector vector) {
        if (this.subProtections != null) {
            return this.subProtections.containsKey(vector);
        }
        return false;
    }

    public boolean equals(Protection protection) {
        return this.ID == protection.ID && this.vector.equals(protection.getVector());
    }

    @Override
    public String toString() {
        return "Protection={ ID:" + this.ID + " ; Type:" + this.type.name() + " ; Owner:" + this.owner + " ; " + this.vector.toString() + " }";
    }
}
