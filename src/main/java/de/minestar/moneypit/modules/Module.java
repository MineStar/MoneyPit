package de.minestar.moneypit.modules;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.BlockPlaceEvent;

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
    private boolean handleRedstone = false;

    protected Module() {
        this.protectionManager = Core.protectionManager;
    }

    public boolean doNeighbourCheck() {
        return doNeighbourCheck;
    }

    public boolean handleRedstone() {
        return handleRedstone;
    }

    public void setDoNeighbourCheck(boolean doNeighbourCheck) {
        this.doNeighbourCheck = doNeighbourCheck;
    }

    public void setHandleRedstone(boolean handleRedstone) {
        this.handleRedstone = handleRedstone;
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

    /**
     * This method is called on a blockplace of a registered module
     * 
     * @param event
     * @param vector
     * @return <b>true</b> if the blockplace-event should abort after the check,
     *         <b>false</b> if it should continue
     */
    public boolean onPlace(BlockPlaceEvent event, BlockVector vector) {
        return false;
    }

    public abstract void addProtection(int ID, BlockVector vector, String owner, ProtectionType type, byte subData);
}
