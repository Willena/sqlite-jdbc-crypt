package org.sqlite.mc;

public class SQLiteMCWxAES256Config extends SQLiteMCConfig.Builder {

    public SQLiteMCWxAES256Config() {
        super();
        setCipher(CipherAlgorithm.WX_AES256);
    }

    @Override
    public SQLiteMCWxAES256Config setLegacy(int value) {
        if (!isValid(value, 0, 1)) {
            throw new IllegalArgumentException("Legacy must be 0 or 1");
        }
        super.setLegacy(value);
        return this;
    }

    @Override
    public SQLiteMCWxAES256Config setLegacyPageSize(int value) {
        super.setLegacyPageSize(value);
        return this;
    }

    @Override
    public SQLiteMCWxAES256Config setKdfIter(int value) {
        if (!isValid(value, 1, Integer.MAX_VALUE)) {
            throw new IllegalArgumentException("KdfIter must be a positive integer");
        }
        super.setKdfIter(value);
        return this;
    }

    public static SQLiteMCWxAES256Config getDefault() {
        return new SQLiteMCWxAES256Config().setLegacy(0).setLegacyPageSize(0).setKdfIter(4001);
    }
}
