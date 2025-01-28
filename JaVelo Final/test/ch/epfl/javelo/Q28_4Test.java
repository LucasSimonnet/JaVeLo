package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Q28_4Test {

    @Test
    void asDoubleWorksOnKnownValues() {
        assertEquals(-6.25, Q28_4.asDouble(0b11111111111111111111111110011100));
        assertEquals(8.1875, Q28_4.asDouble(0b00000000000000000000000010000011));
    }

    @Test
    void ofIntWorksOnKnownValues() {
        assertEquals(0b11111111111111111111111110100000, Q28_4.ofInt(-6));
        assertEquals(0b01000000000000000000000000000000, Q28_4.ofInt(67108864));
    }

    @Test
    void asFloatWorksOnKnownValues() {
        assertEquals(-6.25, Q28_4.asFloat(0b11111111111111111111111110011100));
    }

}

