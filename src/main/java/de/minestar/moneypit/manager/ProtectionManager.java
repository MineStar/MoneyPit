package de.minestar.moneypit.manager;

import java.util.HashMap;

import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.Protection;
import de.minestar.moneypit.data.ProtectionHolder;

public class ProtectionManager {

    private HashMap<BlockVector, ProtectionHolder> protectedBlocks;

    /**
     * Initialize the manager
     */
    public void init() {
        this.protectedBlocks = new HashMap<BlockVector, ProtectionHolder>();
        // TODO: load protections
    }

    // ////////////////////////////////////////////////////////////////
    //
    // Protection
    //
    // ////////////////////////////////////////////////////////////////

    /**
     * Add a protection
     * 
     * @param vector
     * @param protection
     */
    public void addProtection(BlockVector vector, Protection protection) {
        // try to get the ProtectionHolder
        ProtectionHolder holder = this.getProtectionHolder(vector);

        // create the ProtectionHolder, if there is none
        if (holder == null) {
            holder = new ProtectionHolder();
            this.addProtectionHolder(vector, holder);
        }

        // add the Protection the the ProtectionHolder
        holder.addProtection(protection);
    }

    /**
     * Get a specific protection
     * 
     * @param vector
     * @param ID
     * @return the protection
     */
    public Protection getProtection(BlockVector vector, int ID) {
        ProtectionHolder holder = this.getProtectionHolder(vector);
        if (holder != null) {
            return holder.getProtection(ID);
        }
        return null;
    }

    /**
     * Check if we have a specific protection
     * 
     * @param vector
     * @param ID
     * @return <b>true</b> if we have the protection, otherwise <b>false</b>
     */
    public boolean hasProtection(BlockVector vector, int ID) {
        return (this.getProtection(vector, ID) != null);
    }

    /**
     * Remove a protection
     * 
     * @param vector
     * @param protection
     */
    public void removeProtection(BlockVector vector, Protection protection) {
        ProtectionHolder holder = this.getProtectionHolder(vector);
        if (holder != null) {
            // remove the Protection
            holder.removeProtection(protection);

            // remove the ProtectionHolder, if the size is < 1
            if (holder.getSize() < 1) {
                this.removeProtectionHolder(vector);
            }
        }
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
    public boolean hasProtection(BlockVector vector) {
        return (this.getProtectionHolder(vector) != null);
    }

    /**
     * Add a ProtectionHolder to a block
     * 
     * @param vector
     */
    private void addProtectionHolder(BlockVector vector, ProtectionHolder holder) {
        if (!this.hasProtection(vector)) {
            this.protectedBlocks.put(vector, holder);
        }
    }

    /**
     * Get the ProtectionHolder from a block
     * 
     * @param vector
     * @return
     */
    private ProtectionHolder getProtectionHolder(BlockVector vector) {
        return this.protectedBlocks.get(vector);
    }

    /**
     * Remove the ProtectionHolder from a block
     * 
     * @param vector
     */
    private void removeProtectionHolder(BlockVector vector) {
        if (this.hasProtection(vector)) {
            this.protectedBlocks.remove(vector);
        }
    }
}
