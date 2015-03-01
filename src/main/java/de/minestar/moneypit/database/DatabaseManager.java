package de.minestar.moneypit.database;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.IProtection;
import com.bukkit.gemo.patchworking.ProtectionType;

import de.minestar.minestarlibrary.database.AbstractSQLiteHandler;
import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.modules.Module;
import de.minestar.moneypit.utils.ListHelper;

public class DatabaseManager extends AbstractSQLiteHandler {

    private PreparedStatement addProtection, removeProtection, updateGuestList, getProtectionAtPosition, updateOwner;
    private PreparedStatement addSubProtection, removeSubProtections, removeOneSubProtection;
    private PreparedStatement loadAllProtections, loadSubprotections;

    public DatabaseManager(String pluginName, File SQLConfigFile) {
        super(pluginName, SQLConfigFile);
    }

    @Override
    protected void createStructure(String pluginName, Connection con) throws Exception {
        // /////////////////////////////////////////
        //
        // MAIN-PROTECTIONS
        //
        // /////////////////////////////////////////
        StringBuilder builder = new StringBuilder();

        // open statement
        builder.append("CREATE TABLE IF NOT EXISTS `tbl_protections` (");

        // Unique ID
        builder.append("`ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT");
        builder.append(", ");

        // Protectionowner
        builder.append("`owner` TEXT NOT NULL");
        builder.append(", ");

        // ProtectionType as Integer
        builder.append("`protectionType` INTEGER NOT NULL");
        builder.append(", ");

        // Worldname
        builder.append("`blockWorld` TEXT NOT NULL");
        builder.append(", ");

        // BlockX
        builder.append("`blockX` INTEGER NOT NULL");
        builder.append(", ");

        // BlockY
        builder.append("`blockY` INTEGER NOT NULL");
        builder.append(", ");

        // BlockZ
        builder.append("`blockZ` INTEGER NOT NULL");
        builder.append(", ");

        // GuestList - Format : GUEST;GUEST;GUEST
        builder.append("`guestList` TEXT NOT NULL");

        // close statement
        builder.append(");");

        // execute statement
        PreparedStatement statement = con.prepareStatement(builder.toString());
        statement.execute();

        // clear
        statement = null;
        builder.setLength(0);

        // /////////////////////////////////////////
        //
        // SUB-PROTECTIONS
        //
        // /////////////////////////////////////////

        builder = new StringBuilder();

        // open statement
        builder.append("CREATE TABLE IF NOT EXISTS `tbl_subprotections` (");

        // Unique ID
        builder.append("`ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT");
        builder.append(", ");

        // Protectionowner
        builder.append("`parentID` INTEGER NOT NULL");
        builder.append(", ");

        // Worldname
        builder.append("`blockWorld` TEXT NOT NULL");
        builder.append(", ");

        // BlockX
        builder.append("`blockX` INTEGER NOT NULL");
        builder.append(", ");

        // BlockY
        builder.append("`blockY` INTEGER NOT NULL");
        builder.append(", ");

        // BlockZ
        builder.append("`blockZ` INTEGER NOT NULL");

        // close statement
        builder.append(");");

        // execute statement
        statement = con.prepareStatement(builder.toString());
        statement.execute();

        // clear
        statement = null;
        builder.setLength(0);
    }

    @Override
    protected void createStatements(String pluginName, Connection con) throws Exception {
        //@formatter:off;
        this.addProtection              = con.prepareStatement("INSERT INTO `tbl_protections` (owner, protectionType, blockWorld, blockX, blockY, blockZ, guestList) VALUES (?, ?, ?, ?, ?, ?, ?);");
        this.removeProtection           = con.prepareStatement("DELETE FROM `tbl_protections` WHERE ID=?;");
        this.updateGuestList            = con.prepareStatement("UPDATE `tbl_protections` SET guestList=? WHERE ID=?;");
        this.updateOwner                = con.prepareStatement("UPDATE `tbl_protections` SET owner=? WHERE owner=?;");       
        this.getProtectionAtPosition    = con.prepareStatement("SELECT * FROM `tbl_protections` WHERE blockWorld=? AND blockX=? AND blockY=? AND blockZ=? LIMIT 1;");
        this.loadAllProtections         = con.prepareStatement("SELECT * FROM `tbl_protections` ORDER BY ID ASC");
       
        this.addSubProtection           = con.prepareStatement("INSERT INTO `tbl_subprotections` (parentID, blockWorld, blockX, blockY, blockZ) VALUES (?, ?, ?, ?, ?)");
        this.removeSubProtections       = con.prepareStatement("DELETE FROM `tbl_subprotections` WHERE parentID=?");   
        this.removeOneSubProtection     = con.prepareStatement("DELETE FROM `tbl_subprotections` WHERE parentID=? AND blockWorld=? AND blockX=? AND blockY=? AND blockZ=?");    
        this.loadSubprotections         = con.prepareStatement("SELECT * FROM `tbl_subprotections` ORDER BY ID ASC");        
        //@formatter:on;
    }

    /**
     * Init this Manager
     */
    public void init() {
        this.loadAllProtections();
    }

    /**
     * Get the protection at a certain BlockVector
     * 
     * @param vector
     * @return the Protection
     */
    public Protection getProtectionAtPosition(BlockVector vector) {
        try {
            this.getProtectionAtPosition.setString(1, vector.getWorldName());
            this.getProtectionAtPosition.setInt(2, vector.getX());
            this.getProtectionAtPosition.setInt(3, vector.getY());
            this.getProtectionAtPosition.setInt(4, vector.getZ());
            ResultSet results = this.getProtectionAtPosition.executeQuery();
            while (results.next()) {
                return new Protection(results.getInt("ID"), vector.getRelative(0, 0, 0), results.getString("owner"), ProtectionType.byID(results.getInt("protectionType")));
            }
            return null;
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't get the protection at position: " + vector.toString() + "!");
            return null;
        }
    }

    /**
     * Create a new Protection
     * 
     * @param vector
     * @param owner
     * @param type
     * @return the newly created Protection
     */
    public IProtection createProtection(BlockVector vector, String owner, ProtectionType type) {
        try {
            this.addProtection.setString(1, owner);
            this.addProtection.setInt(2, type.getID());
            this.addProtection.setString(3, vector.getWorldName());
            this.addProtection.setInt(4, vector.getX());
            this.addProtection.setInt(5, vector.getY());
            this.addProtection.setInt(6, vector.getZ());
            this.addProtection.setString(7, "");
            this.addProtection.executeUpdate();
            return this.getProtectionAtPosition(vector);
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't create protection!");
            return null;
        }
    }

    /**
     * Create a new Protection
     * 
     * @param vector
     * @param owner
     * @param type
     * @return the newly created Protection
     */
    public boolean createSubProtection(IProtection subProtection, boolean reallySave) {
        if (!reallySave) {
            return true;
        }
        try {
            this.addSubProtection.setInt(1, subProtection.getMainProtection().getDatabaseID());
            this.addSubProtection.setString(2, subProtection.getVector().getWorldName());
            this.addSubProtection.setInt(3, subProtection.getVector().getX());
            this.addSubProtection.setInt(4, subProtection.getVector().getY());
            this.addSubProtection.setInt(5, subProtection.getVector().getZ());
            this.addSubProtection.executeUpdate();
            return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't create subprotection! " + subProtection.toString());
            return false;
        }
    }

    /**
     * Delete all given sunprotections
     * 
     * @param protection
     * @return <b>true</b> if the deletion was successful, otherwise <b>false</b>
     */
    public boolean deleteSubProtections(IProtection mainProtection) {
        try {
            this.removeSubProtections.setInt(1, mainProtection.getDatabaseID());
            this.removeSubProtections.executeUpdate();
            return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't delete subprotection from database @ " + mainProtection);
            return false;
        }
    }

    /**
     * Delete all given sunprotections
     * 
     * @param protection
     * @return <b>true</b> if the deletion was successful, otherwise <b>false</b>
     */
    public boolean deleteSubProtections(int mainProtectionID) {
        try {
            this.removeSubProtections.setInt(1, mainProtectionID);
            this.removeSubProtections.executeUpdate();
            return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't delete subprotection from database @ " + mainProtectionID);
            return false;
        }
    }

    /**
     * Delete a given subprotection
     * 
     * @param protection
     * @return <b>true</b> if the deletion was successful, otherwise <b>false</b>
     */
    public boolean deleteOneSubProtection(IProtection subProtection) {
        try {
            this.removeOneSubProtection.setInt(1, subProtection.getDatabaseID());
            this.removeOneSubProtection.setString(2, subProtection.getVector().getWorldName());
            this.removeOneSubProtection.setInt(3, subProtection.getVector().getX());
            this.removeOneSubProtection.setInt(4, subProtection.getVector().getY());
            this.removeOneSubProtection.setInt(5, subProtection.getVector().getZ());
            this.removeOneSubProtection.executeUpdate();
            return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't delete specific subprotection from database @ " + subProtection);
            return false;
        }
    }

    /**
     * Update the guestlist of a protection
     * 
     * @param protection
     * @param guestList
     * @return <b>true</b> if the update was successful, otherwise <b>false</b>
     */
    public boolean updateGuestList(IProtection protection, String guestList) {
        try {
            this.updateGuestList.setString(1, guestList);
            this.updateGuestList.setInt(2, protection.getDatabaseID());
            this.updateGuestList.executeUpdate();
            return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't save guestList in database! ID=" + protection.getDatabaseID());
            return false;
        }
    }

    public boolean updateOwner(String oldName, String newName) {
        try {
            this.updateOwner.setString(1, newName);
            this.updateOwner.setString(2, oldName);
            this.updateOwner.executeUpdate();
            return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't update owner " + oldName + " to " + newName);
            return false;
        }
    }

    /**
     * Delete a given protection
     * 
     * @param protection
     * @return <b>true</b> if the deletion was successful, otherwise <b>false</b>
     */
    public boolean deleteProtection(BlockVector vector) {
        try {
            IProtection protection = this.getProtectionAtPosition(vector);
            if (protection != null) {
                this.removeProtection.setInt(1, protection.getDatabaseID());
                this.removeProtection.executeUpdate();
                this.deleteSubProtections(protection);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't delete protection from database @ " + vector.toString());
            return false;
        }
    }

    /**
     * Delete a given protection
     * 
     * @param protection
     * @return <b>true</b> if the deletion was successful, otherwise <b>false</b>
     */
    public boolean deleteProtection(int protectionID) {
        try {
            this.removeProtection.setInt(1, protectionID);
            this.removeProtection.executeUpdate();
            this.deleteSubProtections(protectionID);
            return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't delete protection from database @ " + protectionID);
            return false;
        }
    }

    /**
     * Load all protections from the Database
     */
    private void loadAllProtectionsOLD() {
        try {
            ResultSet results = this.loadAllProtections.executeQuery();
            int count = 0;
            Map<Integer, BlockVector> failedProtections = new HashMap<Integer, BlockVector>();
            while (results.next()) {
                BlockVector vector = new BlockVector(results.getString("blockWorld"), results.getInt("blockX"), results.getInt("blockY"), results.getInt("blockZ"));
                try {
                    Location location = vector.getLocation();
                    if (location == null) {
                        failedProtections.put(results.getInt("ID"), vector);
                        continue;
                    }

                    location.getChunk().load(true);
                    Module module = MoneyPitCore.moduleManager.getRegisteredModule(location.getBlock().getTypeId());

                    Hanging entityHanging = null;
                    if (module == null) {
                        // search for an itemframe
                        Collection<ItemFrame> frames = location.getWorld().getEntitiesByClass(ItemFrame.class);
                        boolean found = false;
                        for (ItemFrame frame : frames) {
                            BlockVector otherVector = new BlockVector(frame.getLocation());
                            if (vector.equals(otherVector)) {
                                module = MoneyPitCore.moduleManager.getRegisteredModule(Material.ITEM_FRAME.getId());
                                entityHanging = frame;
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            // search for a painting
                            Collection<Painting> paintings = location.getWorld().getEntitiesByClass(Painting.class);
                            for (Painting paint : paintings) {
                                BlockVector otherVector = new BlockVector(paint.getLocation());
                                if (vector.equals(otherVector)) {
                                    module = MoneyPitCore.moduleManager.getRegisteredModule(Material.PAINTING.getId());
                                    entityHanging = paint;
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                failedProtections.put(results.getInt("ID"), vector);
                                continue;
                            }
                        }
                    }
                    IProtection protection = new Protection(results.getInt("ID"), vector, results.getString("owner"), ProtectionType.byID(results.getInt("protectionType")));
                    protection.setGuestList(ListHelper.toList(results.getString("guestList")));
                    byte subData = location.getBlock().getData();
                    if (module.getRegisteredTypeID() == Material.ITEM_FRAME.getId() || module.getRegisteredTypeID() == Material.PAINTING.getId()) {
                        if (entityHanging != null) {
                            subData = (byte) entityHanging.getAttachedFace().ordinal();
                        } else {
                            failedProtections.put(results.getInt("ID"), vector);
                            continue;
                        }
                    }
                    if (module.addProtection(protection, subData, false)) {
                        ++count;
                    } else {
                        failedProtections.put(results.getInt("ID"), vector);
                    }
                } catch (Exception error) {
                    ConsoleUtils.printWarning(MoneyPitCore.NAME, "Can't load protection: ID=" + results.getInt("ID") + " -> " + vector.toString());
                }
            }
            ConsoleUtils.printInfo(MoneyPitCore.NAME, count + " protections loaded!");
            if (failedProtections.size() > 0) {
                ConsoleUtils.printInfo(MoneyPitCore.NAME, failedProtections.size() + " protections are NOT loaded due to missing blocks or locations!");
                // delete failed protections
                for (Map.Entry<Integer, BlockVector> entry : failedProtections.entrySet()) {
                    this.deleteProtection(entry.getKey());
                }
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't load protections!");
        }
    }

    /**
     * Load all protections from the Database
     */
    private void loadAllProtections() {
        try {
            ResultSet results = this.loadAllProtections.executeQuery();
            Map<Integer, BlockVector> failedProtections = new HashMap<Integer, BlockVector>();
            List<IProtection> cachedProtections = new ArrayList<IProtection>();
            while (results.next()) {
                BlockVector vector = new BlockVector(results.getString("blockWorld"), results.getInt("blockX"), results.getInt("blockY"), results.getInt("blockZ"));
                try {
                    Location location = vector.getLocation();
                    if (location == null) {
                        failedProtections.put(results.getInt("ID"), vector);
                        continue;
                    }

                    IProtection protection = new Protection(results.getInt("ID"), vector, results.getString("owner"), ProtectionType.byID(results.getInt("protectionType")));
                    protection.setGuestList(ListHelper.toList(results.getString("guestList")));
                    cachedProtections.add(protection);
                } catch (Exception error) {
                    ConsoleUtils.printWarning(MoneyPitCore.NAME, "Can't load protection: ID=" + results.getInt("ID") + " -> " + vector.toString());
                    failedProtections.put(results.getInt("ID"), vector);
                    continue;
                }
            }
            
            // load subprotections
            this.loadSubProtections(cachedProtections);

            // print info
            ConsoleUtils.printInfo(MoneyPitCore.NAME, cachedProtections.size() + " protections loaded!");
            if (failedProtections.size() > 0) {
                ConsoleUtils.printInfo(MoneyPitCore.NAME, failedProtections.size() + " protections are NOT loaded due to missing blocks or locations!");
                // delete failed protections
                for (Map.Entry<Integer, BlockVector> entry : failedProtections.entrySet()) {
                    this.deleteProtection(entry.getKey());
                }
            }

            // set cached protections
            MoneyPitCore.protectionManager.setCachedProtections(cachedProtections);
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't load protections!");
        }
    }

    private void loadSubProtections(List<IProtection> cachedProtections) throws SQLException {
        ResultSet results = this.loadSubprotections.executeQuery();
        while (results.next()) {
            IProtection mainProtection = this.getProtection(results.getInt("parentID"), cachedProtections);
            if (mainProtection != null) {
                BlockVector vector = new BlockVector(results.getString("blockWorld"), results.getInt("blockX"), results.getInt("blockY"), results.getInt("blockZ"));
                IProtection subProtection = new Protection(vector, mainProtection);
                mainProtection.addSubProtection(subProtection);
            }
        }
    }

    private IProtection getProtection(int databaseID, List<IProtection> cachedProtections) {
        for (IProtection protection : cachedProtections) {
            if (protection.getDatabaseID() == databaseID) {
                return protection;
            }
        }
        return null;
    }
}
