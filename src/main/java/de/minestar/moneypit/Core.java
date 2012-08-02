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

import org.bukkit.plugin.PluginManager;

import de.minestar.minestarlibrary.AbstractCore;
import de.minestar.minestarlibrary.commands.CommandList;
import de.minestar.moneypit.commands.cmd_cinfo;
import de.minestar.moneypit.commands.cmd_cprivate;
import de.minestar.moneypit.commands.cmd_cpublic;
import de.minestar.moneypit.commands.cmd_cremove;
import de.minestar.moneypit.listener.ActionListener;
import de.minestar.moneypit.manager.ModuleManager;
import de.minestar.moneypit.manager.PlayerManager;
import de.minestar.moneypit.manager.ProtectionManager;

public class Core extends AbstractCore {

    public static Core INSTANCE;

    public static final String NAME = "MoneyPit";

    /** LISTENER */
    public static ActionListener actionListener;

    /** MANAGER */
    public static ModuleManager moduleManager;
    public static ProtectionManager protectionManager;
    public static PlayerManager playerManager;

    /** CONSTRUCTOR */
    public Core() {
        super(NAME);
    }

    @Override
    protected boolean createManager() {
        INSTANCE = this;

        // create
        moduleManager = new ModuleManager();
        playerManager = new PlayerManager();
        protectionManager = new ProtectionManager();

        // init
        moduleManager.init();
        playerManager.init();
        protectionManager.init();

        return true;
    }

    @Override
    protected boolean createListener() {
        actionListener = new ActionListener();
        return true;
    }

    @Override
    protected boolean commonDisable() {
        return true;
    }

    @Override
    protected boolean registerEvents(PluginManager pm) {
        pm.registerEvents(actionListener, this);
        return true;
    }

    @Override
    protected boolean createCommands() {
        //@formatter:off;
        this.cmdList = new CommandList(
                new cmd_cinfo           ("/cinfo",      "",    "moneypit.commands.cinfo"),
                new cmd_cprivate        ("/cprivate",   "",    "moneypit.commands.cprivate"),
                new cmd_cpublic         ("/cpublic",    "",    "moneypit.commands.cpublic"),
                new cmd_cremove         ("/cremove",    "",    "moneypit.commands.cremove")
         );
        // @formatter: on;
        return true;
    }
}
