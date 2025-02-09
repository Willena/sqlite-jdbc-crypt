package org.sqlite.mc;

public class SQLiteMCAegisConfig extends SQLiteMCConfig.Builder {

    public SQLiteMCAegisConfig() {
        super();
        setCipher(CipherAlgorithm.AEGIS);
    }

    @Override
    public SQLiteMCAegisConfig setMCost(long value) {
        return (SQLiteMCAegisConfig) super.setMCost(value);
    }

    @Override
    public SQLiteMCAegisConfig setPCost(long value) {
        return (SQLiteMCAegisConfig) super.setPCost(value);
    }

    @Override
    public SQLiteMCAegisConfig setTCost(long value) {
        return (SQLiteMCAegisConfig) super.setTCost(value);
    }

    @Override
    public SQLiteMCAegisConfig setAegisAlgorithm(AegisAlgorithm algorithm) {
        return (SQLiteMCAegisConfig) super.setAegisAlgorithm(algorithm);
    }

    public static SQLiteMCAegisConfig getDefault() {
        return new SQLiteMCAegisConfig()
                .setAegisAlgorithm(AegisAlgorithm.AEGIS_256)
                .setTCost(2)
                .setMCost(19456)
                .setPCost(1);
    }
}
