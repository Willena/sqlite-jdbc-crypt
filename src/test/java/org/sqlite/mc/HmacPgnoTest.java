package org.sqlite.mc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HmacPgnoTest {

    @Test
    void getValue() {
        assertThat(HmacPgno.LITTLE_ENDIAN.getValue()).isEqualTo(1);
    }
}