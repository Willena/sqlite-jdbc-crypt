package org.sqlite.mc;

import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;

import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class SQLiteMCRC4ConfigTest {

    @Test
    void setLegacy(){
        SQLiteMCRC4Config config = new SQLiteMCRC4Config();
        assertThatThrownBy( () -> config.setLegacy(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy( () -> config.setLegacy(2)).isInstanceOf(IllegalArgumentException.class);
        config.setLegacy(1);
        Properties props = config.build().toProperties();

        assertThat(props.get(SQLiteConfig.Pragma.LEGACY.pragmaName)).isEqualTo( "1");
    }

    @Test
    void setLegacyPageSize(){
        SQLiteMCRC4Config config = new SQLiteMCRC4Config();
        assertThatThrownBy( () -> config.setLegacyPageSize(65537)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy( () -> config.setLegacyPageSize(-1)).isInstanceOf(IllegalArgumentException.class);

        config.setLegacyPageSize(1);
        Properties props = config.build().toProperties();

        assertThat(props.get(SQLiteConfig.Pragma.LEGACY_PAGE_SIZE.pragmaName)).isEqualTo( "1");
    }

    @Test
    void getDefault() {
        Properties props = SQLiteMCRC4Config.getDefault().build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY.pragmaName)).isEqualTo( "1");
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY_PAGE_SIZE.pragmaName)).isEqualTo( "0");

        assertThat(props.get(SQLiteConfig.Pragma.CIPHER.pragmaName)).isEqualTo( "rc4");

    }
}