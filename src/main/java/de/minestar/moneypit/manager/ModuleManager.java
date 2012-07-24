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

    private HashMap<Integer, Module> registeredModules = new HashMap<Integer, Module>();

    public void init() {
        this.loadConfig();
        this.printInfo();
    }

    private void printInfo() {
        // PRINT INFO
        ConsoleUtils.printInfo(Core.NAME, this.registeredModules.size() + " Modules registered!");
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
            
            // INIT MODULES
            new Module_Chest    (this, ymlFile);
            new Module_SignPost (this, ymlFile);
            new Module_WallSign (this, ymlFile);
            
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
            
            // SAVE MODULES
            new Module_Chest    (ymlFile);
            new Module_SignPost (ymlFile);
            new Module_WallSign (ymlFile);
            
            // @formatter:on

            // SAVE
            ymlFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
