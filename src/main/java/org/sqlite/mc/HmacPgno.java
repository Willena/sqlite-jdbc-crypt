package org.sqlite.mc;

public enum HmacPgno {
    NATIVE(0),
    LITTLE_ENDIAN(1),
    BIG_ENDIAN(2);

    private final int value;

    HmacPgno(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
