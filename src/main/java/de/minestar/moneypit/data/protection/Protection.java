package de.minestar.moneypit.data.protection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.entity.Player;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;
import com.bukkit.gemo.patchworking.ISubProtection;
import com.bukkit.gemo.patchworking.ProtectionType;
import com.bukkit.gemo.utils.UtilPermissions;

public class Protection implements IProtection {
    private final int ID;
    private final BlockVector vector;
    private String owner;
    private ProtectionType type;
    private HashSet<String> guestList;
    private HashMap<BlockVector, ISubProtection> subProtections;

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
        this.guestList = null;
        this.subProtections = null;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#getID()
     */
    public int getID() {
        return ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#getVector()
     */
    public BlockVector getVector() {
        return this.vector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#getModuleID()
     */
    public int getModuleID() {
        return this.vector.getLocation().getBlock().getTypeId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#getOwner()
     */
    public String getOwner() {
        return owner;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#isPublic()
     */
    public boolean isPublic() {
        return this.type == ProtectionType.PUBLIC;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#isGift()
     */
    public boolean isGift() {
        return this.type == ProtectionType.GIFT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#isPrivate()
     */
    public boolean isPrivate() {
        return this.type == ProtectionType.PRIVATE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#getType()
     */
    public ProtectionType getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#addGuest(java.lang.String)
     */
    public void addGuest(String guest) {
        if (this.guestList == null) {
            this.guestList = new HashSet<String>();
        }
        this.guestList.add(guest.toLowerCase());
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#removeGuest(java.lang.String)
     */
    public void removeGuest(String guest) {
        if (this.guestList != null) {
            this.guestList.remove(guest.toLowerCase());
        }
        if (this.guestList.size() < 1) {
            this.guestList = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#getGuestList()
     */
    public HashSet<String> getGuestList() {
        return this.guestList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#setGuestList(java.util.HashSet)
     */
    public void setGuestList(HashSet<String> guestList) {
        this.guestList = guestList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#clearGuestList()
     */
    public void clearGuestList() {
        if (this.guestList != null) {
            this.guestList.clear();
            this.guestList = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#isGuest(java.lang.String)
     */
    public boolean isGuest(String guest) {
        if (this.guestList != null) {
            return this.guestList.contains(guest.toLowerCase());
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#isOwner(java.lang.String)
     */
    public boolean isOwner(String otherName) {
        return this.owner.equalsIgnoreCase(otherName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#getSubProtections()
     */
    public Collection<ISubProtection> getSubProtections() {
        if (this.subProtections != null) {
            return this.subProtections.values();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#hasAnySubProtection()
     */
    public boolean hasAnySubProtection() {
        if (this.subProtections != null) {
            return (this.subProtections.size() > 0);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#addSubProtection(de.minestar.moneypit.data.subprotection.SubProtection)
     */
    public void addSubProtection(ISubProtection subProtection) {
        if (subProtection.getVector().equals(this.vector)) {
            return;
        }

        if (this.subProtections == null) {
            this.subProtections = new HashMap<BlockVector, ISubProtection>();
        }

        if (!this.hasSubProtection(subProtection.getVector())) {
            this.subProtections.put(subProtection.getVector(), subProtection);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#removeSubProtection(de.minestar.moneypit.data.BlockVector)
     */
    public void removeSubProtection(BlockVector vector) {
        if (this.subProtections != null) {
            this.subProtections.remove(vector);
        }

        if (!this.hasAnySubProtection()) {
            this.subProtections = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#hasSubProtection(de.minestar.moneypit.data.BlockVector)
     */
    public boolean hasSubProtection(BlockVector vector) {
        if (this.subProtections != null) {
            return this.subProtections.containsKey(vector);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#canEdit(org.bukkit.entity.Player)
     */
    public boolean canEdit(Player player) {
        return this.isOwner(player.getName()) || UtilPermissions.playerCanUseCommand(player, "moneypit.admin");
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#canAccess(org.bukkit.entity.Player)
     */
    public boolean canAccess(Player player) {
        return this.isPublic() || this.isGift() || this.isOwner(player.getName()) || this.isGuest(player.getName()) || this.canEdit(player);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#canAccess(java.lang.String)
     */
    public boolean canAccess(String playerName) {
        return this.isPublic() || this.isGift() || this.isOwner(playerName) || this.isGuest(playerName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#equals(de.minestar.moneypit.data.protection.Protection)
     */
    public boolean equals(IProtection protection) {
        return this.ID == protection.getID() && this.vector.equals(protection.getVector());
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtection#toString()
     */
    @Override
    public String toString() {
        return "Protection={ ID:" + this.ID + " ; Type:" + this.type.name() + " ; Owner:" + this.owner + " ; " + this.vector.toString() + " }";
    }
}
