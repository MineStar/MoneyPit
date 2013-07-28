package de.minestar.moneypit.data.subprotection;

import org.bukkit.entity.Player;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;
import com.bukkit.gemo.patchworking.ISubProtection;
import com.bukkit.gemo.patchworking.ProtectionType;

public class SubProtection implements ISubProtection {

    private final BlockVector vector;
    private final IProtection parent;

    /**
     * Constructor
     * 
     * @param parent
     */
    public SubProtection(BlockVector vector, IProtection parent) {
        this.vector = vector;
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#getVector()
     */
    public BlockVector getVector() {
        return this.vector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#getParent()
     */
    public IProtection getParent() {
        return this.parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#getModuleID()
     */
    public int getModuleID() {
        return this.parent.getModuleID();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#getType()
     */
    public ProtectionType getType() {
        return this.parent.getType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#addGuest(java.lang.String)
     */
    public void addGuest(String guest) {
        this.parent.addGuest(guest);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#removeGuest(java.lang.String)
     */
    public void removeGuest(String guest) {
        this.parent.removeGuest(guest);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#isGuest(java.lang.String)
     */
    public boolean isGuest(String guest) {
        return this.parent.isGuest(guest);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#getID()
     */
    public int getID() {
        return this.parent.getID();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#getOwner()
     */
    public String getOwner() {
        return this.parent.getOwner();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#isOwner(java.lang.String)
     */
    public boolean isOwner(String otherName) {
        return this.getOwner().equalsIgnoreCase(otherName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#isPublic()
     */
    public boolean isPublic() {
        return this.parent.isPublic();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#isPrivate()
     */
    public boolean isPrivate() {
        return this.parent.isPrivate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#isGift()
     */
    public boolean isGift() {
        return this.parent.isGift();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#equals(de.minestar.moneypit.data.subprotection.ISubProtection)
     */
    public boolean equals(ISubProtection subProtection) {
        return this.getID() == subProtection.getID() && this.vector.equals(subProtection.getVector());
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#canEdit(org.bukkit.entity.Player)
     */
    public boolean canEdit(Player player) {
        return this.parent.canEdit(player);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#canAccess(org.bukkit.entity.Player)
     */
    public boolean canAccess(Player player) {
        return this.parent.canAccess(player);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#canAccess(java.lang.String)
     */
    public boolean canAccess(String playerName) {
        return this.parent.canAccess(playerName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtection#toString()
     */
    @Override
    public String toString() {
        return "SubProtection={ " + this.vector.toString() + " ; " + this.parent.toString() + " }";
    }
}
