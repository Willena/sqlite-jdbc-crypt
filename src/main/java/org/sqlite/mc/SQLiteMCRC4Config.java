package org.sqlite.mc;

public class SQLiteMCRC4Config extends SQLiteMCConfig.Builder {

    public SQLiteMCRC4Config() {
        super();
        setCipher(CipherAlgorithm.RC4);
    }

    @Override
    public SQLiteMCRC4Config setLegacy(int value) {
        if (value != 1) {
            throw new IllegalArgumentException("Legacy value can only be 1");
        }
        super.setLegacy(value);
        return this;
    }

    @Override
    public SQLiteMCRC4Config setLegacyPageSize(int value) {
        super.setLegacyPageSize(value);
        return this;
    }

    public static SQLiteMCRC4Config getDefault() {
        return new SQLiteMCRC4Config().setLegacy(1).setLegacyPageSize(0);
    }
}
