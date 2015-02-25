package de.minestar.moneypit.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;
import com.bukkit.gemo.patchworking.ISubProtection;
import com.bukkit.gemo.patchworking.ISubProtectionHolder;
import com.bukkit.gemo.patchworking.ProtectionType;

import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.subprotection.SubProtectionHolder;

public class ProtectionManager {

    private HashSet<String> giftList;
    private HashMap<BlockVector, IProtection> protections;
    private HashMap<BlockVector, ISubProtectionHolder> subProtections;

    /**
     * Initialize the manager
     */
    public void init() {
        this.protections = new HashMap<BlockVector, IProtection>(512);
        this.subProtections = new HashMap<BlockVector, ISubProtectionHolder>(1024);
        this.giftList = new HashSet<String>(128);
    }

    // ////////////////////////////////////////////////////////////////
    //
    // Protection
    //
    // ////////////////////////////////////////////////////////////////

    /**
     * Check if the given BlockVector has any protection
     * 
     * @param vector
     * @return <b>true</b> if the block is protected, otherwise <b>false</b>
     */
    public boolean hasAnyProtection(BlockVector vector) {
        return (this.hasProtection(vector) || this.hasSubProtectionHolder(vector));
    }

    /**
     * Get the Protection of a block
     * 
     * @param vector
     * @return the Protection
     */
    public IProtection getProtection(BlockVector vector) {
        return this.protections.get(vector);
    }

    /**
     * Add a protection
     * 
     * @param protection
     */
    public boolean addProtection(Protection protection) {
        // is it a gift-protection?
        if (protection.getType().equals(ProtectionType.GIFT)) {
            if (this.hasGiftProtection(protection.getOwner())) {
                return false;
            }

            this.giftList.add(protection.getOwner().toLowerCase());
        }

        // register the protection
        this.protections.put(protection.getVector(), protection);

        // register the subprotections
        if (protection.hasAnySubProtection()) {
            Collection<ISubProtection> subs = protection.getSubProtections();
            for (ISubProtection sub : subs) {
                this.addSubProtection(sub);
            }
        }
        return true;
    }

    /**
     * Add a SubProtection
     * 
     * @param subProtection
     */
    public void addSubProtection(ISubProtection subProtection) {
        ISubProtectionHolder holder = this.getSubProtectionHolder(subProtection.getVector());
        if (holder == null) {
            holder = new SubProtectionHolder();
            this.addSubProtectionHolder(subProtection.getVector(), holder);
        }
        holder.addProtection(subProtection);
    }

    /**
     * Remove a Protection from a block
     * 
     * @param vector
     */
    public void removeProtection(BlockVector vector) {
        IProtection protection = this.protections.get(vector);
        if (protection != null) {
            // check if it is a giftprotection
            if (protection.isGift()) {
                this.removeGiftProtection(protection.getOwner());
            }

            // remove the protection
            this.protections.remove(vector);
            // remove the subprotections
            if (protection.hasAnySubProtection()) {
                Collection<ISubProtection> subs = protection.getSubProtections();
                for (ISubProtection sub : subs) {
                    this.removeSubProtection(sub);
                }
            }
        }
    }

    /**
     * Remove a SubProtection from a block
     * 
     * @param subProtection
     */
    private void removeSubProtection(ISubProtection subProtection) {
        ISubProtectionHolder holder = this.getSubProtectionHolder(subProtection.getVector());
        if (holder != null) {
            holder.removeProtection(subProtection);
        }

        if (holder.getSize() < 1) {
            this.removeProtectionHolder(subProtection.getVector());
        }
    }

    /**
     * Check if the given BlockVector has a Protection
     * 
     * @param vector
     * @return <b>true</b> if the block is protected, otherwise <b>false</b>
     */
    public boolean hasProtection(BlockVector vector) {
        return this.protections.containsKey(vector);
    }

    // ////////////////////////////////////////////////////////////////
    //
    // ProtectionHolder
    //
    // ////////////////////////////////////////////////////////////////

    /**
     * Check if a block has a ProtectionHolder
     * 
     * @param vector
     * @return <b>true</b> if the block is protected, otherwise <b>false</b>
     */
    public boolean hasSubProtectionHolder(BlockVector vector) {
        return this.subProtections.containsKey(vector);
    }

    /**
     * Add a ProtectionHolder to a block
     * 
     * @param vector
     * @param holder
     */
    private void addSubProtectionHolder(BlockVector vector, ISubProtectionHolder holder) {
        if (!this.hasSubProtectionHolder(vector)) {
            this.subProtections.put(vector, holder);
        }
    }

    /**
     * Get the SubProtectionHolder from a block
     * 
     * @param vector
     * @return the SubProtectionHolder
     */
    public ISubProtectionHolder getSubProtectionHolder(BlockVector vector) {
        return this.subProtections.get(vector);
    }

    /**
     * Remove the ProtectionHolder from a block
     * 
     * @param vector
     */
    private void removeProtectionHolder(BlockVector vector) {
        if (this.hasSubProtectionHolder(vector)) {
            this.subProtections.remove(vector);
        }
    }

    // ////////////////////////////////////////////////////////////////
    //
    // Gifts
    //
    // ////////////////////////////////////////////////////////////////

    /**
     * Check if a player has a GiftProtection
     * 
     * @param playerName
     * @return <b>true</b> if the player has one, otherwise <b>false</b>
     */
    public boolean hasGiftProtection(String playerName) {
        return this.giftList.contains(playerName.toLowerCase());
    }

    /**
     * Remove the GiftProtection for a player
     * 
     * @param playerName
     */
    private void removeGiftProtection(String playerName) {
        if (this.hasGiftProtection(playerName)) {
            this.giftList.remove(playerName.toLowerCase());
        }
    }

    public void transferProtections(String oldPlayername, String newPlayername) {
        // update gift-protection
        if (this.giftList.contains(oldPlayername.toLowerCase())) {
            this.removeGiftProtection(oldPlayername);
            this.giftList.add(newPlayername.toLowerCase());
        }

        // update protections
        for (IProtection iProtect : this.protections.values()) {
            if (iProtect.isOwner(oldPlayername)) {
                Protection protection = (Protection) iProtect;
                protection.setOwner(newPlayername);
            }
        }
    }
}
