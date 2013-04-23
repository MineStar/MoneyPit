package de.minestar.moneypit.data.protection;

import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.subprotection.SubProtectionHolder;
import de.minestar.moneypit.manager.ProtectionManager;

public class ProtectionInfo {
    private boolean hasProtection, hasSubProtection, hasAnyProtection;
    private Protection protection;
    private SubProtectionHolder subProtections;

    private final ProtectionManager protectionManager;

    public ProtectionInfo() {
        this(false, false, false, null, null);
    }

    private ProtectionInfo(boolean hasProtection, boolean hasSubProtection, boolean hasAnyProtection, Protection protection, SubProtectionHolder subProtections) {
        this.protectionManager = MoneyPitCore.protectionManager;
        this.hasProtection = hasProtection;
        this.hasSubProtection = hasSubProtection;
        this.hasAnyProtection = hasAnyProtection;
        this.protection = protection;
        this.subProtections = subProtections;
    }

    public ProtectionInfo clone() {
        return new ProtectionInfo(hasProtection, hasSubProtection, hasAnyProtection, protection, subProtections);
    }

    public void update(BlockVector vector) {
        this.protection = this.protectionManager.getProtection(vector);
        this.subProtections = this.protectionManager.getSubProtectionHolder(vector);
        this.hasProtection = (this.protection != null);
        this.hasSubProtection = (this.subProtections != null);
        this.hasAnyProtection = this.hasProtection || this.hasSubProtection;
    }

    /**
     * @return the protection
     */
    public Protection getProtection() {
        return protection;
    }

    /**
     * @return the subProtections
     */
    public SubProtectionHolder getSubProtections() {
        return subProtections;
    }

    /**
     * @return the First Protection
     */
    public Protection getFirstProtection() {
        if (this.hasSubProtection) {
            return this.subProtections.getProtection(0).getParent();
        }
        return null;
    }

    /**
     * @return the hasAnyProtection
     */
    public boolean hasAnyProtection() {
        return hasAnyProtection;
    }

    /**
     * @return the hasProtection
     */
    public boolean hasProtection() {
        return hasProtection;
    }

    /**
     * @return the hasSubProtection
     */
    public boolean hasSubProtection() {
        return hasSubProtection;
    }
}
