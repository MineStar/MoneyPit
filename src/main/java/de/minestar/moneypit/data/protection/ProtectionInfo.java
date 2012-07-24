package de.minestar.moneypit.data.protection;

import de.minestar.moneypit.Core;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.manager.ProtectionManager;

public class ProtectionInfo {
    private boolean hasAnyProtection;
    private boolean hasProtection, hasSubProtection;

    private final ProtectionManager protectionManager;

    public ProtectionInfo() {
        this.protectionManager = Core.protectionManager;
    }

    public void update(BlockVector vector) {
        this.hasProtection = this.protectionManager.hasProtection(vector);
        this.hasSubProtection = this.protectionManager.hasSubProtectionHolder(vector);
        this.hasAnyProtection = this.hasProtection || this.hasSubProtection;
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
