package de.minestar.moneypit.data.subprotection;

import org.bukkit.entity.Player;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.protection.ProtectionType;

public class SubProtection {

    private final BlockVector vector;
    private final Protection parent;

    /**
     * Constructor
     * 
     * @param parent
     */
    public SubProtection(BlockVector vector, Protection parent) {
        this.vector = vector;
        this.parent = parent;
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
     * Get the parent of this protection
     * 
     * @return the parent
     */
    public Protection getParent() {
        return this.parent;
    }

    /**
     * Get the ModuleID
     * 
     * @return the ModuleID of this Protection
     */
    public int getModuleID() {
        return this.parent.getModuleID();
    }

    /**
     * Get the type of the parent protection
     * 
     * @return the type
     */
    public ProtectionType getType() {
        return this.parent.getType();
    }

    /**
     * Add a guest to the guestlist
     * 
     * @param guest
     */
    public void addGuest(String guest) {
        this.parent.addGuest(guest);
    }

    /**
     * Remove a guest from the guestlist
     * 
     * @param guest
     */
    public void removeGuest(String guest) {
        this.parent.removeGuest(guest);
    }

    /**
     * Check if someone is on the guestlist
     * 
     * @param guest
     * @return <b>true</b> if the name is found, otherwise <b>false</b>
     */
    public boolean isGuest(String guest) {
        return this.parent.isGuest(guest);
    }

    /**
     * Get the ID of this protection
     * 
     * @return the unique ID
     */
    public int getID() {
        return this.parent.getID();
    }

    /**
     * Get the owner of this protection
     * 
     * @return the owner;
     */
    public String getOwner() {
        return this.parent.getOwner();
    }

    /**
     * Check if the given name is the owner of this subprotection
     * 
     * @param otherName
     * @return <b>true</b> if the name is the owner, otherwise <b>false</b>
     */
    public boolean isOwner(String otherName) {
        return this.getOwner().equalsIgnoreCase(otherName);
    }

    /**
     * Is this protection public?
     * 
     * @return <b>true</b> if it is public, otherwise <b>false</b>
     */
    public boolean isPublic() {
        return this.parent.isPublic();
    }

    /**
     * Is this protection private?
     * 
     * @return <b>true</b> if it is private, otherwise <b>false</b>
     */
    public boolean isPrivate() {
        return this.parent.isPrivate();
    }

    /**
     * Check if a SubProtection equals this subProtection
     * 
     * @param subProtection
     * @return
     */
    public boolean equals(SubProtection subProtection) {
        return this.getID() == subProtection.getID() && this.vector.equals(subProtection.getVector());
    }

    /**
     * Check if the player can edit this SubProtection
     * 
     * @param player
     * @return <b>true</b> if the player can, otherwise <b>false</b>
     */
    public boolean canEdit(Player player) {
        return this.parent.canEdit(player);
    }

    /**
     * Check if the player can access this SubProtection
     * 
     * @param player
     * @return <b>true</b> if the player can, otherwise <b>false</b>
     */
    public boolean canAccess(Player player) {
        return this.parent.canAccess(player);
    }

    @Override
    public String toString() {
        return "SubProtection={ " + this.vector.toString() + " ; " + this.parent.toString() + " }";
    }
}
