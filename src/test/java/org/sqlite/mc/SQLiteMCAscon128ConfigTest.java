package org.sqlite.mc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;

class SQLiteMCAscon128ConfigTest {

    @Test
    void setKdfIter() {
        SQLiteMCAscon128Config config = new SQLiteMCAscon128Config();
        assertThatThrownBy(() -> config.setKdfIter(0)).isInstanceOf(IllegalArgumentException.class);

        config.setKdfIter(2);

        Properties props = config.build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.KDF_ITER.pragmaName)).isEqualTo("2");
    }

    @Test
    void getDefault() {
        SQLiteMCAscon128Config config = SQLiteMCAscon128Config.getDefault();
        Properties props = config.build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.KDF_ITER.pragmaName)).isEqualTo("64007");
        assertThat(props.get(SQLiteConfig.Pragma.CIPHER.pragmaName)).isEqualTo("ascon128");
    }
}
