package org.sqlite.mc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigInteger;
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
}
