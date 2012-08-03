package de.minestar.moneypit.data.protection;

public enum ProtectionType {
    UNKNOWN(-1),

    PUBLIC(0),

    PRIVATE(1);

    private final int ID;

    private ProtectionType(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return this.ID;
    }

    public static ProtectionType byID(int ID) {
        for (ProtectionType type : ProtectionType.values()) {
            if (type.getID() == ID)
                return type;
        }
        return UNKNOWN;
    }
}
