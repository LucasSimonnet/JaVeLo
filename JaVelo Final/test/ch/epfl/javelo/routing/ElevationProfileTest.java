package ch.epfl.javelo.routing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.DoubleSummaryStatistics;
import java.util.random.RandomGenerator;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevationProfileTest {
    float[] samples = new float[] {1000,1015,1030,1045,1060};
    float[] samples1 = new float[] {1000};
    float[] samples2 = new float[] {1000,1015};
    float[] samples3 = new float[] {2100,2000,1900,1800,1700,1600};
    float[] samples4 = new float[] {1000,1015,1030,1015,1000};
    ElevationProfile profile = new ElevationProfile(1,samples);
    ElevationProfile profile1 = new ElevationProfile(1.5,samples2);
    ElevationProfile profile2 = new ElevationProfile(0.1,samples3);
    ElevationProfile profile3 = new ElevationProfile(1500,samples);
    ElevationProfile profile4 = new ElevationProfile(30000,samples4);

    @Test
    public void ConstructorThrowsOnInvalidValues() {

        assertThrows(IllegalArgumentException.class ,() -> {
            new ElevationProfile(-1.5,samples);
        });

        assertThrows(IllegalArgumentException.class ,() -> {
            new ElevationProfile(1,samples1);
        });

        assertDoesNotThrow(() -> {
            new ElevationProfile(1.5,samples2);
        });

        assertDoesNotThrow(() -> {
            new ElevationProfile(0.1,samples3);
        });

        assertThrows(IllegalArgumentException.class ,() -> {
            new ElevationProfile(0,samples4);
        });

    }

    @Test
    void lengthWorksOnKnownValues() {
        assertEquals(1500,profile3.length());
        assertEquals(1.5,profile1.length());
        assertEquals(1,profile.length());
        assertEquals(30000,profile4.length());
        assertEquals(0.1,profile2.length());
    }

    @Test
    void elevationWorksOnKnownValues() {
        assertEquals(1060,profile3.maxElevation());
        assertEquals(1015,profile1.maxElevation());
        assertEquals(1000,profile.minElevation());
        assertEquals(1000,profile4.minElevation());
        assertEquals(2100,profile2.maxElevation());
        assertEquals(1600,profile2.minElevation());

    }

    @Test
    void ascentWorksOnKnownValues() {
        assertEquals(60,profile.totalAscent());
        assertEquals(15,profile1.totalAscent());
        assertEquals(0,profile2.totalAscent());
        assertEquals(30,profile4.totalAscent());
    }

    @Test
    void descentWorksOnKnownValues() {
        assertEquals(0,profile.totalDescent());
        assertEquals(0,profile1.totalDescent());
        assertEquals(500,profile2.totalDescent());
        assertEquals(30,profile4.totalDescent());
    }

    @Test
    void elevationAtWorksOnKnownValues() {
        assertEquals(1030,profile.elevationAt(0.5));
        assertEquals(1003,profile1.elevationAt(0.3));
        assertEquals(1600,profile2.elevationAt(299999));
        assertEquals(1015,profile3.elevationAt(375));
        assertEquals(1000,profile4.elevationAt(-6000));

    }

    @Test
    void elevationProfileConstructorThrowsWithNotEnoughSamples() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(1, new float[0]);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(1, new float[]{3.14f});
        });
    }

    @Test
    void elevationProfileConstructorThrowsWithZeroLength() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(0, new float[]{1, 2, 3});
        });
    }

    @Test
    void elevationProfileLengthReturnsLength() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var length = Math.nextUp(rng.nextDouble(1000));
            var profile = new ElevationProfile(length, new float[]{1, 2, 3});
            assertEquals(length, profile.length());
        }
    }

    private static float[] randomSamples(RandomGenerator rng, int count) {
        var samples = new float[count];
        for (int i = 0; i < count; i += 1)
            samples[i] = rng.nextFloat(4096);
        return samples;
    }

    @Test
    void elevationProfileMinElevationReturnsMinElevation() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var sampleCount = rng.nextInt(2, 1000);
            var elevationSamples = randomSamples(rng, sampleCount);
            var elevationStatistics = new DoubleSummaryStatistics();
            for (var s : elevationSamples) elevationStatistics.accept(s);
            var profile = new ElevationProfile(1000, elevationSamples);
            assertEquals(elevationStatistics.getMin(), profile.minElevation());
        }
    }

    @Test
    void elevationProfileMaxElevationReturnsMaxElevation() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var sampleCount = rng.nextInt(2, 1000);
            var elevationSamples = randomSamples(rng, sampleCount);
            var elevationStatistics = new DoubleSummaryStatistics();
            for (var s : elevationSamples) elevationStatistics.accept(s);
            var profile = new ElevationProfile(1000, elevationSamples);
            assertEquals(elevationStatistics.getMax(), profile.maxElevation());
        }
    }

    @Test
    void elevationProfileTotalAscentReturnsTotalAscent() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var sampleCount = rng.nextInt(2, 1000);
            var elevationSamples = randomSamples(rng, sampleCount);
            var totalAscent = 0d;
            for (int j = 1; j < sampleCount; j += 1) {
                var d = elevationSamples[j] - elevationSamples[j - 1];
                if (d > 0) totalAscent += d;
            }
            var profile = new ElevationProfile(1000, elevationSamples);
            assertEquals(totalAscent, profile.totalAscent());
        }
    }

    @Test
    void elevationProfileTotalDescentReturnsTotalDescent() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var sampleCount = rng.nextInt(2, 1000);
            var elevationSamples = randomSamples(rng, sampleCount);
            var totalDescent = 0d;
            for (int j = 1; j < sampleCount; j += 1) {
                var d = elevationSamples[j] - elevationSamples[j - 1];
                if (d < 0) totalDescent -= d;
            }
            var profile = new ElevationProfile(1000, elevationSamples);
            assertEquals(totalDescent, profile.totalDescent());
        }
    }

    @Test
    void elevationProfileElevationAtWorksOnKnownValues() {
        var samples = new float[]{
                100.00f, 123.25f, 375.50f, 212.75f, 220.00f, 210.25f
        };
        var profile = new ElevationProfile(1000, samples);

        var actual1 = profile.elevationAt(0);
        var expected1 = 100.0;
        assertEquals(expected1, actual1);

        var actual2 = profile.elevationAt(200);
        var expected2 = 123.25;
        assertEquals(expected2, actual2);

        var actual3 = profile.elevationAt(400);
        var expected3 = 375.5;
        assertEquals(expected3, actual3);

        var actual4 = profile.elevationAt(600);
        var expected4 = 212.75;
        assertEquals(expected4, actual4);

        var actual5 = profile.elevationAt(800);
        var expected5 = 220.0;
        assertEquals(expected5, actual5);

        var actual6 = profile.elevationAt(1000);
        var expected6 = 210.25;
        assertEquals(expected6, actual6);

        var actual7 = profile.elevationAt(500);
        var expected7 = 294.125;
        assertEquals(expected7, actual7);
    }

}
