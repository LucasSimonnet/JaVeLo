package ch.epfl.javelo.data;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttributeSetTest {
    AttributeSet att = AttributeSet.of(Attribute.ONEWAY_BICYCLE_YES,Attribute.HIGHWAY_TRACK,Attribute.BICYCLE_YES,Attribute.TRACKTYPE_GRADE1,Attribute.SURFACE_GRASS, Attribute.SURFACE_ASPHALT, Attribute.HIGHWAY_PRIMARY);
    AttributeSet att1 = AttributeSet.of(Attribute.ONEWAY_BICYCLE_YES,Attribute.HIGHWAY_TRACK,Attribute.BICYCLE_YES);
    AttributeSet att2 = AttributeSet.of(Attribute.TRACKTYPE_GRADE1,Attribute.SURFACE_GRASS, Attribute.SURFACE_ASPHALT, Attribute.HIGHWAY_PRIMARY);
    AttributeSet att3 = AttributeSet.of(Attribute.ONEWAY_BICYCLE_YES,Attribute.HIGHWAY_TRACK,Attribute.BICYCLE_YES,Attribute.TRACKTYPE_GRADE1,Attribute.SURFACE_GRASS, Attribute.SURFACE_ASPHALT, Attribute.HIGHWAY_PRIMARY);
    AttributeSet att4 = AttributeSet.of(Attribute.HIGHWAY_TRACK,Attribute.HIGHWAY_TRACK,Attribute.BICYCLE_YES,Attribute.TRACKTYPE_GRADE1,Attribute.SURFACE_GRASS, Attribute.SURFACE_ASPHALT, Attribute.HIGHWAY_PRIMARY);
    AttributeSet att5 = AttributeSet.of(Attribute.ONEWAY_BICYCLE_YES);

    @Test
    void attributeSetConstructorThrowsOnInvalidBits() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet(0b100000000000000000000000000000000000000000000000000000000000000L);
        });
        assertDoesNotThrow(() -> {
            new AttributeSet(0b10000000000000000000000000000000000000000000000000000000000000L);
        });
    }

    @Test
    void ofWorksOnKnownValues() {
        Assertions.assertEquals(new AttributeSet(0b10000000001000000000010000000001000000010000100000001000000010L), AttributeSet.of(Attribute.LCN_YES,Attribute.ONEWAY_BICYCLE_YES,Attribute.HIGHWAY_TRACK,Attribute.BICYCLE_YES,Attribute.TRACKTYPE_GRADE1,Attribute.SURFACE_GRASS, Attribute.SURFACE_ASPHALT, Attribute.HIGHWAY_PRIMARY ));
        Assertions.assertEquals(new AttributeSet(0b10000000000000000000000000000000000000000L),AttributeSet.of(Attribute.ONEWAY_BICYCLE_YES,Attribute.ONEWAY_BICYCLE_YES));
        Assertions.assertEquals(new AttributeSet(0b1000000000010000000000000000000000000000000000000010L),AttributeSet.of(Attribute.ONEWAY_BICYCLE_YES,Attribute.HIGHWAY_TRACK,Attribute.BICYCLE_YES));
    }

    @Test
    void containsWorksOnKnownValues() {
        Assertions.assertTrue(att.contains(Attribute.SURFACE_GRASS));
        Assertions.assertTrue(att.contains(Attribute.SURFACE_ASPHALT));
        Assertions.assertFalse(att.contains(Attribute.ACCESS_PERMISSIVE));
        Assertions.assertTrue(att.contains(Attribute.HIGHWAY_PRIMARY));
        Assertions.assertFalse(att5.contains(Attribute.HIGHWAY_PRIMARY));
    }

    @Test
    void intersectsWorksOnKnownValues() {
        Assertions.assertFalse(att1.intersects(att2));
        Assertions.assertTrue(att1.intersects(att3));
        Assertions.assertTrue(att4.intersects(att3));
        Assertions.assertFalse(att5.intersects(att4));
    }

    @Test
    void toStringWorksOnKnownValues() {
        Assertions.assertEquals("{highway=track,highway=primary,tracktype=grade1,surface=asphalt,surface=grass,oneway:bicycle=yes,bicycle=yes}",att.toString());
        Assertions.assertEquals("{oneway:bicycle=yes}",att5.toString());
        Assertions.assertEquals("{highway=track,oneway:bicycle=yes,bicycle=yes}",att1.toString());
    }

    private static final int ATTRIBUTES_COUNT = 62;

    @Test
    void attributeSetConstructorWorksWithAllBitsSet() {
        assertDoesNotThrow(() -> {
            var allValidBits = (1L << ATTRIBUTES_COUNT) - 1;
            new AttributeSet(allValidBits);
        });
    }

    @Test
    void attributeSetConstructorThrowsWithInvalidBitsSet() {
        for (int i = ATTRIBUTES_COUNT; i < Long.SIZE; i += 1) {
            var invalidBits = 1L << i;
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                new AttributeSet(invalidBits);
            });
        }
    }

    @Test
    void attributeSetOfWorksForEmptySet() {
        assertEquals(0L, AttributeSet.of().bits());
    }

    @Test
    void attributeSetOfWorksForFullSet() {
        var allAttributes = AttributeSet.of(Attribute.values());
        assertEquals((1L << ATTRIBUTES_COUNT) - 1, allAttributes.bits());
        assertEquals(ATTRIBUTES_COUNT, Long.bitCount(allAttributes.bits()));
    }

    @Test
    void attributeSetContainsWorksOnRandomSets() {
        var allAttributes = Attribute.values();
        assert allAttributes.length == ATTRIBUTES_COUNT;
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            Collections.shuffle(Arrays.asList(allAttributes), new Random(rng.nextLong()));
            var count = rng.nextInt(ATTRIBUTES_COUNT + 1);
            var attributes = Arrays.copyOf(allAttributes, count);
            var attributeSet = AttributeSet.of(attributes);
            assertEquals(count, Long.bitCount(attributeSet.bits()));
            for (int j = 0; j < count; j += 1)
                assertTrue(attributeSet.contains(allAttributes[j]));
            for (int j = count; j < ATTRIBUTES_COUNT; j += 1)
                assertFalse(attributeSet.contains(allAttributes[j]));
        }
    }

    @Test
    void attributeSetIntersectsWorksOnRandomSets() {
        var allAttributes = Attribute.values();
        assert allAttributes.length == ATTRIBUTES_COUNT;
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            Collections.shuffle(Arrays.asList(allAttributes), new Random(rng.nextLong()));
            var count = rng.nextInt(1, ATTRIBUTES_COUNT + 1);
            var attributes = Arrays.copyOf(allAttributes, count);
            var attributeSet = AttributeSet.of(attributes);
            var attributeSet1 = AttributeSet.of(attributes[0]);
            assertTrue(attributeSet.intersects(attributeSet1));
            assertTrue(attributeSet1.intersects(attributeSet));
        }
    }

    @Test
    void attributeSetIntersectsWorksOnComplementarySets() {
        var rng = newRandom();
        var validBitsMask = (1L << ATTRIBUTES_COUNT) - 1;
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var bits = rng.nextLong();
            var set = new AttributeSet(bits & validBitsMask);
            var setComplement = new AttributeSet(~bits & validBitsMask);
            assertFalse(set.intersects(setComplement));
            assertFalse(setComplement.intersects(set));
            assertTrue(set.intersects(set));
            assertTrue(setComplement.intersects(setComplement));
        }
    }

    @Test
    void attributeSetToStringWorksOnKnownValues() {
        assertEquals("{}", new AttributeSet(0).toString());

        for (var attribute : Attribute.values()) {
            var expected = "{" + attribute + "}";
            assertEquals(expected, AttributeSet.of(attribute).toString());
        }

        AttributeSet set = AttributeSet.of(Attribute.TRACKTYPE_GRADE1, Attribute.HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());
    }

}
