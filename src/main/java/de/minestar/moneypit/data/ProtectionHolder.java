package de.minestar.moneypit.data;

import java.util.ArrayList;

public class ProtectionHolder {
    private ArrayList<Protection> protectionList = null;

    /**
     * Get the amount of protections in this ProtectionHolder
     * 
     * @return the amount of protections
     */
    public int getSize() {
        if (this.protectionList != null) {
            return this.protectionList.size();
        }
        return 0;
    }

    /**
     * Add a protection
     * 
     * @param protection
     */
    public void addProtection(Protection protection) {
        if (this.protectionList == null) {
            this.protectionList = new ArrayList<Protection>();
        }
        if (!this.hasProtection(protection)) {
            this.protectionList.add(protection);
        }
    }

    /**
     * Remove a protection
     * 
     * @param protection
     */
    public void removeProtection(Protection protection) {
        if (this.protectionList != null) {
            for (int i = 0; i < this.protectionList.size(); i++) {
                if (this.protectionList.get(i).equals(protection)) {
                    this.protectionList.remove(i);
                    break;
                }
            }
            if (this.protectionList.size() < 1) {
                this.protectionList = null;
            }
        }
    }

    /**
     * Get a protection by its ID
     * 
     * @param ID
     * @return
     */
    public Protection getProtection(int ID) {
        if (this.protectionList != null) {
            for (int i = 0; i < this.protectionList.size(); i++) {
                if (this.protectionList.get(i).getID() == ID) {
                    return this.protectionList.get(i);
                }
            }
        }
        return null;
    }

    /**
     * Check if we have the protection
     * 
     * @param protection
     * @return
     */
    public boolean hasProtection(Protection protection) {
        if (this.protectionList != null) {
            for (int i = 0; i < this.protectionList.size(); i++) {
                if (this.protectionList.get(i).equals(protection)) {
                    return true;
                }
            }
        }
        return false;
    }
}
