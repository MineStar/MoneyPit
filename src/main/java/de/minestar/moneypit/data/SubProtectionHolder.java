package de.minestar.moneypit.data;

import java.util.ArrayList;

public class SubProtectionHolder {
    private ArrayList<SubProtection> protections = null;

    /**
     * Get the amount of SubProtection in this SubProtectionHolder
     * 
     * @return the amount of protections
     */
    public int getSize() {
        if (this.protections != null) {
            return this.protections.size();
        }
        return 0;
    }

    /**
     * Get all SubProtections
     * 
     * @return all SubProtections
     */
    public ArrayList<SubProtection> getProtections() {
        return this.protections;
    }

    /**
     * Add a SubProtection
     * 
     * @param subProtection
     */
    public void addProtection(SubProtection subProtection) {
        if (this.protections == null) {
            this.protections = new ArrayList<SubProtection>();
        }
        if (!this.hasProtection(subProtection)) {
            this.protections.add(subProtection);
        }
    }

    /**
     * Remove a SubProtection
     * 
     * @param subProtection
     */
    public void removeProtection(SubProtection subProtection) {
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

    /**
     * Check if we have the SubProtection
     * 
     * @param subProtection
     * @return
     */
    public boolean hasProtection(SubProtection subProtection) {
        if (this.protections != null) {
            for (int i = 0; i < this.protections.size(); i++) {
                if (this.protections.get(i).equals(subProtection)) {
                    return true;
                }
            }
        }
        return false;
    }
}
