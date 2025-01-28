package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.ElevationProfile;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    public void EdgesTest() {
        PointCh a = new PointCh(2549278.75+10000,1166252.3125+10000);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x6_6);
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (1 << 30) | 2
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x17A_8,
                (short) 0x164_C, (short) 0x1540,
                (short) 0x12C0, (short) 0x1260
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

    }

    @Test
    void NodeClosestTo3() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        System.out.println(g.nodeClosestTo(new PointCh(2520000 + 1000, 1145000 + 1000), 15000));
        assertNotEquals(-1, g.nodeClosestTo(new PointCh(2520000 + 1000, 1145000 + 1000), 15000));
    }

    @Test
    void edgesTest(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(20);
        edgesBuffer.putInt(0b00000000000000000000000000000001);//1
        edgesBuffer.putShort(4, (short) 0b0000000000110000);//3
        edgesBuffer.putShort(6, (short)0b0000000000010000);//1
        edgesBuffer.putShort(8, (short) 0b00000000000000000);//9
        // une autre arrête
        edgesBuffer.putInt(10,~13);//13
        edgesBuffer.putShort(14, (short) 0b0000000001100000);//6
        edgesBuffer.putShort(16, (short)0b0000000000110000);//3
        edgesBuffer.putShort(18, (short) 0b00000000000000001);//15

        IntBuffer profileId = IntBuffer.allocate(2);
        profileId.put(0, 0b10000000000000000000000000000000);
        profileId.put(1, 0b00000000000000000000000000000010);


        ShortBuffer elevation = ShortBuffer.allocate(6);
        elevation.put(0,(short) 0);
        elevation.put(1,(short) 0x10F8);
        elevation.put(2,(short)0x0010);
        elevation.put(3,(short)0x0020);
        elevation.put(4,(short)0x0030);
        elevation.put(5,(short)0x0040);

        GraphEdges g = new GraphEdges(edgesBuffer,profileId,elevation);
        List<AttributeSet> attributesSet = new ArrayList<>();
        attributesSet.add(new AttributeSet(1l));
        attributesSet.add(new AttributeSet(0l));
        Graph graph = new Graph(null, null, g, attributesSet);

        assertFalse(graph.edgeIsInverted(0));
        assertTrue(graph.edgeIsInverted(1));

        Assertions.assertEquals(1,graph.edgeTargetNodeId(0));
        Assertions.assertEquals(13, graph.edgeTargetNodeId(1));

        Assertions.assertEquals(3, graph.edgeLength(0));
        Assertions.assertEquals(6, graph.edgeLength(1));

        Assertions.assertEquals(1, graph.edgeElevationGain(0));
        Assertions.assertEquals(3, graph.edgeElevationGain(1));

        Assertions.assertEquals(new AttributeSet(1l), graph.edgeAttributes(0));
        Assertions.assertEquals(new AttributeSet(0l), graph.edgeAttributes(1));

        Assertions.assertEquals(Double.NaN, graph.edgeProfile(1).applyAsDouble(23));
        Assertions.assertEquals(1f, graph.edgeProfile(0).applyAsDouble(1.5));

        assertArrayEquals(new float[] {0f,1f,0.5f}, g.profileSamples(0));
    }

    private static final int SUBDIVISIONS_PER_SIDE = 128;
    private static final int SECTORS_COUNT = SUBDIVISIONS_PER_SIDE * SUBDIVISIONS_PER_SIDE;

    private static final ByteBuffer SECTORS_BUFFER = createSectorsBuffer();

    private static ByteBuffer createSectorsBuffer() {
        ByteBuffer sectorsBuffer = ByteBuffer.allocate(SECTORS_COUNT * (Integer.BYTES + Short.BYTES));
        for (int i = 0; i < SECTORS_COUNT; i += 1) {
            sectorsBuffer.putInt(i);
            sectorsBuffer.putShort((short) 1);
        }
        assert !sectorsBuffer.hasRemaining();
        return sectorsBuffer.rewind().asReadOnlyBuffer();
    }

    @Test
    void graphLoadFromWorksOnLausanneData() throws IOException {
        var graph = Graph.loadFrom(Path.of("lausanne"));

        // Check that nodes.bin was properly loaded
        var actual1 = graph.nodeCount();
        var expected1 = 212679;
        Assertions.assertEquals(expected1, actual1);

        var actual2 = graph.nodeOutEdgeId(2022, 0);
        var expected2 = 4095;
        Assertions.assertEquals(expected2, actual2);

        // Check that edges.bin was properly loaded
        var actual3 = graph.edgeLength(2022);
        var expected3 = 17.875;
        Assertions.assertEquals(expected3, actual3);

        // Check that profile_ids.bin and elevations.bin was properly loaded
        var actual4 = graph.edgeProfile(2022).applyAsDouble(0);
        var expected4 = 625.5625;
        Assertions.assertEquals(expected4, actual4);

        // Check that attributes.bin and elevations.bin was properly loaded
        var actual5 = graph.edgeAttributes(2022).bits();
        var expected5 = 16;
        Assertions.assertEquals(expected5, actual5);
    }

    @Test
    void graphNodeCountWorksFrom0To99() {
        var edgesCount = 10;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
        var attributeSets = List.<AttributeSet>of();

        for (int count = 0; count < 100; count += 1) {
            var buffer = IntBuffer.allocate(3 * count);
            var graphNodes = new GraphNodes(buffer);

            var graph = new Graph(graphNodes, graphSectors, graphEdges, attributeSets);
            Assertions.assertEquals(count, graph.nodeCount());
        }
    }

    @Test
    void graphNodePointWorksOnRandomValues() {
        var edgesCount = 10;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
        var attributeSets = List.<AttributeSet>of();

        var nodesCount = 10_000;
        var buffer = IntBuffer.allocate(3 * nodesCount);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var e = 2_600_000 + 50_000 * rng.nextDouble();
            var n = 1_200_000 + 50_000 * rng.nextDouble();
            var e_q28_4 = (int) Math.scalb(e, 4);
            var n_q28_4 = (int) Math.scalb(n, 4);
            e = Math.scalb((double) e_q28_4, -4);
            n = Math.scalb((double) n_q28_4, -4);
            var nodeId = rng.nextInt(nodesCount);
            buffer.put(3 * nodeId, e_q28_4);
            buffer.put(3 * nodeId + 1, n_q28_4);
            var graphNodes = new GraphNodes(buffer);

            var graph = new Graph(graphNodes, graphSectors, graphEdges, attributeSets);
            Assertions.assertEquals(new PointCh(e, n), graph.nodePoint(nodeId));
        }
    }

    @Test
    void graphNodeOutDegreeWorksOnRandomValues() {
        var edgesCount = 10;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
        var attributeSets = List.<AttributeSet>of();

        var nodesCount = 10_000;
        var buffer = IntBuffer.allocate(3 * nodesCount);
        var rng = newRandom();
        for (int outDegree = 0; outDegree < 16; outDegree += 1) {
            var firstEdgeId = rng.nextInt(1 << 28);
            var nodeId = rng.nextInt(nodesCount);
            buffer.put(3 * nodeId + 2, (outDegree << 28) | firstEdgeId);
            var graphNodes = new GraphNodes(buffer);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, attributeSets);
            Assertions.assertEquals(outDegree, graph.nodeOutDegree(nodeId));
        }
    }

    @Test
    void graphNodeOutEdgeIdWorksOnRandomValues() {
        var edgesCount = 10;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
        var attributeSets = List.<AttributeSet>of();

        var nodesCount = 10_000;
        var buffer = IntBuffer.allocate(3 * nodesCount);
        var rng = newRandom();
        for (int outDegree = 0; outDegree < 16; outDegree += 1) {
            var firstEdgeId = rng.nextInt(1 << 28);
            var nodeId = rng.nextInt(nodesCount);
            buffer.put(3 * nodeId + 2, (outDegree << 28) | firstEdgeId);
            var graphNodes = new GraphNodes(buffer);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, attributeSets);

            for (int i = 0; i < outDegree; i += 1)
                Assertions.assertEquals(firstEdgeId + i, graph.nodeOutEdgeId(nodeId, i));
        }
    }

    @Test
    void graphNodeClosestToWorksOnLausanneData() throws IOException {
        var graph = Graph.loadFrom(Path.of("lausanne"));

        var actual1 = graph.nodeClosestTo(new PointCh(2_532_734.8, 1_152_348.0), 100);
        var expected1 = 159049;
        Assertions.assertEquals(expected1, actual1);

        var actual2 = graph.nodeClosestTo(new PointCh(2_538_619.9, 1_154_088.0), 100);
        var expected2 = 117402;
        Assertions.assertEquals(expected2, actual2);

        var actual3 = graph.nodeClosestTo(new PointCh(2_600_000, 1_200_000), 100);
        var expected3 = -1;
        Assertions.assertEquals(expected3, actual3);
    }

    @Test
    void graphEdgeTargetNodeIdWorksOnRandomValues() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        var attributeSets = List.<AttributeSet>of();

        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var targetNodeId = rng.nextInt();
            var edgeId = rng.nextInt(edgesCount);
            edgesBuffer.putInt(10 * edgeId, targetNodeId);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, attributeSets);
            var expectedTargetNodeId = targetNodeId < 0 ? ~targetNodeId : targetNodeId;
            Assertions.assertEquals(expectedTargetNodeId, graph.edgeTargetNodeId(edgeId));
        }
    }

    @Test
    void graphEdgeIsInvertedWorksForPlusMinus100() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);
        var attributeSets = List.<AttributeSet>of();

        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var rng = newRandom();
        for (int targetNodeId = -100; targetNodeId < 100; targetNodeId += 1) {
            var edgeId = rng.nextInt(edgesCount);
            edgesBuffer.putInt(10 * edgeId, targetNodeId);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, attributeSets);
            Assertions.assertEquals(targetNodeId < 0, graph.edgeIsInverted(edgeId));
        }
    }

    @Test
    void graphEdgeAttributesWorksOnRandomValues() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);

        var attributeSetsCount = 3 * RANDOM_ITERATIONS;
        var rng = newRandom();
        var attributeSets = new ArrayList<AttributeSet>(attributeSetsCount);
        for (int i = 0; i < attributeSetsCount; i += 1) {
            var attributeSetBits = rng.nextLong(1L << 62);
            attributeSets.add(new AttributeSet(attributeSetBits));
        }
        var unmodifiableAttributeSets = Collections.unmodifiableList(attributeSets);

        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var edgeId = rng.nextInt(edgesCount);
            var attributeSetIndex = (short) rng.nextInt(attributeSetsCount);
            edgesBuffer.putShort(10 * edgeId + 8, attributeSetIndex);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, unmodifiableAttributeSets);
            Assertions.assertEquals(
                    unmodifiableAttributeSets.get(attributeSetIndex),
                    graph.edgeAttributes(edgeId));
        }
    }

    @Test
    void graphConstructorCopiesAttributesListToEnsureImmutability() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);

        var attributeSet = new AttributeSet(0b1111L);
        var attributeSets = new ArrayList<>(List.of(attributeSet));
        var unmodifiableAttributeSets = Collections.unmodifiableList(attributeSets);

        var edgesCount = 1;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        edgesBuffer.putShort(8, (short) 0);
        var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
        var graph = new Graph(graphNodes, graphSectors, graphEdges, unmodifiableAttributeSets);
        attributeSets.set(0, new AttributeSet(0L));
        Assertions.assertEquals(attributeSet, graph.edgeAttributes(0));
    }

    @Test
    void graphEdgeLengthWorksOnRandomValues() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);

        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var edgeId = rng.nextInt(edgesCount);
            var length = rng.nextDouble(1 << 12);
            var length_q12_4 = (int) Math.scalb(length, 4);
            length = Math.scalb((double) length_q12_4, -4);
            edgesBuffer.putShort(10 * edgeId + 4, (short) length_q12_4);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, List.of());

            Assertions.assertEquals(length, graph.edgeLength(edgeId));
        }
    }

    @Test
    void graphEdgeElevationGainWorksOnRandomValues() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);

        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var edgeId = rng.nextInt(edgesCount);
            var elevationGain = rng.nextDouble(1 << 12);
            var elevationGain_q12_4 = (int) Math.scalb(elevationGain, 4);
            elevationGain = Math.scalb((double) elevationGain_q12_4, -4);
            edgesBuffer.putShort(10 * edgeId + 6, (short) elevationGain_q12_4);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, List.of());

            Assertions.assertEquals(elevationGain, graph.edgeElevationGain(edgeId));
        }
    }

    @Test
    void graphEdgeProfileWorksForType0() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);

        var edgesCount = 10_000;
        var elevationsCount = 25_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(elevationsCount);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var edgeId = rng.nextInt(edgesCount);
            var firstSampleIndex = rng.nextInt(elevationsCount);
            profileIds.put(edgeId, firstSampleIndex);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            var graph = new Graph(graphNodes, graphSectors, graphEdges, List.of());
            var edgeProfile = graph.edgeProfile(edgeId);
            assertTrue(Double.isNaN(edgeProfile.applyAsDouble(-1)));
            assertTrue(Double.isNaN(edgeProfile.applyAsDouble(0)));
            assertTrue(Double.isNaN(edgeProfile.applyAsDouble(1000)));
        }
    }

    @Test
    void graphEdgeProfileWorksForType1() {
        var nodesCount = 10;
        var nodesBuffer = IntBuffer.allocate(3 * nodesCount);
        var graphNodes = new GraphNodes(nodesBuffer);
        var graphSectors = new GraphSectors(SECTORS_BUFFER);

        var elevationsCount = 500;
        var edgesBuffer = ByteBuffer.allocate(10);
        var profileIds = IntBuffer.allocate(1);
        var elevations = ShortBuffer.allocate(elevationsCount);
        var rng = newRandom();
        for (int i = 0; i < elevationsCount; i += 1)
            elevations.put(i, (short) rng.nextInt(1 << 16));
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var inverted = rng.nextBoolean();
            var sampleCount = rng.nextInt(2, 100);
            var firstSampleIndex = rng.nextInt(elevationsCount - sampleCount);
            var edgeLength_q28_4 = (2 * (sampleCount - 1)) << 4;
            edgesBuffer.putInt(0, inverted ? ~0 : 0);
            edgesBuffer.putShort(4, (short) edgeLength_q28_4);
            profileIds.put(0, (1 << 30) | firstSampleIndex);
            var graphEdges = new GraphEdges(edgesBuffer.asReadOnlyBuffer(), profileIds.asReadOnlyBuffer(), elevations.asReadOnlyBuffer());
            var graph = new Graph(graphNodes, graphSectors, graphEdges, List.of());
            var edgeProfile = graph.edgeProfile(0);

            for (int j = 0; j < sampleCount; j += 1) {
                var elevation = Math.scalb(Short.toUnsignedInt(elevations.get(firstSampleIndex + j)), -4);
                if (inverted) {
                    var x = (sampleCount - 1 - j) * Math.scalb((double)edgeLength_q28_4, -4) / (sampleCount - 1);
                    Assertions.assertEquals(elevation, edgeProfile.applyAsDouble(x), 1e-7);
                }
                else {
                    var x = j * Math.scalb((double)edgeLength_q28_4, -4) / (sampleCount - 1);
                    Assertions.assertEquals(elevation, edgeProfile.applyAsDouble(x), 1e-7);
                }
            }
        }
    }
}
