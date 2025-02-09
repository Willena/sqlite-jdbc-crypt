package org.sqlite.mc;

import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;

import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class SQLiteMCWxAES256ConfigTest {


    @Test
    void setLegacy() {
        SQLiteMCWxAES256Config config = new SQLiteMCWxAES256Config();
        assertThatThrownBy( () -> config.setLegacy(3)).isInstanceOf(IllegalArgumentException.class);

        Properties props = config.setLegacy(1).build().toProperties();
        assertThat( props.get(SQLiteConfig.Pragma.LEGACY.pragmaName)).isEqualTo("1");
    }

    @Test
    void setLegacyPageSize() {
        SQLiteMCWxAES256Config config = new SQLiteMCWxAES256Config();
        assertThatThrownBy( () -> config.setLegacy(-3)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy( () -> config.setLegacy(3)).isInstanceOf(IllegalArgumentException.class);

        Properties props = config.setLegacyPageSize(2).build().toProperties();
        assertThat( props.get(SQLiteConfig.Pragma.LEGACY_PAGE_SIZE.pragmaName)).isEqualTo("2");
    }

 
    @Test
    void setKdfIter() {
        SQLiteMCWxAES256Config config = new SQLiteMCWxAES256Config();
        assertThatThrownBy( () -> config.setLegacy(-3)).isInstanceOf(IllegalArgumentException.class);

        Properties props = config.setKdfIter(2).build().toProperties();
        assertThat( props.get(SQLiteConfig.Pragma.KDF_ITER.pragmaName)).isEqualTo("2");
    }


    @Test
    void getDefault() {
        Properties props = SQLiteMCWxAES256Config.getDefault().build().toProperties();

        assertThat( props.get(SQLiteConfig.Pragma.LEGACY.pragmaName)).isEqualTo("0");
        assertThat( props.get(SQLiteConfig.Pragma.LEGACY_PAGE_SIZE.pragmaName)).isEqualTo("0");
        assertThat( props.get(SQLiteConfig.Pragma.KDF_ITER.pragmaName)).isEqualTo("4001");
        assertThat( props.get(SQLiteConfig.Pragma.CIPHER.pragmaName)).isEqualTo("aes256cbc");

    }
}