package de.minestar.moneypit.data.protection;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;
import com.bukkit.gemo.patchworking.IProtectionInfo;
import com.bukkit.gemo.patchworking.ISubProtectionHolder;

import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.manager.ProtectionManager;

public class ProtectionInfo implements IProtectionInfo {
    private boolean hasProtection, hasSubProtection, hasAnyProtection;
    private IProtection protection;
    private ISubProtectionHolder subProtections;

    private final ProtectionManager protectionManager;
    private BlockVector origin = new BlockVector("", 0, 0, 0);

    public ProtectionInfo() {
        this(false, false, false, null, null);
    }

    private ProtectionInfo(boolean hasProtection, boolean hasSubProtection, boolean hasAnyProtection, IProtection protection, ISubProtectionHolder subProtections) {
        this.protectionManager = MoneyPitCore.protectionManager;
        this.hasProtection = hasProtection;
        this.hasSubProtection = hasSubProtection;
        this.hasAnyProtection = hasAnyProtection;
        this.protection = protection;
        this.subProtections = subProtections;
    }

    public BlockVector getOrigin() {
        return origin;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtectionInfo#clone()
     */
    public IProtectionInfo clone() {
        return new ProtectionInfo(hasProtection, hasSubProtection, hasAnyProtection, protection, subProtections);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtectionInfo#update(de.minestar.moneypit.data.BlockVector)
     */
    public void update(BlockVector vector) {
        this.origin.update(vector.getLocation());
        this.protection = this.protectionManager.getProtection(vector);
        this.subProtections = this.protectionManager.getSubProtectionHolder(vector);
        this.hasProtection = (this.protection != null);
        this.hasSubProtection = (this.subProtections != null);
        this.hasAnyProtection = this.hasProtection || this.hasSubProtection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtectionInfo#getProtection()
     */
    public IProtection getProtection() {
        return protection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtectionInfo#getSubProtections()
     */
    public ISubProtectionHolder getSubProtections() {
        return subProtections;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtectionInfo#getFirstProtection()
     */
    public IProtection getFirstProtection() {
        if (this.hasSubProtection) {
            return this.subProtections.getProtection(0).getParent();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtectionInfo#hasAnyProtection()
     */
    public boolean hasAnyProtection() {
        return hasAnyProtection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtectionInfo#hasProtection()
     */
    public boolean hasProtection() {
        return hasProtection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.protection.IProtectionInfo#hasSubProtection()
     */
    public boolean hasSubProtection() {
        return hasSubProtection;
    }
}
