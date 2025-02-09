package org.sqlite.mc;

public enum HmacAlgorithm {
    SHA1(0),
    SHA256(1),
    SHA512(2);

    private final int value;

    HmacAlgorithm(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
