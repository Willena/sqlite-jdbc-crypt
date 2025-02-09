package org.sqlite.mc;

public enum CipherAlgorithm {
    SQL_CIPHER("sqlcipher"),
    RC4("rc4"),
    CHACHA20("chacha20"),
    WX_AES128("aes128cbc"),
    WX_AES256("aes256cbc"),
    ASCON128("ascon128"),
    AEGIS("aegis");

    private final String cipherName;

    CipherAlgorithm(String name) {
        this.cipherName = name;
    }

    public String getValue() {
        return this.cipherName;
    }
}
