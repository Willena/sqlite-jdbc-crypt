package org.sqlite.mc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CipherAlgorithmTest {

    @Test
    void getValue() {
        assertThat(CipherAlgorithm.AEGIS.getValue()).isEqualTo("aegis");
    }
}
