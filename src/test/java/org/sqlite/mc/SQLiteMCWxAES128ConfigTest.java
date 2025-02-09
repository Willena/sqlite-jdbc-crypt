package org.sqlite.mc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;

class SQLiteMCWxAES128ConfigTest {

    @Test
    void setLegacy() {
        SQLiteMCWxAES128Config config = new SQLiteMCWxAES128Config();
        assertThatThrownBy(() -> config.setLegacy(3)).isInstanceOf(IllegalArgumentException.class);

        Properties props = config.setLegacy(1).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY.pragmaName)).isEqualTo("1");
    }

    @Test
    void setLegacyPageSize() {
        SQLiteMCWxAES128Config config = new SQLiteMCWxAES128Config();
        assertThatThrownBy(() -> config.setLegacy(-3)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> config.setLegacy(3)).isInstanceOf(IllegalArgumentException.class);

        Properties props = config.setLegacyPageSize(2).build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY_PAGE_SIZE.pragmaName)).isEqualTo("2");
    }

    @Test
    void getDefault() {
        Properties props = SQLiteMCWxAES128Config.getDefault().build().toProperties();

        assertThat(props.get(SQLiteConfig.Pragma.LEGACY.pragmaName)).isEqualTo("0");
        assertThat(props.get(SQLiteConfig.Pragma.LEGACY_PAGE_SIZE.pragmaName)).isEqualTo("0");
        assertThat(props.get(SQLiteConfig.Pragma.CIPHER.pragmaName)).isEqualTo("aes128cbc");
    }
}
