package org.sqlite.mc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AegisAlgorithmTest {

    @Test
    void getValue() {
        assertThat(AegisAlgorithm.AEGIS_256X4.getStringValue()).isEqualTo("aegis-256x4");
        assertThat(AegisAlgorithm.AEGIS_256X4.getInValue()).isEqualTo(6);
    }
}