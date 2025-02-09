package org.sqlite.mc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.sqlite.SQLiteConfig;

@DisabledIfSystemProperty(
        disabledReason = "SQLite3 binary not compatible with that test",
        named = "disableCipherTests",
        matches = "true")
class SQLiteMCChacha20ConfigTest {

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
        SQLiteMCChacha20Config config = new SQLiteMCChacha20Config();
        assertThatThrownBy(() -> config.setLegacy(5555)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> config.setLegacy(-1)).isInstanceOf(IllegalArgumentException.class);

        config.setLegacy(1);
        Properties props = config.build().toProperties();

        assertThat(props.get(SQLiteConfig.Pragma.LEGACY.pragmaName)).isEqualTo("1");
    }

    @Test
    void setLegacyPageSize() {
        SQLiteMCChacha20Config config = new SQLiteMCChacha20Config();
        assertThatThrownBy(() -> config.setLegacyPageSize(65537)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> config.setLegacyPageSize(-1)).isInstanceOf(IllegalArgumentException.class);

        config.setLegacyPageSize(1);
        Properties props = config.build().toProperties();

        assertThat(props.get(SQLiteConfig.Pragma.LEGACY_PAGE_SIZE.pragmaName)).isEqualTo("1");
    }

    @Test
    void setKdfIter() {
        SQLiteMCChacha20Config config = new SQLiteMCChacha20Config();
        assertThatThrownBy(() -> config.setKdfIter(-1)).isInstanceOf(IllegalArgumentException.class);

        config.setKdfIter(1);
        Properties props = config.build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.KDF_ITER.pragmaName)).isEqualTo("1");
    }

    @Test
    void withRawUnsaltedKey() {
        SQLiteMCChacha20Config config = new SQLiteMCChacha20Config();
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> config.withRawUnsaltedKey(toBytes(unsaltedHexKeyInvalid)));

        config.withRawUnsaltedKey(toBytes(unsaltedHexKeyValid));
        assertThat(config.build().toProperties().getProperty(SQLiteConfig.Pragma.KEY.pragmaName))
                .isEqualTo(("raw:" + unsaltedHexKeyValid));
    }

    @Test
    void withRawSaltedKey() {
        SQLiteMCChacha20Config config = new SQLiteMCChacha20Config();
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> config.withRawSaltedKey(toBytes(saltedHexKeyInvalid)));

        config.withRawSaltedKey(toBytes(saltedHexKeyValid));
        assertThat(config.build().toProperties().getProperty(SQLiteConfig.Pragma.KEY.pragmaName))
                .isEqualTo(("raw:" + saltedHexKeyValid));
    }

    @Test
    void getDefault() {
        SQLiteMCChacha20Config def = SQLiteMCChacha20Config.getDefault();
        Properties props = def.build().toProperties();

        assertThat(props.get(SQLiteConfig.Pragma.LEGACY.pragmaName)).isEqualTo("0");
        assertThat(props.get(SQLiteConfig.Pragma.KDF_ITER.pragmaName)).isEqualTo("64007");
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY_PAGE_SIZE.pragmaName)).isEqualTo("4096");
        assertThat(props.get(SQLiteConfig.Pragma.CIPHER.pragmaName)).isEqualTo("chacha20");

    }

    @Test
    void getSqlleetDefaults() {
        SQLiteMCChacha20Config def = SQLiteMCChacha20Config.getSqlleetDefaults();
        Properties props = def.build().toProperties();

        assertThat(props.get(SQLiteConfig.Pragma.LEGACY.pragmaName)).isEqualTo("1");
        assertThat(props.get(SQLiteConfig.Pragma.KDF_ITER.pragmaName)).isEqualTo("12345");
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY_PAGE_SIZE.pragmaName)).isEqualTo("4096");
        assertThat(props.get(SQLiteConfig.Pragma.CIPHER.pragmaName)).isEqualTo("chacha20");

    }

}
