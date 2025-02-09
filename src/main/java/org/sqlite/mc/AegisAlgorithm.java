package org.sqlite.mc;

public enum AegisAlgorithm {
    AEGIS_128L(1, "aegis-128l"),
    AEGIS_128X2(2, "aegis-128x2"),
    AEGIS_128X4(3, "aegis-128x4"),
    AEGIS_256(4, "aegis-256"),
    AEGIS_256X2(5, "aegis-256x2"),
    AEGIS_256X4(6, "aegis-256x4");

    private final int inValue;
    private final String stringValue;

    AegisAlgorithm(int inValue, String stringValue) {
        this.inValue = inValue;
        this.stringValue = stringValue;
    }

    public int getInValue() {
        return inValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}
