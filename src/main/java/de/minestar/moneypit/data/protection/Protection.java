package de.minestar.moneypit.data.protection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.entity.Player;

import com.bukkit.gemo.patchworking.BlockVector;
import com.bukkit.gemo.patchworking.GuestGroup;
import com.bukkit.gemo.patchworking.IProtection;
import com.bukkit.gemo.patchworking.ProtectionType;
import com.bukkit.gemo.utils.UtilPermissions;

public class Protection implements IProtection {
    // both protections
    private final int databaseID;
    private final BlockVector vector;

    // main-protections only
    private String owner;
    private ProtectionType type;
    private GuestGroup guests;
    private HashMap<BlockVector, IProtection> subProtections;

    // sub-protections only
    private final IProtection parent;
    private final boolean isSubProtection;

    /**
     * Constructor
     * 
     * @param owner
     * @param type
     */
    public Protection(int databaseID, BlockVector vector, String owner, ProtectionType type) {
        this.databaseID = databaseID;
        this.vector = vector;
        this.parent = null;
        this.isSubProtection = false;
        this.owner = owner;
        this.type = type;
        this.guests = new GuestGroup(GuestGroup.DEFAULT_NAME, this.owner);
        this.subProtections = null;
    }

    public Protection(BlockVector vector, IProtection parent) {
        this.vector = vector;
        this.parent = parent;
        this.databaseID = this.parent.getDatabaseID();
        this.isSubProtection = true;
    }

    public void setOwner(String owner) {
        if (this.isSubProtection) {
            this.parent.setOwner(owner);
        } else {
            this.owner = owner;
        }
    }

    public int getDatabaseID() {
        if (this.isSubProtection) {
            return this.parent.getDatabaseID();
        } else {
            return this.databaseID;
        }
    }

    public BlockVector getVector() {
        return this.vector;
    }

    public int getBlockTypeID() {
        return this.vector.getLocation().getBlock().getTypeId();
    }

    public String getOwner() {
        if (this.isSubProtection) {
            return this.parent.getOwner();
        } else {
            return owner;
        }
    }

    public boolean isPublic() {
        if (this.isSubProtection) {
            return this.parent.isPublic();
        } else {
            return this.type == ProtectionType.PUBLIC;
        }
    }

    public boolean isGift() {
        if (this.isSubProtection) {
            return this.parent.isGift();
        } else {
            return this.type == ProtectionType.GIFT;
        }
    }

    public boolean isPrivate() {
        if (this.isSubProtection) {
            return this.parent.isPrivate();
        } else {
            return this.type == ProtectionType.PRIVATE;
        }
    }

    public ProtectionType getType() {
        if (this.isSubProtection) {
            return this.parent.getType();
        } else {
            return type;
        }
    }

    public void addGuest(String guest) {
        if (this.isSubProtection) {
            this.parent.addGuest(guest);
        } else {
            this.guests.add(guest);
        }
    }

    public void removeGuest(String guest) {
        if (this.isSubProtection) {
            this.parent.removeGuest(guest);
        } else {
            this.guests.remove(guest);
        }
    }

    public GuestGroup getGuestList() {
        if (this.isSubProtection) {
            return this.parent.getGuestList();
        } else {
            return this.guests;
        }
    }

    public void setGuestList(GuestGroup group) {
        if (this.isSubProtection) {
            this.parent.setGuestList(group);
        } else {
            this.guests = group;
        }
    }

    public void addGuests(HashSet<String> guestList) {
        if (this.isSubProtection) {
            this.parent.addGuests(guestList);
        } else {
            this.guests.add(guestList);
        }
    }

    public void clearGuestList() {
        if (this.isSubProtection) {
            this.parent.clearGuestList();
        } else {
            if (this.guests.isDefault()) {
                this.guests.clear();
            }
        }
    }

    public boolean isGuest(String guest) {
        if (this.isSubProtection) {
            return this.parent.isGuest(guest);
        } else {
            return this.guests.contains(guest);
        }
    }

    public boolean isOwner(String otherName) {
        if (this.isSubProtection) {
            return this.parent.isOwner(otherName);
        } else {
            return this.owner.equalsIgnoreCase(otherName);
        }
    }

    public IProtection getMainProtection() {
        if (this.isSubProtection) {
            return this.parent;
        } else {
            return this;
        }
    }

    public Collection<IProtection> getSubProtections() {
        if (this.isSubProtection) {
            return this.parent.getSubProtections();
        } else {
            if (this.subProtections != null) {
                return this.subProtections.values();
            }
            return new HashMap<BlockVector, IProtection>().values();
        }
    }

    public boolean hasAnySubProtection() {
        if (this.isSubProtection) {
            return this.parent.hasAnySubProtection();
        } else {
            if (this.subProtections != null) {
                return (this.subProtections.size() > 0);
            }
            return false;
        }
    }

    @Override
    public void addSubProtection(IProtection subProtection) {
        if (this.isSubProtection) {
            this.parent.addSubProtection(subProtection);
        } else {
            if (subProtection.getVector().equals(this.vector)) {
                return;
            }

            if (this.subProtections == null) {
                this.subProtections = new HashMap<BlockVector, IProtection>();
            }

            if (!this.hasSubProtection(subProtection.getVector())) {
                this.subProtections.put(subProtection.getVector(), subProtection);
            }
        }
    }

    public void removeSubProtection(BlockVector vector) {
        if (this.isSubProtection) {
            this.parent.removeSubProtection(vector);
        } else {
            if (this.subProtections != null) {
                this.subProtections.remove(vector);
            }

            if (!this.hasAnySubProtection()) {
                this.subProtections = null;
            }
        }
    }

    public boolean hasSubProtection(BlockVector vector) {
        if (this.isSubProtection) {
            return this.parent.hasSubProtection(vector);
        } else {
            if (this.subProtections != null) {
                return this.subProtections.containsKey(vector);
            }
            return false;
        }
    }

    public boolean canEdit(Player player) {
        return this.isOwner(player.getName()) || UtilPermissions.playerCanUseCommand(player, "moneypit.admin");
    }

    public boolean canAccess(Player player) {
        return this.isPublic() || this.isGift() || this.isOwner(player.getName()) || this.isGuest(player.getName()) || this.canEdit(player);
    }

    public boolean canAccess(String playerName) {
        return this.isPublic() || this.isGift() || this.isOwner(playerName) || this.isGuest(playerName);
    }

    public boolean equals(IProtection protection) {
        return this.databaseID == protection.getDatabaseID() && this.vector.equals(protection.getVector());
    }

    @Override
    public String toString() {
        return "Protection={ ID:" + this.databaseID + " ; Type:" + this.type.name() + " ; Owner:" + this.owner + " ; SubProtection:" + this.isSubProtection + " ; " + this.vector.toString() + " }";
    }

    public void defaultGuestList() {
        this.guests = new GuestGroup(GuestGroup.DEFAULT_NAME, this.owner);
    }
}
