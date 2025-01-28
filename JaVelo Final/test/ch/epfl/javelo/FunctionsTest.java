package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class FunctionsTest {
    @Test
    void constantWorksOnKnownValues() {
        double y = Functions.constant(8).applyAsDouble(-108);
        assertEquals(8,y);
    }

    @Test
    void sampledWorksOnKnownValues() {
        float [] samples = {15.125f,15.23f,18.74f,21.47f,27.27f,28.31f,15.02f,24.31f,35.75f};
        DoubleUnaryOperator f = Functions.sampled(samples,8);
        assertEquals(samples[0],f.applyAsDouble(0));
        samples[0] = 2;
        assertNotEquals(samples[0],f.applyAsDouble(0));
        assertEquals(15.125,f.applyAsDouble(0));
        assertEquals(24.3293346,Functions.sampled(samples,72.74).applyAsDouble(31.76),1e-6);
        assertEquals(15.125,Functions.sampled(samples,72.74).applyAsDouble(-5));
        assertEquals(35.75,Functions.sampled(samples,72.74).applyAsDouble(89));
        float [] samples1 = {15,16};
        assertEquals(16,Functions.sampled(samples1,18).applyAsDouble(18),1e-6);
    }
}
