package org.sqlite.mc;

public class SQLiteMCWxAES128Config extends SQLiteMCConfig.Builder {

    public SQLiteMCWxAES128Config() {
        super();
        setCipher(CipherAlgorithm.WX_AES128);
    }

    @Override
    public SQLiteMCWxAES128Config setLegacy(int value) {
        if (!isValid(value, 0, 1)) {
            throw new IllegalArgumentException("Legacy must be 0 or 1");
        }
        super.setLegacy(value);
        return this;
    }

    @Override
    public SQLiteMCWxAES128Config setLegacyPageSize(int value) {
        super.setLegacyPageSize(value);
        return this;
    }

    public static SQLiteMCWxAES128Config getDefault() {
        return new SQLiteMCWxAES128Config().setLegacy(0).setLegacyPageSize(0);
    }
}
