package de.minestar.moneypit.manager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.modules.Module;
import de.minestar.moneypit.modules.Module_Chest;
import de.minestar.moneypit.modules.Module_Dispenser;
import de.minestar.moneypit.modules.Module_FenceGate;
import de.minestar.moneypit.modules.Module_Furnace;
import de.minestar.moneypit.modules.Module_FurnaceBurning;
import de.minestar.moneypit.modules.Module_IronDoor;
import de.minestar.moneypit.modules.Module_Jukebox;
import de.minestar.moneypit.modules.Module_Lever;
import de.minestar.moneypit.modules.Module_SignPost;
import de.minestar.moneypit.modules.Module_StoneButton;
import de.minestar.moneypit.modules.Module_StonePlate;
import de.minestar.moneypit.modules.Module_TrapDoor;
import de.minestar.moneypit.modules.Module_WallSign;
import de.minestar.moneypit.modules.Module_WoodenDoor;

public class ModuleManager {

    private static String FILENAME = "modules.yml";

    private HashMap<Integer, Module> registeredModules = new HashMap<Integer, Module>();

    public void init() {
        this.loadConfig();
        this.printInfo();
    }

    private void printInfo() {
        // PRINT INFO
        ConsoleUtils.printInfo(MoneyPitCore.NAME, this.registeredModules.size() + " Modules registered!");
    }

    public void registerModule(Module module) {
        // ONLY REGISTER IF NOT ALREADY REGISTERED
        if (!this.isModuleRegistered(module.getRegisteredTypeID())) {
            this.registeredModules.put(module.getRegisteredTypeID(), module);
        }
    }

    public boolean isModuleRegistered(int TypeId) {
        return this.registeredModules.containsKey(TypeId);
    }

    public Module getRegisteredModule(int TypeId) {
        return this.registeredModules.get(TypeId);
    }

    private void loadConfig() {
        File file = new File(MoneyPitCore.INSTANCE.getDataFolder(), FILENAME);
        if (!file.exists()) {
            this.writeDefaultConfig();
        }

        try {
            // CREATE
            YamlConfiguration ymlFile = new YamlConfiguration();

            // LOAD
            ymlFile.load(file);

            // @formatter:off
            
            // INIT MODULES
            new Module_Chest            (this, ymlFile);
            new Module_SignPost         (this, ymlFile);
            new Module_WallSign         (this, ymlFile);
            new Module_WoodenDoor       (this, ymlFile);
            new Module_IronDoor         (this, ymlFile);
            new Module_TrapDoor         (this, ymlFile);
            new Module_StoneButton      (this, ymlFile);
            new Module_Lever            (this, ymlFile);
            new Module_FenceGate        (this, ymlFile);
            new Module_StonePlate       (this, ymlFile);
            new Module_Dispenser        (this, ymlFile);
            new Module_Furnace          (this, ymlFile); 
            new Module_FurnaceBurning   (this, ymlFile);
            new Module_Jukebox          (this, ymlFile);
            
            // @formatter:on

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeDefaultConfig() {
        File file = new File(MoneyPitCore.INSTANCE.getDataFolder(), FILENAME);
        if (file.exists()) {
            file.delete();
        }

        try {
            // CREATE
            YamlConfiguration ymlFile = new YamlConfiguration();

            // @formatter:off
            
            // SAVE MODULES
            new Module_Chest            (ymlFile);
            new Module_SignPost         (ymlFile);
            new Module_WallSign         (ymlFile);
            new Module_WoodenDoor       (ymlFile);
            new Module_IronDoor         (ymlFile);
            new Module_TrapDoor         (ymlFile);
            new Module_StoneButton      (ymlFile);
            new Module_Lever            (ymlFile);
            new Module_FenceGate        (ymlFile);            
            new Module_StonePlate       (ymlFile);
            new Module_Dispenser        (ymlFile);
            new Module_Furnace          (ymlFile); 
            new Module_FurnaceBurning   (ymlFile);
            new Module_Jukebox          (ymlFile);
            
            // @formatter:on

            // SAVE
            ymlFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
