package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphNodesTest {

    IntBuffer buffer = IntBuffer.wrap(new int[]{12,87,0b000100000000100001000100000000100, 4,24,0b01100011110000001000100000000000, 18,98,0b00000000000001110000000000000000, 7,64,0b01000000010000100000000100001000});
    GraphNodes noeuds = new GraphNodes(buffer);

    IntBuffer buffer1 = IntBuffer.wrap(new int[]{90,76,0b00100000100001000010000100000101, 45,38,0b00010001000000111000011000000100, 32,2398,0b00001000000010100010001000000000, 98,53,0b00010000000100001000100001001010, 65,76,0b00010000010000100000100001000100, 25,32,0b01000001001000001000100100100100, 176,23,0b10000000000000100000000000001000});
    GraphNodes noeuds1 = new GraphNodes(buffer1);

    IntBuffer buffer2 = IntBuffer.wrap(new int[]{88,32,0b00100000010000001000000001000000, 55,76,0b10000010000000100000000100000100, 166,28,0b10100000100000000001000000000000, 43,34,0b11110000001111111000000011000000, 78,2863,0b00010000000010000000100000001000, 24,22,0b00000100000000000000000000000000, 64,24,0b00000001000000000000000000000000, 78,3280746,0b11111111111111111111111111111111, 16,11,0b11111111111111111111111111111111, 9,15,0b00000000000000000000000000000000});
    GraphNodes noeuds2 = new GraphNodes(buffer2);

    IntBuffer buffer3 = IntBuffer.wrap(new int[]{});
    GraphNodes noeuds3 = new GraphNodes(buffer3);

    @Test
    void countWorks() {

        assertEquals(4,noeuds.count());

        assertEquals(7, noeuds1.count());

        assertEquals(10, noeuds2.count());

        assertEquals (0, noeuds3.count());
    }

    /*
    @Test
     void nodeEWorks() {
       assertEquals(4, noeuds.nodeE(1));
       assertEquals(43, noeuds2.nodeE(3));
       assertEquals(45, noeuds1.nodeE(1));
       assertEquals(24, noeuds2.nodeE(5));
    }

    @Test
    void nodeNWorks() {
        assertEquals(32, noeuds2.nodeN(0));
        assertEquals(64, noeuds.nodeN(3));
        assertEquals(3280746, noeuds2.nodeN(7));
        assertEquals(23, noeuds1.nodeN(6));
    }

     */

    @Test
    void outDegreeWorks() {
        assertEquals(0b1111, noeuds2.outDegree(7));
        assertEquals(0b0000, noeuds.outDegree(2));
        assertEquals(0b0010, noeuds2.outDegree(0));
        assertEquals(0b0001, noeuds1.outDegree(4));
    }

    @Test
    void edgeIdWorks() {
        assertEquals(0b0000100001000010000100000110, noeuds1.edgeId(0,1));
        assertEquals(0b10000000000000000000000000010, noeuds2.edgeId(8, 3));
        assertEquals(0b0000010000001000000001000001, noeuds2.edgeId(0, 1));
        assertEquals(0b0011110000001000100000000100, noeuds.edgeId(1, 4));
    }

    @Test
    void GraphNodesWorks() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }

    @Test
    void graphNodesWorksOnGivenExample() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }

    @Test
    void graphNodesCountWorksFrom0To99() {
        for (int count = 0; count < 100; count += 1) {
            var buffer = IntBuffer.allocate(3 * count);
            var graphNodes = new GraphNodes(buffer);
            assertEquals(count, graphNodes.count());
        }
    }

    @Test
    void graphNodesENWorkOnRandomCoordinates() {
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
            assertEquals(e, graphNodes.nodeE(nodeId));
            assertEquals(n, graphNodes.nodeN(nodeId));
        }
    }

    @Test
    void graphNodesOutDegreeWorks() {
        var nodesCount = 10_000;
        var buffer = IntBuffer.allocate(3 * nodesCount);
        var rng = newRandom();
        for (int outDegree = 0; outDegree < 16; outDegree += 1) {
            var firstEdgeId = rng.nextInt(1 << 28);
            var nodeId = rng.nextInt(nodesCount);
            buffer.put(3 * nodeId + 2, (outDegree << 28) | firstEdgeId);
            var graphNodes = new GraphNodes(buffer);
            assertEquals(outDegree, graphNodes.outDegree(nodeId));
        }
    }

    @Test
    void graphNodesEdgeIdWorksOnRandomValues() {
        var nodesCount = 10_000;
        var buffer = IntBuffer.allocate(3 * nodesCount);
        var rng = newRandom();
        for (int outDegree = 0; outDegree < 16; outDegree += 1) {
            var firstEdgeId = rng.nextInt(1 << 28);
            var nodeId = rng.nextInt(nodesCount);
            buffer.put(3 * nodeId + 2, (outDegree << 28) | firstEdgeId);
            var graphNodes = new GraphNodes(buffer);
            for (int i = 0; i < outDegree; i += 1)
                assertEquals(firstEdgeId + i, graphNodes.edgeId(nodeId, i));
        }
    }
}
