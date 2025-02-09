package org.sqlite.mc;

public class SQLiteMCSqlCipherConfig extends SQLiteMCConfig.Builder {

    public SQLiteMCSqlCipherConfig() {
        super();
        setCipher(CipherAlgorithm.SQL_CIPHER);
    }

    @Override
    public SQLiteMCSqlCipherConfig setLegacy(int value) {
        if (!isValid(value, 0, 4)) {
            throw new IllegalArgumentException("Legacy must be between 0 and 4");
        }
        super.setLegacy(value);
        return this;
    }

    @Override
    public SQLiteMCSqlCipherConfig setLegacyPageSize(int value) {
        super.setLegacyPageSize(value);
        return this;
    }

    @Override
    public SQLiteMCSqlCipherConfig setKdfIter(int value) {
        if (!isValid(value, 1, Integer.MAX_VALUE)) {
            throw new IllegalArgumentException("KdfIter must be a positive integer");
        }
        super.setKdfIter(value);
        return this;
    }

    @Override
    public SQLiteMCSqlCipherConfig setFastKdfIter(int value) {
        if (!isValid(value, 1, Integer.MAX_VALUE)) {
            throw new IllegalArgumentException("FastKdfIter must be a positive integer");
        }
        super.setFastKdfIter(value);
        return this;
    }

    @Override
    public SQLiteMCSqlCipherConfig setHmacUse(boolean value) {
        super.setHmacUse(value);
        return this;
    }

    @Override
    public SQLiteMCSqlCipherConfig setHmacPgno(HmacPgno value) {
        super.setHmacPgno(value);
        return this;
    }

    @Override
    public SQLiteMCSqlCipherConfig setHmacSaltMask(int value) {
        if (!isValid(value, 0, 255)) {
            throw new IllegalArgumentException("HmacSaltMask must be between 0 and 255");
        }
        super.setHmacSaltMask(value);
        return this;
    }

    @Override
    public SQLiteMCSqlCipherConfig setKdfAlgorithm(KdfAlgorithm value) {
        super.setKdfAlgorithm(value);
        return this;
    }

    @Override
    public SQLiteMCSqlCipherConfig setHmacAlgorithm(HmacAlgorithm value) {
        super.setHmacAlgorithm(value);
        return this;
    }

    @Override
    public SQLiteMCSqlCipherConfig setPlaintextHeaderSize(int value) {
        if (!isValid(value, 0, 100) || value % 16 != 0) {
            throw new IllegalArgumentException(
                    "PlainTextHeaderSize must be a multiple of 16 and between 0 and 100");
        }
        super.setPlaintextHeaderSize(value);
        return this;
    }

    public SQLiteMCSqlCipherConfig withRawUnsaltedKey(byte[] key) {
        if (key.length != 32) {
            throw new IllegalArgumentException(
                    String.format(
                            "Raw unsalted key must be exactly 32 bytes long (provided: %s)",
                            key.length));
        }

        return withRawKey(toHexString(key));
    }

    public SQLiteMCSqlCipherConfig withRawSaltedKey(byte[] key) {
        if (key.length != 48) {
            throw new IllegalArgumentException(
                    String.format(
                            "Raw unsalted key must be exactly 48 bytes long (provided: %s)",
                            key.length));
        }

        return withRawKey(toHexString(key));
    }

    private SQLiteMCSqlCipherConfig withRawKey(String key) {
        if (key.length() != 64 && key.length() != 96) {
            throw new IllegalArgumentException(
                    String.format(
                            "Raw unsalted key must be exactly 64 or 96 char long (provided: %s)",
                            key.length()));
        }
        withKey(String.format("x'%s'", key));
        return this;
    }

    public static SQLiteMCSqlCipherConfig getDefault() {
        return new SQLiteMCSqlCipherConfig()
                .setKdfIter(256000)
                .setFastKdfIter(2)
                .setHmacUse(true)
                .setHmacPgno(HmacPgno.LITTLE_ENDIAN)
                .setHmacSaltMask(0x3a)
                .setLegacy(0)
                .setLegacyPageSize(4096)
                .setKdfAlgorithm(KdfAlgorithm.SHA512)
                .setHmacAlgorithm(HmacAlgorithm.SHA512)
                .setPlaintextHeaderSize(0);
    }

    public static SQLiteMCSqlCipherConfig getV1Defaults() {
        return new SQLiteMCSqlCipherConfig()
                .setKdfIter(4000)
                .setFastKdfIter(2)
                .setHmacUse(false)
                .setLegacy(1)
                .setLegacyPageSize(1024)
                .setKdfAlgorithm(KdfAlgorithm.SHA1)
                .setHmacAlgorithm(HmacAlgorithm.SHA1);
    }

    public static SQLiteMCSqlCipherConfig getV2Defaults() {
        return new SQLiteMCSqlCipherConfig()
                .setKdfIter(4000)
                .setFastKdfIter(2)
                .setHmacUse(true)
                .setHmacPgno(HmacPgno.LITTLE_ENDIAN)
                .setHmacSaltMask(0x3a)
                .setLegacy(2)
                .setLegacyPageSize(1024)
                .setKdfAlgorithm(KdfAlgorithm.SHA1)
                .setHmacAlgorithm(HmacAlgorithm.SHA1);
    }

    public static SQLiteMCSqlCipherConfig getV3Defaults() {
        return new SQLiteMCSqlCipherConfig()
                .setKdfIter(64000)
                .setFastKdfIter(2)
                .setHmacUse(true)
                .setHmacPgno(HmacPgno.LITTLE_ENDIAN)
                .setHmacSaltMask(0x3a)
                .setLegacy(3)
                .setLegacyPageSize(1024)
                .setKdfAlgorithm(KdfAlgorithm.SHA1)
                .setHmacAlgorithm(HmacAlgorithm.SHA1);
    }

    public static SQLiteMCSqlCipherConfig getV4Defaults() {
        return new SQLiteMCSqlCipherConfig()
                .setKdfIter(256000)
                .setFastKdfIter(2)
                .setHmacUse(true)
                .setHmacPgno(HmacPgno.LITTLE_ENDIAN)
                .setHmacSaltMask(0x3a)
                .setLegacy(4)
                .setLegacyPageSize(4096)
                .setKdfAlgorithm(KdfAlgorithm.SHA512)
                .setHmacAlgorithm(HmacAlgorithm.SHA512)
                .setPlaintextHeaderSize(0);
    }
}
