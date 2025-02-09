package org.sqlite.mc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HmacPgnoTest {

    @Test
    void getValue() {
        assertThat(HmacPgno.LITTLE_ENDIAN.getValue()).isEqualTo(1);
    }
}
