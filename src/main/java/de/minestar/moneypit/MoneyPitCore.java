/*
 * Copyright (C) 2011 MineStar.de 
 * 
 * This file is part of TheRock.
 * 
 * TheRock is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * TheRock is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with TheRock.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.moneypit;

import java.io.File;
import java.util.Timer;

import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;
import com.bukkit.gemo.patchworking.IProtectionCore;
import com.bukkit.gemo.patchworking.IProtectionInfo;
import com.bukkit.gemo.patchworking.ProtectionType;

import de.minestar.minestarlibrary.AbstractCore;
import de.minestar.minestarlibrary.commands.CommandList;
import de.minestar.moneypit.autoclose.AutoCloseBackgroundTask;
import de.minestar.moneypit.commands.cmd_cgift;
import de.minestar.moneypit.commands.cmd_cinfo;
import de.minestar.moneypit.commands.cmd_cinvite;
import de.minestar.moneypit.commands.cmd_cprivate;
import de.minestar.moneypit.commands.cmd_cpublic;
import de.minestar.moneypit.commands.cmd_cremove;
import de.minestar.moneypit.commands.cmd_ctoggle;
import de.minestar.moneypit.commands.cmd_cuninvite;
import de.minestar.moneypit.commands.cmd_cuninviteall;
import de.minestar.moneypit.commands.cmd_noLock;
import de.minestar.moneypit.data.protection.ProtectionInfo;
import de.minestar.moneypit.database.DatabaseManager;
import de.minestar.moneypit.listener.ActionListener;
import de.minestar.moneypit.listener.EntityListener;
import de.minestar.moneypit.listener.MonitorListener;
import de.minestar.moneypit.manager.EntityModuleManager;
import de.minestar.moneypit.manager.EntityProtectionManager;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.manager.PlayerManager;
import de.minestar.moneypit.manager.ProtectionManager;
import de.minestar.moneypit.manager.QueueManager;

public class MoneyPitCore extends AbstractCore implements IProtectionCore {

    public static MoneyPitCore INSTANCE;

    public static final String NAME = "MoneyPit";

    /** LISTENER */
    public static ActionListener actionListener;
    public static EntityListener entityListener;
    public static MonitorListener monitorListener;

    /** MANAGER */
    public static ModuleManager moduleManager;
    public static EntityModuleManager entityModuleManager;
    public static ProtectionManager protectionManager;
    public static EntityProtectionManager entityProtectionManager;
    public static PlayerManager playerManager;
    public static QueueManager queueManager;

    /** DATABASE */
    public static DatabaseManager databaseManager;

    /** AUTOCLOSETASK */
    private static Timer timer;
    public static AutoCloseBackgroundTask autoCloseTask;

    /** BlockVector */
    private static BlockVector vector;
    private static IProtectionInfo protectionInfo;

    /** CONSTRUCTOR */
    public MoneyPitCore() {
        super(NAME);
    }

    @Override
    protected boolean createManager() {
        INSTANCE = this;

        // create
        moduleManager = new ModuleManager();
        entityModuleManager = new EntityModuleManager();
        playerManager = new PlayerManager();
        protectionManager = new ProtectionManager();
        entityProtectionManager = new EntityProtectionManager();
        queueManager = new QueueManager();
        protectionManager.init();
        entityProtectionManager.init();

        // database
        databaseManager = new DatabaseManager(MoneyPitCore.NAME, new File(getDataFolder(), "sqlconfig.yml"));

        // init
        moduleManager.init();
        entityModuleManager.init();

        // load data
        databaseManager.init();

        // create timer
        autoCloseTask = new AutoCloseBackgroundTask();
        timer = new Timer();
        timer.scheduleAtFixedRate(autoCloseTask, 0, 2 * 1000);

        // create some vars
        MoneyPitCore.vector = new BlockVector("", 0, 0, 0);
        MoneyPitCore.protectionInfo = new ProtectionInfo();
        return true;
    }

    public static boolean protectionsAreEqual(IProtectionInfo protection1, IProtectionInfo protection2) {
        IProtectionInfo protectionChest = protection1;
        IProtectionInfo protectionSign = protection2;

        // BOTH BLOCKS ARE PROTECTED/UNPROTECTED?
        boolean hasChestProtection = protectionChest.hasAnyProtection();
        boolean hasSignProtection = protectionSign.hasAnyProtection();
        if (hasChestProtection != hasSignProtection) {
            return false;
        }

        // CHECK PROTECTION
        if (hasChestProtection && hasSignProtection) {
            // CHECK OWNER
            IProtection chestProtection = protectionChest.getProtection();
            IProtection signProtection = protectionSign.getProtection();

            if (!protectionChest.hasProtection()) {
                chestProtection = protectionChest.getFirstProtection();
            }
            if (!protectionSign.hasProtection()) {
                signProtection = protectionSign.getFirstProtection();
            }

            if (!signProtection.getOwner().equalsIgnoreCase(chestProtection.getOwner())) {
                return false;
            }

            // CHECK PROTECTION TYPE
            if (signProtection.getType().equals(chestProtection.getType())) {
                return true;
            }

            if ((chestProtection.getType().equals(ProtectionType.GIFT) && signProtection.getType().equals(ProtectionType.PRIVATE)) || (chestProtection.getType().equals(ProtectionType.PRIVATE) && signProtection.getType().equals(ProtectionType.GIFT))) {
                return true;
            }

            return false;
        }
        return true;
    }

    @Override
    protected boolean createListener() {
        actionListener = new ActionListener();
        entityListener = new EntityListener();
        monitorListener = new MonitorListener();
        return true;
    }

    @Override
    protected boolean commonDisable() {
        actionListener.closeInventories();

        if (databaseManager.hasConnection()) {
            databaseManager.closeConnection();
        }
        timer.cancel();
        return true;
    }

    @Override
    protected boolean registerEvents(PluginManager pm) {
        pm.registerEvents(actionListener, this);
        pm.registerEvents(entityListener, this);
        pm.registerEvents(monitorListener, this);
        return true;
    }

    @Override
    protected boolean createCommands() {
        //@formatter:off;
        this.cmdList = new CommandList(
                new cmd_cinfo           ("/cinfo",          "",                         "moneypit.commands.cinfo"),
                new cmd_cprivate        ("/cprivate",       "",                         "moneypit.commands.cprivate"),
                new cmd_cpublic         ("/cpublic",        "",                         "moneypit.commands.cpublic"),
                new cmd_cgift           ("/cgift",          "",                         "moneypit.commands.cgift"),                
                new cmd_cremove         ("/cremove",        "",                         "moneypit.commands.cremove"),
                new cmd_cinvite         ("/cinvite",        "<Player> [ ... Player n]", "moneypit.commands.cinvite"),
                new cmd_cuninvite       ("/cuninvite",      "<Player> [ ... Player n]", "moneypit.commands.cuninvite"),
                new cmd_cuninviteall    ("/cuninviteall",   "",                         "moneypit.commands.cuninvite"),
                new cmd_cprivate        ("/cgroup",         "<Name> [Players ...]",     "moneypit.commands.cgroup"),
                new cmd_ctoggle         ("/ctoggle",        "",                         "moneypit.commands.cinfo"),
                new cmd_noLock          ("/cautolock",      "",                         "moneypit.commands.cinfo")
         );
        //@formatter:on;
        return true;
    }

    @Override
    public boolean hasProtection(Block block) {
        vector.update(block.getLocation());
        protectionInfo.update(vector);
        return protectionInfo.hasAnyProtection();
    }

    @Override
    public IProtectionInfo getProtectionInfo(Block block) {
        vector.update(block.getLocation());
        protectionInfo.update(vector);
        return protectionInfo.clone();
    }
}
