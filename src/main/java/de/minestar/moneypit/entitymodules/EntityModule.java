package de.minestar.moneypit.entitymodules;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;

import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.EventResult;
import de.minestar.moneypit.manager.EntityModuleManager;
import de.minestar.moneypit.manager.ProtectionManager;

public abstract class EntityModule {

    private ProtectionManager protectionManager;
    private EntityType entityType = null;
    private boolean autoLock = false;
    private boolean doNeighbourCheck = false;
    private boolean blockRedstone = false;

    protected EntityModule() {
        this.protectionManager = MoneyPitCore.protectionManager;
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

    public EntityType getEntityType() {
        return entityType;
    }

    public boolean isAutoLock() {
        return autoLock;
    }

    protected void setAutolock(boolean autoLock) {
        this.autoLock = autoLock;
    }

    protected ProtectionManager getProtectionManager() {
        return this.protectionManager;
    }

    protected void init(EntityModuleManager entityModuleManager, YamlConfiguration ymlFile, EntityType entityType) {
        this.entityType = entityType;
        boolean isEnabled = ymlFile.getBoolean("protect." + this.entityType + ".enabled", true);
        this.autoLock = ymlFile.getBoolean("protect." + this.entityType + ".lockOnPlace", false);
        if (isEnabled) {
            entityModuleManager.registerModule(this);
        }
    }

    protected void writeDefaultConfig(YamlConfiguration ymlFile) {
        ymlFile.set("protect." + entityType + ".enabled", true);
        ymlFile.set("protect." + entityType + ".lockOnPlace", false);
        this.writeExtraConfig(ymlFile);
    }

    protected void writeExtraConfig(YamlConfiguration ymlFile) {
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
    public EventResult onPlace(Player player, BlockVector vector) {
        return new EventResult(false, false, null);
    }

    public abstract boolean addProtection(IProtection protection, byte subData, boolean saveToDatabase);
}
