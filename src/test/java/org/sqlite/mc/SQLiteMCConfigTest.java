package org.sqlite.mc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.sqlite.SQLiteConfig.Pragma.LEGACY_PAGE_SIZE;

import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfigFactory;

class SQLiteMCConfigTest {

    @Test
    void builderSetLegacyPageSizeTest() {
        Properties p = new SQLiteMCConfig.Builder().setLegacyPageSize(0).build().toProperties();
        assertThat(p.get(LEGACY_PAGE_SIZE.getPragmaName())).isEqualTo("0");

        p = new SQLiteMCConfig.Builder().setLegacyPageSize(65536).build().toProperties();
        assertThat(p.get(LEGACY_PAGE_SIZE.getPragmaName())).isEqualTo("65536");

        p = new SQLiteMCConfig.Builder().setLegacyPageSize(1024).build().toProperties();
        assertThat(p.get(LEGACY_PAGE_SIZE.getPragmaName())).isEqualTo("1024");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new SQLiteMCConfig.Builder().setLegacyPageSize(-1));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new SQLiteMCConfig.Builder().setLegacyPageSize(65637));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new SQLiteMCConfig.Builder().setLegacyPageSize(1233));
    }

    @Test
    void isValid() {
        assertThat(SQLiteMCConfig.Builder.isValid(1, 5, 10)).isFalse();
        assertThat(SQLiteMCConfig.Builder.isValid(5, 5, 10)).isTrue();
        assertThat(SQLiteMCConfig.Builder.isValid(7, 5, 10)).isTrue();
        assertThat(SQLiteMCConfig.Builder.isValid(10, 5, 10)).isTrue();
        assertThat(SQLiteMCConfig.Builder.isValid(11, 5, 10)).isFalse();
    }

    @Test
    void toHexString() {
        assertThat(SQLiteMCConfig.Builder.toHexString(new byte[] {12})).isEqualTo("0c");
    }

    @Test
    void fromProperties() {
        Properties p = new Properties();
        p.put(SQLiteConfig.Pragma.CIPHER.getPragmaName(), CipherAlgorithm.AEGIS.getValue());

        Properties p2 = new SQLiteMCConfig.Builder(p).build().toProperties();
        assertThat(p2.get(SQLiteConfig.Pragma.CIPHER.getPragmaName())).isEqualTo("aegis");
    }

    @Test
    void setPlaintextHeaderSize() {
        Properties props =
                new SQLiteMCConfig.Builder().setPlaintextHeaderSize(12).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.PLAINTEXT_HEADER_SIZE.getPragmaName()))
                .isEqualTo("12");
    }

    @Test
    void setLegacy() {
        Properties props = new SQLiteMCConfig.Builder().setLegacy(1).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY.getPragmaName())).isEqualTo("1");
    }

    @Test
    void setKdfIter() {
        Properties props = new SQLiteMCConfig.Builder().setKdfIter(11).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.KDF_ITER.getPragmaName())).isEqualTo("11");
    }

    @Test
    void setKdfAlgorithm() {
        Properties props =
                new SQLiteMCConfig.Builder()
                        .setKdfAlgorithm(KdfAlgorithm.SHA512)
                        .build()
                        .toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.KDF_ALGORITHM.getPragmaName())).isEqualTo("2");
    }

    @Test
    void setHmacUse() {
        Properties props = new SQLiteMCConfig.Builder().setHmacUse(true).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_USE.getPragmaName())).isEqualTo("1");
    }

    @Test
    void setHmacSaltMask() {
        Properties props = new SQLiteMCConfig.Builder().setHmacSaltMask(12).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_SALT_MASK.getPragmaName())).isEqualTo("12");
    }

    @Test
    void setHmacPgno() {
        Properties props =
                new SQLiteMCConfig.Builder()
                        .setHmacPgno(HmacPgno.LITTLE_ENDIAN)
                        .build()
                        .toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_PGNO.getPragmaName())).isEqualTo("1");
    }

    @Test
    void setHmacAlgorithm() {
        Properties props =
                new SQLiteMCConfig.Builder()
                        .setHmacAlgorithm(HmacAlgorithm.SHA512)
                        .build()
                        .toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.HMAC_ALGORITHM.getPragmaName())).isEqualTo("2");
    }

    @Test
    void setFastKdfIter() {
        Properties props = new SQLiteMCConfig.Builder().setFastKdfIter(123).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.FAST_KDF_ITER.getPragmaName())).isEqualTo("123");
    }

    @Test
    void setCipher() {
        Properties props =
                new SQLiteMCConfig.Builder().setCipher(CipherAlgorithm.RC4).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.CIPHER.getPragmaName())).isEqualTo("rc4");
    }

    @Test
    void setTCost() {
        Properties props = new SQLiteMCConfig.Builder().setTCost(12).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.TCOST.getPragmaName())).isEqualTo("12");
    }

    @Test
    void setMCost() {
        Properties props = new SQLiteMCConfig.Builder().setMCost(12).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.MCOST.getPragmaName())).isEqualTo("12");
    }

    @Test
    void setPCost() {
        Properties props = new SQLiteMCConfig.Builder().setPCost(12).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.PCOST.getPragmaName())).isEqualTo("12");
    }

    @Test
    void setAegisAlgorithm() {
        Properties props =
                new SQLiteMCConfig.Builder()
                        .setAegisAlgorithm(AegisAlgorithm.AEGIS_256)
                        .build()
                        .toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.ALGORITHM.getPragmaName())).isEqualTo("aegis-256");
    }

    @Test
    void withHexKey() {
        Properties props =
                new SQLiteMCConfig.Builder().withHexKey(new byte[] {1, 2}).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.HEXKEY_MODE.getPragmaName())).isEqualTo("SSE");
        assertThat(props.get(SQLiteConfig.Pragma.PASSWORD.getPragmaName())).isEqualTo("0102");
        assertThat(props.get(SQLiteConfig.Pragma.KEY.getPragmaName())).isEqualTo("0102");

        Properties props2 = new SQLiteMCConfig.Builder().withHexKey("0102").build().toProperties();
        assertThat(props2.get(SQLiteConfig.Pragma.HEXKEY_MODE.getPragmaName())).isEqualTo("SSE");
        assertThat(props2.get(SQLiteConfig.Pragma.PASSWORD.getPragmaName())).isEqualTo("0102");
        assertThat(props2.get(SQLiteConfig.Pragma.KEY.getPragmaName())).isEqualTo("0102");
    }

    @Test
    void withKey() {
        Properties props = new SQLiteMCConfig.Builder().withKey("1234").build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.HEXKEY_MODE.getPragmaName())).isEqualTo("NONE");
        assertThat(props.get(SQLiteConfig.Pragma.PASSWORD.getPragmaName())).isEqualTo("1234");
        assertThat(props.get(SQLiteConfig.Pragma.KEY.getPragmaName())).isEqualTo("1234");
    }

    @Test
    void toProperties() {
        Properties props = new SQLiteMCConfig.Builder().build().toProperties();
        assertThat(props.get(SQLiteConfigFactory.CONFIG_CLASS_NAME))
                .isEqualTo("org.sqlite.mc.SQLiteMCConfig");
    }
}
