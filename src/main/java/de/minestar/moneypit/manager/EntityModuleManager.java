package de.minestar.moneypit.manager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.entitymodules.EntityModule;
import de.minestar.moneypit.entitymodules.EntityModule_ArmorStand;
import de.minestar.moneypit.entitymodules.EntityModule_Horse;

public class EntityModuleManager {

    private static String FILENAME = "entity_modules.yml";

    private HashMap<EntityType, EntityModule> registeredModules = new HashMap<EntityType, EntityModule>();

    public void init() {
        this.loadConfig();
        this.printInfo();
    }

    private void printInfo() {
        // PRINT INFO
        ConsoleUtils.printInfo(MoneyPitCore.NAME, this.registeredModules.size() + " EntityModules registered!");
    }

    public void registerModule(EntityModule module) {
        // ONLY REGISTER IF NOT ALREADY REGISTERED
        if (!this.isModuleRegistered(module.getEntityType())) {
            this.registeredModules.put(module.getEntityType(), module);
        }
    }

    public boolean isModuleRegistered(EntityType entityType) {
        return this.registeredModules.containsKey(entityType);
    }

    public EntityModule getRegisteredModule(EntityType entityType) {
        return this.registeredModules.get(entityType);
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

            // INIT MODULES
            this.initModules(true, ymlFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initModules(boolean withInstance, YamlConfiguration ymlFile) {
        // @formatter:off
        if (withInstance) {
            // LOAD MODULES 
            new EntityModule_ArmorStand(ymlFile, true);  
            new EntityModule_Horse(ymlFile, true);                 
        } else {
            // CREATE DEFAULT CONFIGS
            new EntityModule_ArmorStand(ymlFile, false);
            new EntityModule_Horse(ymlFile, false);       
        }
        // @formatter:on
    }

    private void writeDefaultConfig() {
        File file = new File(MoneyPitCore.INSTANCE.getDataFolder(), FILENAME);
        if (file.exists()) {
            file.delete();
        }

        try {
            // CREATE
            YamlConfiguration ymlFile = new YamlConfiguration();

            // write default configs for modules
            this.initModules(false, ymlFile);

            // SAVE
            ymlFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
