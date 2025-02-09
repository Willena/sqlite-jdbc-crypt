package org.sqlite.mc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HmacAlgorithmTest {

    @Test
    void getValue() {
        assertThat(HmacAlgorithm.SHA256.getValue()).isEqualTo(1);
    }
}