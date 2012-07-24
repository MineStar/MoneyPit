package de.minestar.moneypit.modules;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.moneypit.Core;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.ProtectionType;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.manager.ProtectionManager;

public abstract class Module {

    private ProtectionManager protectionManager;
    private String moduleName = "UNKNOWN";
    private int registeredTypeID = -1;
    private boolean autoLock = false;
    private boolean doNeighbourCheck = false;

    protected Module() {
        this.protectionManager = Core.protectionManager;
    }

    public boolean doNeighbourCheck() {
        return doNeighbourCheck;
    }

    public void setDoNeighbourCheck(boolean doNeighbourCheck) {
        this.doNeighbourCheck = doNeighbourCheck;
    }

    public int getRegisteredTypeID() {
        return this.registeredTypeID;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public boolean isAutoLock() {
        return autoLock;
    }

    protected ProtectionManager getProtectionManager() {
        return this.protectionManager;
    }

    protected void init(ModuleManager moduleManager, YamlConfiguration ymlFile, int registeredTypeID, String moduleName) {
        this.registeredTypeID = registeredTypeID;
        this.moduleName = moduleName;
        boolean isEnabled = ymlFile.getBoolean("protect." + this.getModuleName() + ".enabled", true);
        this.autoLock = ymlFile.getBoolean("protect." + this.getModuleName() + ".lockOnPlace", false);
        if (isEnabled) {
            moduleManager.registerModule(this);
        }
    }

    public void writeDefaultConfig(String moduleName, YamlConfiguration ymlFile) {
        ymlFile.set("protect." + moduleName + ".enabled", true);
        ymlFile.set("protect." + moduleName + ".lockOnPlace", false);
    }

    public void removeProtection(BlockVector vector) {
        getProtectionManager().removeProtection(vector);
    }

    public abstract void onNeighbourPlace(Location neighbourLocation);

    public abstract void addProtection(int ID, BlockVector vector, String owner, ProtectionType type, byte subData);
}
