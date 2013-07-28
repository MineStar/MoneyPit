package de.minestar.moneypit.data.subprotection;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.bukkit.gemo.patchworking.ISubProtection;
import com.bukkit.gemo.patchworking.ISubProtectionHolder;

public class SubProtectionHolder implements ISubProtectionHolder {
    private ArrayList<ISubProtection> protections = null;

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtectionHolder#getSize()
     */
    public int getSize() {
        if (this.protections != null) {
            return this.protections.size();
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtectionHolder#getProtections()
     */
    public ArrayList<ISubProtection> getProtections() {
        return this.protections;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtectionHolder#addProtection(de.minestar.moneypit.data.subprotection.SubProtection)
     */
    public void addProtection(ISubProtection subProtection) {
        if (this.protections == null) {
            this.protections = new ArrayList<ISubProtection>();
        }
        if (!this.hasProtection(subProtection)) {
            this.protections.add(subProtection);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtectionHolder#removeProtection(de.minestar.moneypit.data.subprotection.ISubProtection)
     */
    public void removeProtection(ISubProtection subProtection) {
        if (this.protections != null) {
            for (int i = 0; i < this.protections.size(); i++) {
                if (this.protections.get(i).equals(subProtection)) {
                    this.protections.remove(i);
                    break;
                }
            }
            if (this.protections.size() < 1) {
                this.protections = null;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtectionHolder#hasProtection(de.minestar.moneypit.data.subprotection.ISubProtection)
     */
    public boolean hasProtection(ISubProtection subProtection) {
        if (this.protections != null) {
            for (int i = 0; i < this.protections.size(); i++) {
                if (this.protections.get(i).equals(subProtection)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtectionHolder#canAccessAll(org.bukkit.entity.Player)
     */
    public boolean canAccessAll(Player player) {
        if (this.getSize() > 0) {
            for (ISubProtection subProtection : this.protections) {
                if (!subProtection.canAccess(player)) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtectionHolder#areAllPublic()
     */
    public boolean areAllPublic() {
        if (this.getSize() > 0) {
            for (ISubProtection subProtection : this.protections) {
                if (subProtection.isPrivate()) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtectionHolder#canAccessAll(java.lang.String)
     */
    public boolean canAccessAll(String playerName) {
        if (this.getSize() > 0) {
            for (ISubProtection subProtection : this.protections) {
                if (!subProtection.canAccess(playerName)) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtectionHolder#canEditAll(org.bukkit.entity.Player)
     */
    public boolean canEditAll(Player player) {
        if (this.getSize() > 0) {
            for (ISubProtection subProtection : this.protections) {
                if (!subProtection.canEdit(player)) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.minestar.moneypit.data.subprotection.ISubProtectionHolder#getProtection(int)
     */
    public ISubProtection getProtection(int index) {
        if (this.getSize() > 0) {
            return this.protections.get(index);
        }
        return null;
    }
}
