package org.sqlite.mc;

public class SQLiteMCAscon128Config extends SQLiteMCConfig.Builder {

    public SQLiteMCAscon128Config() {
        super();
        setCipher(CipherAlgorithm.ASCON128);
    }

    @Override
    public SQLiteMCAscon128Config setKdfIter(int value) {
        if (value < 1) {
            throw new IllegalArgumentException("Minimum value for kdf_iter is 1");
        }
        super.setKdfIter(value);
        return this;
    }

    public static SQLiteMCAscon128Config getDefault() {
        return new SQLiteMCAscon128Config().setKdfIter(64007);
    }
}
