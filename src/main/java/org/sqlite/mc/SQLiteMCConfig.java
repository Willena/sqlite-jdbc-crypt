package org.sqlite.mc;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfigFactory;

public class SQLiteMCConfig extends SQLiteConfig {

    private static final Function<String, String> STRING_PRAGMA_CONVERTER = s -> "'" + s + "'";
    private static final Function<String, String> IDENTITY_CONVERTER = s -> s;

    private static final Map<Pragma, Function<String, String>> CONVERTERS = new HashMap<>();

    static {
        CONVERTERS.put(Pragma.CIPHER, STRING_PRAGMA_CONVERTER);
        CONVERTERS.put(Pragma.ALGORITHM, STRING_PRAGMA_CONVERTER);
    }

    private static final Pragma[] CIPHER_PRAGMA_ORDER =
            new Pragma[]{
                    Pragma.CIPHER,
                    Pragma.LEGACY,
                    Pragma.HMAC_CHECK,
                    Pragma.MC_LEGACY_WAL,
                    Pragma.LEGACY_PAGE_SIZE,
                    Pragma.KDF_ITER,
                    Pragma.FAST_KDF_ITER,
                    Pragma.HMAC_USE,
                    Pragma.HMAC_PGNO,
                    Pragma.HMAC_SALT_MASK,
                    Pragma.KDF_ALGORITHM,
                    Pragma.HMAC_ALGORITHM,
                    Pragma.PLAINTEXT_HEADER_SIZE,
                    Pragma.TCOST,
                    Pragma.MCOST,
                    Pragma.PCOST,
                    Pragma.ALGORITHM
            };

    private SQLiteMCConfig(Properties existingProperties) {
        super(existingProperties);
    }

    @Override
    public Properties toProperties() {
        Properties props = super.toProperties();
        props.put(SQLiteConfigFactory.CONFIG_CLASS_NAME, getClass().getName());
        return props;
    }

    @Override
    protected void setupConnection(
            Connection conn, HashSet<String> pragmaParams, Properties pragmaTable)
            throws SQLException {
        // Remove SQLiteMC related PRAGMAS, so that we are not applied twice
        pragmaParams.remove(Pragma.KEY.pragmaName);
        pragmaParams.remove(Pragma.REKEY.pragmaName);
        pragmaParams.remove(Pragma.CIPHER.pragmaName);
        pragmaParams.remove(Pragma.HMAC_CHECK.pragmaName);
        pragmaParams.remove(Pragma.MC_LEGACY_WAL.pragmaName);
        pragmaParams.remove(Pragma.LEGACY.pragmaName);
        pragmaParams.remove(Pragma.LEGACY_PAGE_SIZE.pragmaName);
        pragmaParams.remove(Pragma.KDF_ITER.pragmaName);
        pragmaParams.remove(Pragma.FAST_KDF_ITER.pragmaName);
        pragmaParams.remove(Pragma.HMAC_USE.pragmaName);
        pragmaParams.remove(Pragma.HMAC_PGNO.pragmaName);
        pragmaParams.remove(Pragma.HMAC_SALT_MASK.pragmaName);
        pragmaParams.remove(Pragma.KDF_ALGORITHM.pragmaName);
        pragmaParams.remove(Pragma.HMAC_ALGORITHM.pragmaName);
        pragmaParams.remove(Pragma.PLAINTEXT_HEADER_SIZE.pragmaName);
        pragmaParams.remove(Pragma.TCOST.pragmaName);
        pragmaParams.remove(Pragma.MCOST.pragmaName);
        pragmaParams.remove(Pragma.PCOST.pragmaName);
        pragmaParams.remove(Pragma.ALGORITHM.pragmaName);

        // Configure before applying the key
        // Call the Cipher parameter function
        try (Statement statement = conn.createStatement()) {
            for (Pragma pragma : SQLiteMCConfig.CIPHER_PRAGMA_ORDER) {
                String property = pragmaTable.getProperty(pragma.getPragmaName(), null);

                if (property != null) {
                    Function<String, String> converter = CONVERTERS.getOrDefault(pragma, IDENTITY_CONVERTER);
                    statement.execute(
                            String.format("PRAGMA %s = %s", pragma.getPragmaName(), converter.apply(property)));
                }
            }
        }

        if (pragmaTable.containsKey(Pragma.PASSWORD.pragmaName)
                || pragmaTable.containsKey(Pragma.KEY.pragmaName)) {
            applyPassword(conn, pragmaTable.getProperty(Pragma.KEY.pragmaName));
        }

        applyRemainingPragmas(conn, pragmaParams);
    }

    public static class Builder {
        private final Properties properties;

        public Builder() {
            this(new Properties());
        }

        public Builder(Properties properties) {
            this.properties = properties;
        }

        private void setPragma(Pragma pragma, String value) {
            properties.put(pragma.pragmaName, value);
        }

        protected static boolean isValid(Integer value, int min, int max) {
            return (value >= min && value <= max);
        }

        protected static String toHexString(byte[] key) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(key);
            StringBuilder hexString = new StringBuilder();
            while (byteBuffer.hasRemaining()) {
                hexString.append(String.format("%02x", byteBuffer.get()));
            }
            return hexString.toString();
        }

        public Builder setPlaintextHeaderSize(int value) {
            setPragma(SQLiteConfig.Pragma.PLAINTEXT_HEADER_SIZE, String.valueOf(value));
            return this;
        }

        public Builder setLegacy(int value) {
            setPragma(SQLiteConfig.Pragma.LEGACY, String.valueOf(value));
            return this;
        }

        public Builder setKdfIter(int value) {
            setPragma(SQLiteConfig.Pragma.KDF_ITER, String.valueOf(value));
            return this;
        }

        public Builder setKdfAlgorithm(KdfAlgorithm value) {
            setPragma(SQLiteConfig.Pragma.KDF_ALGORITHM, String.valueOf(value.getValue()));
            return this;
        }

        public Builder setHmacUse(boolean value) {
            setPragma(SQLiteConfig.Pragma.HMAC_USE, String.valueOf(value ? 1 : 0));
            return this;
        }

        public Builder setHmacSaltMask(int value) {
            setPragma(SQLiteConfig.Pragma.HMAC_SALT_MASK, String.valueOf(value));
            return this;
        }

        public Builder setHmacPgno(HmacPgno value) {
            setPragma(SQLiteConfig.Pragma.HMAC_PGNO, String.valueOf(value.getValue()));
            return this;
        }

        public Builder setHmacAlgorithm(HmacAlgorithm value) {
            setPragma(SQLiteConfig.Pragma.HMAC_ALGORITHM, String.valueOf(value.getValue()));
            return this;
        }

        public Builder setFastKdfIter(int value) {
            setPragma(SQLiteConfig.Pragma.FAST_KDF_ITER, String.valueOf(value));
            return this;
        }

        public Builder setLegacyPageSize(int value) {
            if (value < 0 || value > 65536 || (value & (value - 1)) != 0) {
                throw new IllegalArgumentException(
                        "LegacyPageSize must be a power of 2 between 0 and 65536");
            }
            setPragma(Pragma.LEGACY_PAGE_SIZE, String.valueOf(value));
            return this;
        }

        public Builder setCipher(CipherAlgorithm cipherAlgorithm) {
            setPragma(SQLiteConfig.Pragma.CIPHER, cipherAlgorithm.getValue());
            return this;
        }

        public Builder setTCost(long value) {
            if (value <= 0) {
                throw new IllegalArgumentException("tcost must be positive");
            }
            setPragma(Pragma.TCOST, String.valueOf(value));
            return this;
        }

        public Builder setMCost(long value) {
            if (value <= 0) {
                throw new IllegalArgumentException("mcost must be positive");
            }
            setPragma(Pragma.MCOST, String.valueOf(value));
            return this;
        }

        public Builder setPCost(long value) {
            if (value <= 0) {
                throw new IllegalArgumentException("pcost must be positive");
            }
            setPragma(Pragma.PCOST, String.valueOf(value));
            return this;
        }

        public Builder setAegisAlgorithm(AegisAlgorithm algorithm) {
            setPragma(Pragma.ALGORITHM, algorithm.getStringValue());
            return this;
        }

        public Builder withKey(String key) {

            // Hex Key is a string like any key. It will be processed by SQLite. ex: String a =
            // "x'aecc05ff'"
            // Raw Key is a string like any other key.It will be processed by SQLite. ex: String a =
            // "raw'aecc05ff'"
            setPragma(Pragma.HEXKEY_MODE, String.valueOf(HexKeyMode.NONE));
            setPragma(Pragma.KEY, key);

            // For compatibility reason key as the password Pragma.
            // Here to be compatible keep the code writen in original Xenial JDBC*
            // TODO: Remove this?
            setPragma(Pragma.PASSWORD, key);

            return this;
        }

        public Builder withHexKey(byte[] key) {
            return withHexKey(toHexString(key));
        }

        public Builder withHexKey(String hexkey) {
            withKey(hexkey);
            setPragma(Pragma.HEXKEY_MODE, String.valueOf(HexKeyMode.SSE));
            return this;
        }

        public SQLiteMCConfig build() {
            return new SQLiteMCConfig(this.properties);
        }
    }
}
