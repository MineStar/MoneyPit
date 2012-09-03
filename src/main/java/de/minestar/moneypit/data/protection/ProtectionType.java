package de.minestar.moneypit.data.protection;

public enum ProtectionType {
    UNKNOWN(-1, "Unknown"),

    PUBLIC(0, "Public"),

    PRIVATE(1, "Private"),

    GIFT(2, "Gift");

    private final int ID;
    private final String typeName;

    private ProtectionType(int ID, String typeName) {
        this.ID = ID;
        this.typeName = typeName;
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

    public String toString() {
        return this.typeName;
    }
}
