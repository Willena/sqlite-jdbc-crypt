package org.sqlite.mc;

public enum KdfAlgorithm {
    SHA1(0),
    SHA256(1),
    SHA512(2);

    private final int value;

    KdfAlgorithm(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
