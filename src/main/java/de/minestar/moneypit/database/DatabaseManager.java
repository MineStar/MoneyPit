package de.minestar.moneypit.database;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.Guest;
import com.bukkit.gemo.patchworking.IProtection;
import com.bukkit.gemo.patchworking.ProtectionType;

import de.minestar.minestarlibrary.database.AbstractSQLiteHandler;
import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.moneypit.MoneyPitCore;
import de.minestar.moneypit.data.guests.Group;
import de.minestar.moneypit.data.guests.GroupManager;
import de.minestar.moneypit.data.protection.EntityProtection;
import de.minestar.moneypit.data.protection.Protection;
import de.minestar.moneypit.utils.ListHelper;

public class DatabaseManager extends AbstractSQLiteHandler {

    private PreparedStatement addProtection, removeProtection, updateGuestList, getProtectionAtPosition, updateOwner;
    private PreparedStatement addSubProtection, removeSubProtections, removeOneSubProtection;
    private PreparedStatement loadAllProtections, loadSubprotections;
    private PreparedStatement loadAllGroups, addGroup, removeGroup, updateGroupGuests, updateGroupOwner;
    private PreparedStatement loadAllEntityProtections, updateEntityOwner, removeEntityProtection, addEntityProtection, updateGuestListEntityProtection, getEntityProtectionByUuid;

    public DatabaseManager(String pluginName, File SQLConfigFile) {
        super(pluginName, SQLConfigFile);
    }

    @Override
    protected void createStructure(String pluginName, Connection connection) throws Exception {
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
        PreparedStatement statement = connection.prepareStatement(builder.toString());
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
        statement = connection.prepareStatement(builder.toString());
        statement.execute();

        // clear
        statement = null;
        builder.setLength(0);

        // /////////////////////////////////////////
        //
        // ENTITY-PROTECTIONS
        //
        // /////////////////////////////////////////

        builder = new StringBuilder();

        // open statement
        builder.append("CREATE TABLE IF NOT EXISTS `tbl_entityprotections` (");

        // Protectionowner
        builder.append("`owner` TEXT NOT NULL");
        builder.append(", ");

        // ProtectionType as Integer
        builder.append("`entityType` TEXT NOT NULL");
        builder.append(", ");

        // ProtectionType as Integer
        builder.append("`protectionType` INTEGER NOT NULL");
        builder.append(", ");

        // UUID of protected entity
        builder.append("`entityUuid` TEXT NOT NULL PRIMARY KEY");
        builder.append(", ");

        // GuestList - Format : GUEST;GUEST;GUEST
        builder.append("`guestList` TEXT NOT NULL");

        // close statement
        builder.append(");");

        // execute statement
        statement = connection.prepareStatement(builder.toString());
        statement.execute();

        // clear
        statement = null;
        builder.setLength(0);

        // /////////////////////////////////////////
        //
        // GROUPS
        //
        // /////////////////////////////////////////
        this.createGroupTable(connection);
    }

    private void createGroupTable(Connection connection) throws SQLException {
        StringBuilder builder = new StringBuilder();

        // open statement
        builder.append("CREATE TABLE IF NOT EXISTS `tbl_groups` (");

        // Protectionowner
        builder.append("`owner` TEXT NOT NULL");
        builder.append(", ");

        // Worldname
        builder.append("`name` TEXT NOT NULL");
        builder.append(", ");

        // GuestList - Format : GUEST;GUEST;GUEST
        builder.append("`guestList` TEXT NOT NULL");

        // close statement
        builder.append(");");

        // execute statement
        PreparedStatement statement = connection.prepareStatement(builder.toString());
        statement.execute();
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
        
        this.loadAllGroups              = con.prepareStatement("SELECT * FROM `tbl_groups`");
        this.addGroup                   = con.prepareStatement("INSERT INTO `tbl_groups` (owner, name, guestList) VALUES (?, ?, ?);");
        this.removeGroup                = con.prepareStatement("DELETE FROM `tbl_groups` WHERE owner=? AND name=?;");       
        this.updateGroupOwner           = con.prepareStatement("UPDATE `tbl_groups` SET owner=? WHERE owner=?");
        this.updateGroupGuests          = con.prepareStatement("UPDATE `tbl_groups` SET guestList=? WHERE owner=? AND name=?;");
        
        this.loadAllEntityProtections       = con.prepareStatement("SELECT * FROM `tbl_entityprotections`");
        this.updateEntityOwner              = con.prepareStatement("UPDATE `tbl_entityprotections` SET owner=? WHERE owner=?;");       
        this.removeEntityProtection         = con.prepareStatement("DELETE FROM `tbl_entityprotections` WHERE entityuuid=?;");
        this.addEntityProtection                = con.prepareStatement("INSERT INTO `tbl_entityprotections` (owner, entityType, protectionType, entityuuid, guestList) VALUES (?, ?, ?, ?, ?);");       
        this.updateGuestListEntityProtection    = con.prepareStatement("UPDATE `tbl_entityprotections` SET guestList=? WHERE entityuuid=?;");
        this.getEntityProtectionByUuid          = con.prepareStatement("SELECT * FROM `tbl_entityprotections` WHERE entityuuid=? LIMIT 1;");        
        //@formatter:on;
    }

    /**
     * Init this Manager
     */
    public void init() {
        this.loadAllGroups();
        this.loadAllProtections();
        this.loadAllEntityProtections();
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
     * Create a new EntityProtection
     * 
     * @param vector
     * @param owner
     * @param type
     * @return the newly created EntityProtection
     */
    public EntityProtection createEntityProtection(String owner, UUID uuid, EntityType type, ProtectionType protectionType) {
        try {
            this.addEntityProtection.setString(1, owner);
            this.addEntityProtection.setString(2, type.name());
            this.addEntityProtection.setInt(3, protectionType.getID());
            this.addEntityProtection.setString(4, uuid.toString());
            this.addEntityProtection.setString(5, "");
            this.addEntityProtection.executeUpdate();
            return this.getEntityProtectionByUuid(uuid);
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't create protection!");
            return null;
        }
    }

    /**
     * Get the EntityProtection of a certain UUID
     * 
     * @param uuid
     * @return the EntityProtection
     */
    public EntityProtection getEntityProtectionByUuid(UUID uuid) {
        try {
            this.getEntityProtectionByUuid.setString(1, uuid.toString());
            ResultSet results = this.getEntityProtectionByUuid.executeQuery();
            while (results.next()) {
                return new EntityProtection(results.getString("owner"), UUID.fromString(results.getString("entityUuid")), EntityType.valueOf(results.getString("entityType")), ProtectionType.byID(results.getInt("protectionType")));
            }
            return null;
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't get the protection for uuid: " + uuid.toString() + "!");
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
    public boolean updateEntityProtectionGuestList(EntityProtection protectedEntity, Collection<Guest> guestList) {
        try {
            this.updateGuestListEntityProtection.setString(1, ListHelper.toString(guestList));
            this.updateGuestListEntityProtection.setString(2, protectedEntity.getUuid().toString());
            this.updateGuestListEntityProtection.executeUpdate();
            return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't save guestList of entity in database! ID=" + protectedEntity.getUuid().toString());
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
    public boolean updateGuestList(IProtection protection, Collection<Guest> guestList) {
        try {
            this.updateGuestList.setString(1, ListHelper.toString(guestList));
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

            this.updateEntityOwner.setString(1, newName);
            this.updateEntityOwner.setString(2, oldName);
            this.updateEntityOwner.executeUpdate();

            this.updateGroupOwner.setString(1, newName);
            this.updateGroupOwner.setString(2, oldName);
            this.updateGroupOwner.executeUpdate();
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
    public boolean deleteEntityProtection(EntityProtection protectedEntity) {
        try {
            this.removeEntityProtection.setString(1, protectedEntity.getUuid().toString());
            this.removeEntityProtection.executeUpdate();
            return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't delete entityprotection from database @ " + protectedEntity.getUuid().toString());
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
    private void loadAllEntityProtections() {
        try {
            ResultSet results = this.loadAllEntityProtections.executeQuery();
            int loaded = 0;
            while (results.next()) {
                try {
                    EntityProtection protectedEntity = new EntityProtection(results.getString("owner"), UUID.fromString(results.getString("entityUuid")), EntityType.valueOf(results.getString("entityType")), ProtectionType.byID(results.getInt("protectionType")));
                    protectedEntity.setGuestList(ListHelper.toList(results.getString("guestList")));
                    MoneyPitCore.entityProtectionManager.addProtection(protectedEntity);
                    loaded++;
                } catch (Exception error) {
                    ConsoleUtils.printWarning(MoneyPitCore.NAME, "Can't load entityprotection: ID=" + results.getInt("ID") + " -> " + results.getString("entityUuid"));
                    continue;
                }
            }
            ConsoleUtils.printInfo(MoneyPitCore.NAME, loaded + " entityprotections loaded!");
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
            HashMap<Integer, IProtection> cachedProtections = new HashMap<Integer, IProtection>();
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
                    cachedProtections.put(protection.getDatabaseID(), protection);
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

    private void loadSubProtections(HashMap<Integer, IProtection> cachedProtections) throws SQLException {
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

    private IProtection getProtection(int databaseID, HashMap<Integer, IProtection> cachedProtections) {
        return cachedProtections.get(databaseID);
    }

    /**
     * Load all groups from the Database
     */
    private void loadAllGroups() {
        try {
            ResultSet results = this.loadAllGroups.executeQuery();
            int loaded = 0;
            while (results.next()) {
                try {
                    String owner = results.getString("owner");
                    String groupName = results.getString("name");
                    String guestList = results.getString("guestList");
                    if (!GroupManager.hasGroup(owner, groupName)) {
                        Group group = GroupManager.createGroup(owner, groupName);
                        group.addPlayers(ListHelper.toList(guestList));
                    }
                    loaded++;
                } catch (Exception error) {
                    ConsoleUtils.printWarning(MoneyPitCore.NAME, "Can't load group: Owner=" + results.getInt("owner") + " -> Name=" + results.getString("name"));
                    continue;
                }
            }
            ConsoleUtils.printInfo(MoneyPitCore.NAME, loaded + " groups loaded!");
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't load groups!");
        }
    }

    /**
     * Add a group
     */
    public boolean addGroup(Group group) {
        try {
            this.addGroup.setString(1, group.getOwner());
            this.addGroup.setString(2, group.getName());
            this.addGroup.setString(3, ListHelper.fromStringsToString(group.getPlayerList()));
            this.addGroup.executeUpdate();
            return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't create group!");
            return false;
        }
    }

    /**
     * Remove a group
     */
    public boolean deleteGroup(Group group) {
        try {
            this.removeGroup.setString(1, group.getOwner());
            this.removeGroup.setString(2, group.getName());
            this.removeGroup.executeUpdate();
            return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't delete group from database!");
            return false;
        }
    }

    /**
     * Update the guestlist of a group
     */
    public boolean updateGroupGuestList(Group group) {
        try {
            this.updateGroupGuests.setString(1, ListHelper.fromStringsToString(group.getPlayerList()));
            this.updateGroupGuests.setString(2, group.getOwner());
            this.updateGroupGuests.setString(3, group.getName());
            this.updateGroupGuests.executeUpdate();
            return true;
        } catch (Exception e) {
            ConsoleUtils.printException(e, MoneyPitCore.NAME, "Can't update guestList of group! Owner=" + group.getOwner() + " , Name=" + group.getName());
            return false;
        }
    }
}
