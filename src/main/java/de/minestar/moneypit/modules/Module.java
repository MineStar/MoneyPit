package de.minestar.moneypit.modules;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.BlockPlaceEvent;

import de.minestar.moneypit.Core;
import de.minestar.moneypit.data.BlockVector;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.manager.ProtectionManager;

public abstract class Module {

    private ProtectionManager protectionManager;
    private String moduleName = "UNKNOWN";
    private int registeredTypeID = -1;
    private boolean autoLock = false;
    private boolean doNeighbourCheck = false;
    private boolean blockRedstone = false;

    private static final Set<Integer> nonSolidStateBlocks = new HashSet<Integer>(Arrays.asList(6, 8, 9, 10, 11, 12, 13, 18, 26, 27, 28, 31, 32, 34, 37, 38, 39, 40, 46, 50, 51, 55, 59, 63, 64, 65, 66, 68, 69, 70, 71, 72, 75, 76, 77, 78, 79, 81, 83, 93, 94, 96, 103, 104, 105, 106, 111, 115, 127, 131, 132));

    protected Module() {
        this.protectionManager = Core.protectionManager;
    }

    protected boolean isBlockNonSolid(int ID) {
        return nonSolidStateBlocks.contains(ID);
    }

    public boolean doNeighbourCheck() {
        return doNeighbourCheck;
    }

    public boolean blockRedstone() {
        return blockRedstone;
    }

    public void setDoNeighbourCheck(boolean doNeighbourCheck) {
        this.doNeighbourCheck = doNeighbourCheck;
    }

    public void setBlockRedstone(boolean blockRedstone) {
        this.blockRedstone = blockRedstone;
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

    protected void writeDefaultConfig(String moduleName, YamlConfiguration ymlFile) {
        ymlFile.set("protect." + moduleName + ".enabled", true);
        ymlFile.set("protect." + moduleName + ".lockOnPlace", false);
        this.writeExtraConfig(moduleName, ymlFile);
    }

    protected void writeExtraConfig(String moduleName, YamlConfiguration ymlFile) {
    }

    public void removeProtection(BlockVector vector) {
        getProtectionManager().removeProtection(vector);
    }

    /**
     * This method is called on a blockplace of a registered module
     * 
     * @param event
     * @param vector
     * @return <b>true</b> if the blockplace-event should abort after the check, <b>false</b> if it should continue
     */
    public boolean onPlace(BlockPlaceEvent event, BlockVector vector) {
        return false;
    }

    public abstract void addProtection(Protection protection, byte subData);
}
