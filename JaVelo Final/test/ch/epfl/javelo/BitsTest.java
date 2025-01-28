package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BitsTest {

    @Test
    void bitsExtractThrowsWithInvalidStart() {
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0, -1, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0, Integer.SIZE, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0, -1, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0, Integer.SIZE, 1);
        });
    }

    @Test
    void bitsExtractThrowsWithInvalidLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0, 10, -1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0, 0, Integer.SIZE);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0, 10, -1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0, 0, Integer.SIZE + 1);
        });
    }

    @Test
    void bitsExtractWorksOnFullLength() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var v = rng.nextInt();
            assertEquals(v, Bits.extractSigned(v, 0, Integer.SIZE));
        }
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var v = 1 + rng.nextInt(-1, Integer.MAX_VALUE);
            assertEquals(v, Bits.extractUnsigned(v, 0, Integer.SIZE - 1));
        }
    }

    @Test
    void bitsExtractWorksOnRandomValues() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var value = rng.nextInt();
            var start = rng.nextInt(0, Integer.SIZE - 1);
            var length = rng.nextInt(1, Integer.SIZE - start);

            var expectedU = (value >> start) & (1 << length) - 1;
            var mask = 1 << (length - 1);
            var expectedS = (expectedU ^ mask) - mask;
            assertEquals(expectedU, Bits.extractUnsigned(value, start, length));
            assertEquals(expectedS, Bits.extractSigned(value, start, length));
        }
    }

    @Test
    void extractSignedWorksOnKnownValues() {
        assertEquals(0b00000000000000000000000000000000,Bits.extractSigned(0b11001100110011001100110011001110,0,1));
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001100110011001100110011001100,29,4);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001100110011001100110011001100,30,-8);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001100110011001100110011001100,1,34);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001100110011001100110011001100,-2,3);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001100110011001100110011001100,33,3);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001100110011001100110011001100,15,18);
        });
    }

    @Test
    void extractUnsignedWorksOnKnownValues() {
        //assertEquals(0b00000000000000000000000000001100, Bits.extractUnsigned(0b11001100110011001100110011001100, 28, 4));
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001100110011001100110011001100, -3, 3);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001100110011001100110011001100, 16, 32);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001100110011001100110011001100, 17, 16);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001100110011001100110011001100, 16, -5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0b11001100110011001100110011001100, 58, 16);
        });

    }
}


