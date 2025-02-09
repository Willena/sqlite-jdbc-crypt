package org.sqlite.mc;

import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteConfig;

import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class SQLiteMCAegisConfigTest {

    @Test
    void setMCost() {
        SQLiteMCAegisConfig aegisConfig = new SQLiteMCAegisConfig();
        assertThatThrownBy( () -> aegisConfig.setMCost(0)).isInstanceOf(IllegalArgumentException.class);
        aegisConfig.setMCost(2);

        Properties props = aegisConfig.build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.MCOST.pragmaName)).isEqualTo("2");
    }

    @Test
    void setPCost() {
        SQLiteMCAegisConfig aegisConfig = new SQLiteMCAegisConfig();
        assertThatThrownBy( () -> aegisConfig.setPCost(0)).isInstanceOf(IllegalArgumentException.class);
        aegisConfig.setPCost(2);

        Properties props = aegisConfig.build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.PCOST.pragmaName)).isEqualTo("2");
    }

    @Test
    void setTCost() {
        SQLiteMCAegisConfig aegisConfig = new SQLiteMCAegisConfig();
        assertThatThrownBy( () -> aegisConfig.setTCost(0)).isInstanceOf(IllegalArgumentException.class);
        aegisConfig.setTCost(2);

        Properties props = aegisConfig.build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.TCOST.pragmaName)).isEqualTo("2");
    }

    @Test
    void setAegisAlgorithm() {
        SQLiteMCAegisConfig aegisConfig = new SQLiteMCAegisConfig();
        aegisConfig.setAegisAlgorithm(AegisAlgorithm.AEGIS_256X2);

        Properties props = aegisConfig.build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.ALGORITHM.pragmaName)).isEqualTo(AegisAlgorithm.AEGIS_256X2.getStringValue());
    }

    @Test
    void getDefault() {
        SQLiteMCAegisConfig defaultConfig = SQLiteMCAegisConfig.getDefault();
        Properties props = defaultConfig.build().toProperties();
        assertThat(props.get(SQLiteConfig.Pragma.ALGORITHM.pragmaName)).isEqualTo(AegisAlgorithm.AEGIS_256.getStringValue());
        assertThat(props.get(SQLiteConfig.Pragma.TCOST.pragmaName)).isEqualTo("2");
        assertThat(props.get(SQLiteConfig.Pragma.MCOST.pragmaName)).isEqualTo("19456");
        assertThat(props.get(SQLiteConfig.Pragma.PCOST.pragmaName)).isEqualTo("1");

        assertThat(props.get(SQLiteConfig.Pragma.CIPHER.pragmaName)).isEqualTo("aegis");
    }
}