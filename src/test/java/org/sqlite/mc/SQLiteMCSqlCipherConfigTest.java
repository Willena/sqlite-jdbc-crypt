package org.sqlite.mc;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.sqlite.SQLiteConfig;

@DisabledIfSystemProperty(
        disabledReason = "SQLite3 binary not compatible with that test",
        named = "disableCipherTests",
        matches = "true")
class SQLiteMCSqlCipherConfigTest {

    private static final String unsaltedHexKeyValid =
            "54686973206973206D792076657279207365637265742070617373776F72642E".toLowerCase();
    private static final String unsaltedHexKeyInvalid =
            "54686973206973206D79207665765742070617373776F72642".toLowerCase();
    private static final String saltedHexKeyValid =
            "54686973206973206D792076657279207365637265742070617373776F72642E2E73616C7479206B65792073616C742E"
                    .toLowerCase();
    private static final String saltedHexKeyInvalid =
            "54686973206973206D79207070617373776F72642E2E73616C7479206B65792073616C742E"
                    .toLowerCase();

    private static final String hexKey = "54686973206973206d792070";
    private static final String hexKey2 = "aaff54686973206973206d792070";

    // https://www.baeldung.com/java-byte-arrays-hex-strings
    private static byte[] toBytes(String hexString) {
        byte[] byteArray = new BigInteger(hexString, 16).toByteArray();
        if (byteArray[0] == 0) {
            byte[] output = new byte[byteArray.length - 1];
            System.arraycopy(byteArray, 1, output, 0, output.length);
            return output;
        }
        return byteArray;
    }

    @Test
    void setLegacy() {
        SQLiteMCSqlCipherConfig config = new SQLiteMCSqlCipherConfig();
        assertThatThrownBy(() -> config.setLegacy(10)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> config.setLegacy(-1)).isInstanceOf(IllegalArgumentException.class);

        Properties props = config.setLegacy(1).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY.pragmaName)).isEqualTo("1");
    }

    @Test
    void setLegacyPageSize() {
        SQLiteMCSqlCipherConfig config = new SQLiteMCSqlCipherConfig();
        assertThatThrownBy(() -> config.setLegacyPageSize(3))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> config.setLegacyPageSize(-1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> config.setLegacyPageSize(65537))
                .isInstanceOf(IllegalArgumentException.class);

        Properties props = config.setLegacyPageSize(2).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY_PAGE_SIZE.pragmaName)).isEqualTo("2");
    }

    @Test
    void setKdfIter() {
        SQLiteMCSqlCipherConfig config = new SQLiteMCSqlCipherConfig();
        assertThatThrownBy(() -> config.setKdfIter(-1))
                .isInstanceOf(IllegalArgumentException.class);

        Properties props = config.setKdfIter(1).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.KDF_ITER.pragmaName)).isEqualTo("1");
    }

    @Test
    void setFastKdfIter() {
        SQLiteMCSqlCipherConfig config = new SQLiteMCSqlCipherConfig();
        assertThatThrownBy(() -> config.setFastKdfIter(-1))
                .isInstanceOf(IllegalArgumentException.class);

        Properties props = config.setFastKdfIter(1).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.FAST_KDF_ITER.pragmaName)).isEqualTo("1");
    }

    @Test
    void setHmacUse() {
        SQLiteMCSqlCipherConfig config = new SQLiteMCSqlCipherConfig();

        Properties props = config.setHmacUse(true).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_USE.pragmaName)).isEqualTo("1");
    }

    @Test
    void setHmacPgno() {
        SQLiteMCSqlCipherConfig config = new SQLiteMCSqlCipherConfig();

        Properties props = config.setHmacPgno(HmacPgno.BIG_ENDIAN).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_PGNO.pragmaName)).isEqualTo("2");
    }

    @Test
    void setHmacSaltMask() {
        SQLiteMCSqlCipherConfig config = new SQLiteMCSqlCipherConfig();
        assertThatThrownBy(() -> config.setHmacSaltMask(360))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> config.setHmacSaltMask(-1))
                .isInstanceOf(IllegalArgumentException.class);

        Properties props = config.setHmacSaltMask(1).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_SALT_MASK.pragmaName)).isEqualTo("1");
    }

    @Test
    void setKdfAlgorithm() {
        SQLiteMCSqlCipherConfig config = new SQLiteMCSqlCipherConfig();

        Properties props = config.setKdfAlgorithm(KdfAlgorithm.SHA512).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.KDF_ALGORITHM.pragmaName)).isEqualTo("2");
    }

    @Test
    void setHmacAlgorithm() {
        SQLiteMCSqlCipherConfig config = new SQLiteMCSqlCipherConfig();

        Properties props = config.setHmacAlgorithm(HmacAlgorithm.SHA512).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_ALGORITHM.pragmaName)).isEqualTo("2");
    }

    @Test
    void setPlaintextHeaderSize() {
        SQLiteMCSqlCipherConfig config = new SQLiteMCSqlCipherConfig();
        assertThatThrownBy(() -> config.setPlaintextHeaderSize(122))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> config.setPlaintextHeaderSize(-1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> config.setPlaintextHeaderSize(14))
                .isInstanceOf(IllegalArgumentException.class);

        Properties props = config.setPlaintextHeaderSize(32).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.PLAINTEXT_HEADER_SIZE.pragmaName)).isEqualTo("32");
    }

    @Test
    void withRawUnsaltedKey() {
        SQLiteMCSqlCipherConfig config = new SQLiteMCSqlCipherConfig();
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> config.withRawUnsaltedKey(toBytes(unsaltedHexKeyInvalid)));

        config.withRawUnsaltedKey(toBytes(unsaltedHexKeyValid));
        assertThat(config.build().toProperties().getProperty(SQLiteConfig.Pragma.KEY.pragmaName))
                .isEqualTo(("x'" + unsaltedHexKeyValid + "'"));
    }

    @Test
    void withRawSaltedKey() {
        SQLiteMCSqlCipherConfig config = new SQLiteMCSqlCipherConfig();
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> config.withRawSaltedKey(toBytes(saltedHexKeyInvalid)));

        config.withRawSaltedKey(toBytes(saltedHexKeyValid));
        assertThat(config.build().toProperties().getProperty(SQLiteConfig.Pragma.KEY.pragmaName))
                .isEqualTo(("x'" + saltedHexKeyValid + "'"));
    }

    @Test
    void withHexKey() {
        SQLiteMCSqlCipherConfig config = new SQLiteMCSqlCipherConfig();
        config.withHexKey(toBytes(hexKey));

        Properties buildedConfig = config.build().toProperties();

        assertThat(buildedConfig.getProperty(SQLiteConfig.Pragma.KEY.pragmaName)).isEqualTo(hexKey);
        assertThat(buildedConfig.getProperty(SQLiteConfig.Pragma.HEXKEY_MODE.pragmaName))
                .isEqualTo(SQLiteConfig.HexKeyMode.SSE.getValue());
    }

    @Test
    void hexKeyRekey() throws IOException, SQLException {
        File tmpFile = File.createTempFile("tmp-sqlite", ".db");
        tmpFile.deleteOnExit();

        SQLiteMCSqlCipherConfig config = new SQLiteMCSqlCipherConfig();
        Connection con =
                config.withHexKey(hexKey)
                        .build()
                        .createConnection("jdbc:sqlite:file:" + tmpFile.getAbsolutePath());
        con.createStatement().execute(String.format("PRAGMA hexrekey='%s'", hexKey2));
        con.close();

        assertThatExceptionOfType(SQLException.class)
                .isThrownBy(
                        () ->
                                config.withHexKey(hexKey)
                                        .build()
                                        .createConnection(
                                                "jdbc:sqlite:file:" + tmpFile.getAbsolutePath()));

        config.withHexKey(hexKey2)
                .build()
                .createConnection("jdbc:sqlite:file:" + tmpFile.getAbsolutePath());
    }

    @Test
    void getDefault() {
        Properties props = SQLiteMCSqlCipherConfig.getDefault().build().toProperties();

        assertThat(props.get(SQLiteConfig.Pragma.KDF_ITER.pragmaName)).isEqualTo("256000");
        assertThat(props.get(SQLiteConfig.Pragma.FAST_KDF_ITER.pragmaName)).isEqualTo("2");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_USE.pragmaName)).isEqualTo("1");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_PGNO.pragmaName)).isEqualTo("1");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_SALT_MASK.pragmaName)).isEqualTo("58");
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY.pragmaName)).isEqualTo("0");
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY_PAGE_SIZE.pragmaName)).isEqualTo("4096");
        assertThat(props.get(SQLiteConfig.Pragma.KDF_ALGORITHM.pragmaName)).isEqualTo("2");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_ALGORITHM.pragmaName)).isEqualTo("2");
        assertThat(props.get(SQLiteConfig.Pragma.PLAINTEXT_HEADER_SIZE.pragmaName)).isEqualTo("0");

        assertThat(props.get(SQLiteConfig.Pragma.CIPHER.pragmaName)).isEqualTo("sqlcipher");
    }

    @Test
    void getV1Defaults() {
        Properties props = SQLiteMCSqlCipherConfig.getV1Defaults().build().toProperties();

        assertThat(props.get(SQLiteConfig.Pragma.KDF_ITER.pragmaName)).isEqualTo("4000");
        assertThat(props.get(SQLiteConfig.Pragma.FAST_KDF_ITER.pragmaName)).isEqualTo("2");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_USE.pragmaName)).isEqualTo("0");
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY.pragmaName)).isEqualTo("1");
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY_PAGE_SIZE.pragmaName)).isEqualTo("1024");
        assertThat(props.get(SQLiteConfig.Pragma.KDF_ALGORITHM.pragmaName)).isEqualTo("0");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_ALGORITHM.pragmaName)).isEqualTo("0");

        assertThat(props.get(SQLiteConfig.Pragma.CIPHER.pragmaName)).isEqualTo("sqlcipher");
    }

    @Test
    void getV2Defaults() {
        Properties props = SQLiteMCSqlCipherConfig.getV2Defaults().build().toProperties();

        assertThat(props.get(SQLiteConfig.Pragma.KDF_ITER.pragmaName)).isEqualTo("4000");
        assertThat(props.get(SQLiteConfig.Pragma.FAST_KDF_ITER.pragmaName)).isEqualTo("2");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_USE.pragmaName)).isEqualTo("1");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_PGNO.pragmaName)).isEqualTo("1");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_SALT_MASK.pragmaName)).isEqualTo("58");
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY.pragmaName)).isEqualTo("2");
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY_PAGE_SIZE.pragmaName)).isEqualTo("1024");
        assertThat(props.get(SQLiteConfig.Pragma.KDF_ALGORITHM.pragmaName)).isEqualTo("0");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_ALGORITHM.pragmaName)).isEqualTo("0");

        assertThat(props.get(SQLiteConfig.Pragma.CIPHER.pragmaName)).isEqualTo("sqlcipher");
    }

    @Test
    void getV3Defaults() {
        Properties props = SQLiteMCSqlCipherConfig.getV3Defaults().build().toProperties();

        assertThat(props.get(SQLiteConfig.Pragma.KDF_ITER.pragmaName)).isEqualTo("64000");
        assertThat(props.get(SQLiteConfig.Pragma.FAST_KDF_ITER.pragmaName)).isEqualTo("2");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_USE.pragmaName)).isEqualTo("1");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_PGNO.pragmaName)).isEqualTo("1");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_SALT_MASK.pragmaName)).isEqualTo("58");
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY.pragmaName)).isEqualTo("3");
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY_PAGE_SIZE.pragmaName)).isEqualTo("1024");
        assertThat(props.get(SQLiteConfig.Pragma.KDF_ALGORITHM.pragmaName)).isEqualTo("0");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_ALGORITHM.pragmaName)).isEqualTo("0");

        assertThat(props.get(SQLiteConfig.Pragma.CIPHER.pragmaName)).isEqualTo("sqlcipher");
    }

    @Test
    void getV4Defaults() {
        Properties props = SQLiteMCSqlCipherConfig.getV4Defaults().build().toProperties();

        assertThat(props.get(SQLiteConfig.Pragma.KDF_ITER.pragmaName)).isEqualTo("256000");
        assertThat(props.get(SQLiteConfig.Pragma.FAST_KDF_ITER.pragmaName)).isEqualTo("2");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_USE.pragmaName)).isEqualTo("1");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_PGNO.pragmaName)).isEqualTo("1");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_SALT_MASK.pragmaName)).isEqualTo("58");
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY.pragmaName)).isEqualTo("4");
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY_PAGE_SIZE.pragmaName)).isEqualTo("4096");
        assertThat(props.get(SQLiteConfig.Pragma.KDF_ALGORITHM.pragmaName)).isEqualTo("2");
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_ALGORITHM.pragmaName)).isEqualTo("2");
        assertThat(props.get(SQLiteConfig.Pragma.PLAINTEXT_HEADER_SIZE.pragmaName)).isEqualTo("0");

        assertThat(props.get(SQLiteConfig.Pragma.CIPHER.pragmaName)).isEqualTo("sqlcipher");
    }
}
