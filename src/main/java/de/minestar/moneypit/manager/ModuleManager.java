package de.minestar.moneypit.manager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.moneypit.Core;
import de.minestar.moneypit.modules.Module;
import de.minestar.moneypit.modules.Module_Chest;
import de.minestar.moneypit.modules.Module_SignPost;
import de.minestar.moneypit.modules.Module_WallSign;

public class ModuleManager {

    private static String FILENAME = "modules.yml";

    private boolean protectChests = true, protectLevers = true, protectButtons = true, protectFenceGates = true;
    private boolean protectSigns = true, protectWoodDoors = true, protectIronDoors = true, protectTrapDoors = true;

    private boolean autoLockChests = true, autoLockLevers = true, autoLockButtons = true, autoLockFenceGates = true;
    private boolean autoLockSigns = true, autoLockWoodDoors = true, autoLockIronDoors = true, autoLockTrapDoors = true;

    private HashMap<Integer, Module> registeredModules = new HashMap<Integer, Module>();

    public void init() {
        this.loadConfig();
        this.registerModules();
    }

    private void registerModules() {
        // REGISTER MODULES
        this.registerModule(new Module_Chest(this.autoLockChests), this.protectChests);
        this.registerModule(new Module_SignPost(this.autoLockSigns), this.protectSigns);
        this.registerModule(new Module_WallSign(this.autoLockSigns), this.protectSigns);

        // PRINT INFO
        ConsoleUtils.printInfo(Core.NAME, this.registeredModules.size() + " Modules registered!");
    }

    private void registerModule(Module module, boolean isEnabled) {
        // ONLY REGISTER, IF ENABLED AND NOT ALREADY REGISTERED
        if (isEnabled && !this.isModuleRegistered(module.getRegisteredTypeID())) {
            this.registeredModules.put(module.getRegisteredTypeID(), module);
        }
    }

    public boolean isModuleRegistered(int TypeId) {
        return this.registeredModules.containsKey(TypeId);
    }

    public Module getModule(int TypeId) {
        return this.registeredModules.get(TypeId);
    }

    private void loadConfig() {
        File file = new File(Core.INSTANCE.getDataFolder(), FILENAME);
        if (!file.exists()) {
            this.writeDefaultConfig();
        }

        try {
            // CREATE
            YamlConfiguration ymlFile = new YamlConfiguration();

            // LOAD
            ymlFile.load(file);

            // @formatter:off
            // PROTECTIONS
            protectChests       = ymlFile.getBoolean("protect.chests",          protectChests);
            protectSigns        = ymlFile.getBoolean("protect.signs",           protectSigns);
            protectLevers       = ymlFile.getBoolean("protect.levers",          protectLevers);
            protectButtons      = ymlFile.getBoolean("protect.buttons",         protectButtons);
            protectWoodDoors    = ymlFile.getBoolean("protect.doors.wood",      protectWoodDoors);
            protectIronDoors    = ymlFile.getBoolean("protect.doors.iron",      protectIronDoors);
            protectTrapDoors    = ymlFile.getBoolean("protect.doors.trap",      protectTrapDoors);
            protectFenceGates   = ymlFile.getBoolean("protect.doors.fencegate", protectFenceGates);
            
            // AUTOLOCK
            autoLockChests      = ymlFile.getBoolean("protect.chests.autolock",          autoLockChests);
            autoLockSigns       = ymlFile.getBoolean("protect.signs.autolock",           autoLockSigns);
            autoLockLevers      = ymlFile.getBoolean("protect.levers.autolock",          autoLockLevers);
            autoLockButtons     = ymlFile.getBoolean("protect.buttons.autolock",         autoLockButtons);
            autoLockWoodDoors   = ymlFile.getBoolean("protect.doors.wood.autolock",      autoLockWoodDoors);
            autoLockIronDoors   = ymlFile.getBoolean("protect.doors.iron.autolock",      autoLockIronDoors);
            autoLockTrapDoors   = ymlFile.getBoolean("protect.doors.trap.autolock",      autoLockTrapDoors);
            autoLockFenceGates  = ymlFile.getBoolean("protect.doors.fencegate.autolock", autoLockFenceGates);
            // @formatter:on

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeDefaultConfig() {
        File file = new File(Core.INSTANCE.getDataFolder(), FILENAME);
        if (file.exists()) {
            file.delete();
        }

        try {
            // CREATE
            YamlConfiguration ymlFile = new YamlConfiguration();

            // @formatter:off
            // PROTECTIONS
            ymlFile.set("protect.chests",           protectChests);
            ymlFile.set("protect.signs",            protectSigns);
            ymlFile.set("protect.levers",           protectLevers);
            ymlFile.set("protect.buttons",          protectButtons);
            ymlFile.set("protect.doors.wood",       protectWoodDoors);
            ymlFile.set("protect.doors.iron",       protectIronDoors);
            ymlFile.set("protect.doors.trap",       protectTrapDoors);
            ymlFile.set("protect.doors.fencegate",  protectFenceGates);
            
            // AUTOLOCK
            ymlFile.set("protect.chests.autolock",          autoLockChests);
            ymlFile.set("protect.signs.autolock",           autoLockSigns);
            ymlFile.set("protect.levers.autolock",          autoLockLevers);
            ymlFile.set("protect.buttons.autolock",         autoLockButtons);
            ymlFile.set("protect.doors.wood.autolock",      autoLockWoodDoors);
            ymlFile.set("protect.doors.iron.autolock",      autoLockIronDoors);
            ymlFile.set("protect.doors.trap.autolock",      autoLockTrapDoors);
            ymlFile.set("protect.doors.fencegate.autolock", autoLockFenceGates);
            // @formatter:on

            // SAVE
            ymlFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
