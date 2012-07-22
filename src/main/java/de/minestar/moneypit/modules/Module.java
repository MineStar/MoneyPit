package de.minestar.moneypit.modules;

import de.minestar.moneypit.Core;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.ProtectionType;
import de.minestar.moneypit.manager.ProtectionManager;

public abstract class Module {

    private ProtectionManager protectionManager;
    private String moduleName = "UNKNOWN";
    private int registeredTypeID = -1;

    protected Module() {
        this.protectionManager = Core.protectionManager;
    }

    public int getRegisteredTypeID() {
        return this.registeredTypeID;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    protected ProtectionManager getProtectionManager() {
        return this.protectionManager;
    }

    protected void init(int registeredTypeID, String moduleName) {
        this.registeredTypeID = registeredTypeID;
        this.moduleName = moduleName;
    }

    public void removeProtection(BlockVector vector) {
        getProtectionManager().removeProtection(vector);
    }

    public abstract void addProtection(int ID, BlockVector vector, String owner, ProtectionType type, byte subData);
}
