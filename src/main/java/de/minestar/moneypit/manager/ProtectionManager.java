package de.minestar.moneypit.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;
import com.bukkit.gemo.patchworking.ISubProtectionHolder;
import com.bukkit.gemo.patchworking.ProtectionType;

import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.data.subprotection.SubProtectionHolder;
import de.minestar.moneypit.modules.Module;

public class ProtectionManager {

    private HashSet<String> giftList;
    private HashMap<BlockVector, IProtection> protections;
    private HashMap<BlockVector, ISubProtectionHolder> subProtections;
    private HashMap<BlockVector, IProtection> cachedProtections;

    /**
     * Initialize the manager
     */
    public void init() {
        this.protections = new HashMap<BlockVector, IProtection>(512);
        this.subProtections = new HashMap<BlockVector, ISubProtectionHolder>(1024);
        this.cachedProtections = new HashMap<BlockVector, IProtection>(512);
        this.giftList = new HashSet<String>(128);
    }

    public void setCachedProtections(HashMap<Integer, IProtection> cachedProtections) {
        this.cachedProtections = new HashMap<BlockVector, IProtection>(512);
        this.subProtections = new HashMap<BlockVector, ISubProtectionHolder>(1024);
        for (IProtection protection : cachedProtections.values()) {
            if (protection == null) {
                continue;
            }
            this.cachedProtections.put(protection.getVector(), protection);
            // cache subprotections
            if (protection.getSubProtections() != null) {
                for (IProtection subProtection : protection.getSubProtections()) {
                    this.addSubProtection(subProtection);
                }
            }
        }
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
        IProtection cachedProtection = this.cachedProtections.get(vector);
        if (cachedProtection != null) {
            // remove from cache
            this.cachedProtections.remove(vector);

            // check validity
            // this will also add the protection to the real list, if the check succeeds
            if (!this.checkCachedProtection(cachedProtection)) {
                // remove subprotections
                Collection<IProtection> subProtections = cachedProtection.getSubProtections();
                for (IProtection subProtection : subProtections) {
                    this.removeSubProtection(subProtection);
                }

                // return
                return this.protections.get(vector);
            }
        }
        return this.protections.get(vector);
    }

    private boolean checkCachedProtection(IProtection protection) {
        BlockVector vector = protection.getVector();
        Location location = vector.getLocation();
        if (location == null) {
            return false;
        }

        // load chunk
        location.getChunk().load(true);

        // get module
        Module module = MoneyPitCore.moduleManager.getRegisteredModule(location.getBlock().getType());

        Hanging entityHanging = null;
        if (module == null) {
            // search for an itemframe
            Collection<ItemFrame> frames = location.getWorld().getEntitiesByClass(ItemFrame.class);
            boolean found = false;
            for (ItemFrame frame : frames) {
                BlockVector otherVector = new BlockVector(frame.getLocation());
                if (vector.equals(otherVector)) {
                    module = MoneyPitCore.moduleManager.getRegisteredModule(Material.ITEM_FRAME);
                    entityHanging = frame;
                    found = true;
                    break;
                }
            }
            if (!found) {
                // search for a painting
                Collection<Painting> paintings = location.getWorld().getEntitiesByClass(Painting.class);
                for (Painting paint : paintings) {
                    BlockVector otherVector = new BlockVector(paint.getLocation());
                    if (vector.equals(otherVector)) {
                        module = MoneyPitCore.moduleManager.getRegisteredModule(Material.PAINTING);
                        entityHanging = paint;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }

        byte subData = location.getBlock().getData();
        if (module.getRegisteredType() == Material.ITEM_FRAME || module.getRegisteredType() == Material.PAINTING) {
            if (entityHanging != null) {
                subData = (byte) entityHanging.getAttachedFace().ordinal();
            } else {
                return false;
            }
        }
        return module.addProtection(protection, subData, false);
    }

    /**
     * Add a protection
     * 
     * @param protection
     */
    public boolean addProtection(IProtection protection) {
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
            Collection<IProtection> subs = protection.getSubProtections();
            for (IProtection sub : subs) {
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
    public void addSubProtection(IProtection subProtection) {
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
        IProtection protection = this.getProtection(vector);
        if (protection != null) {
            // check if it is a giftprotection
            if (protection.isGift()) {
                this.removeGiftProtection(protection.getOwner());
            }

            // remove the protection
            this.protections.remove(vector);
            // remove the subprotections
            if (protection.hasAnySubProtection()) {
                Collection<IProtection> subs = protection.getSubProtections();
                for (IProtection sub : subs) {
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
    private void removeSubProtection(IProtection subProtection) {
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
        return this.getProtection(vector) != null;
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
