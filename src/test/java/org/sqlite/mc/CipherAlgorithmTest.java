package org.sqlite.mc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CipherAlgorithmTest {

    @Test
    void getValue() {
        assertThat(CipherAlgorithm.AEGIS.getValue()).isEqualTo("aegis");
    }
}