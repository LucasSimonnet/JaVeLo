package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EdgeTest {

    Edge edge = new Edge(1, 2, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
            new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100), 140,
            Functions.sampled(new float[]{0f, 1f, 2f, 1f, 3f, 2f, 4f, 3f, 0.5f, 1f, 2f, 3f, 10f, 12f, 5f}, 140));

    @Test
    void closestToWorks() {
        Assertions.assertEquals(70, edge.positionClosestTo(new PointCh(SwissBounds.MIN_E + 30, SwissBounds.MIN_N + 70)), 1);
        Assertions.assertEquals(100, edge.positionClosestTo(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 141)), 1);
    }

    @Test
    void PointAtWorks() {
        PointCh point = edge.pointAt(113.137084);
        Assertions.assertEquals(SwissBounds.MIN_E + 80, point.e(), 1);
        Assertions.assertEquals(SwissBounds.MIN_N + 80, point.n(), 1);
        PointCh p = edge.pointAt(84.8528);
        Assertions.assertEquals(SwissBounds.MIN_E + 60, p.e(), 1);
        Assertions.assertEquals(SwissBounds.MIN_N + 60, p.n(), 1);
    }

    @Test
    void ELevationAtTest() {
        Assertions.assertEquals(0, edge.elevationAt(-1));
        Assertions.assertEquals(0, edge.elevationAt(0));
        Assertions.assertEquals(1f, edge.elevationAt(10));
        Assertions.assertEquals(4f, edge.elevationAt(60));
        Assertions.assertEquals(10f, edge.elevationAt(120));
        Assertions.assertEquals(3f, edge.elevationAt(55));
        Assertions.assertEquals(5f, edge.elevationAt(140));
    }

    private static final double DELTA = 1e-7;


    @Test
    void edgeOfWorksOnLausanneData() throws IOException {
        var graph = Graph.loadFrom(Path.of("lausanne"));

        var edge = Edge.of(graph, 4095, 2022, 2021);

        var actual1 = edge.fromPoint().e();
        var expected1 = 2535880.25;
        assertEquals(expected1, actual1);

        var actual2 = edge.fromPoint().n();
        var expected2 = 1164939.125;
        assertEquals(expected2, actual2);

        var actual3 = edge.toPoint().e();
        var expected3 = 2535904.5625;
        assertEquals(expected3, actual3);

        var actual4 = edge.toPoint().n();
        var expected4 = 1164911.3125;
        assertEquals(expected4, actual4);

        var actual5 = edge.length();
        var expected5 = 36.9375;
        assertEquals(expected5, actual5);
    }

    @Test
    void edgePositionClosestToWorksOnKnownValues() {
        var p1 = new PointCh(2600123, 1200456);
        var p2 = new PointCh(2600456, 1200789);
        var p3 = new PointCh(2600789, 1200123);
        var p4 = new PointCh(2601000, 1201000);

        var edge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), x -> Double.NaN);
        var edge2 = new Edge(3, 4, p3, p4, p3.distanceTo(p4), x -> Double.NaN);

        var actual1 = edge1.positionClosestTo(p3);
        var expected1 = 235.46655813512032;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = edge1.positionClosestTo(p4);
        System.out.println(edge1.length());
        var expected2 = 1004.7987360660841;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = edge2.positionClosestTo(p1);
        var expected3 = 167.971970023863;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = edge2.positionClosestTo(p2);
        var expected4 = 569.6280214215838;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = edge2.positionClosestTo(p3);
        var expected5 = 0.0;
        assertEquals(expected5, actual5, DELTA);

        var actual6 = edge2.positionClosestTo(p4);
        var expected6 = 902.025498530945;
        assertEquals(expected6, actual6, DELTA);
    }

    @Test
    void edgePointAtWorksBeyondOfEdgeLength() {
        var rng = newRandom();

        // Horizontal edges
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var e1 = rng.nextDouble(2_500_000, 2_600_000);
            var e2 = rng.nextDouble(2_600_000, 2_800_000);
            var n1 = rng.nextDouble(1_100_000, 1_200_000);

            var fPH = new PointCh(e1, n1);
            var tPH = new PointCh(e2, n1);
            var edgeLength = e2 - e1;
            var edge = new Edge(0, 1, fPH, tPH, edgeLength, d -> 100);

            assertEquals(new PointCh(e1 - 1, n1), edge.pointAt(-1));
            assertEquals(new PointCh(e2 + 1, n1), edge.pointAt(edgeLength + 1));
        }

        // Vertical edges
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var e1 = rng.nextDouble(2_500_000, 2_600_000);
            var e2 = rng.nextDouble(2_600_000, 2_800_000);
            var n1 = rng.nextDouble(1_100_000, 1_200_000);

            var fPH = new PointCh(e1, n1);
            var tPH = new PointCh(e2, n1);
            var edgeLength = e2 - e1;
            var edge = new Edge(0, 1, fPH, tPH, edgeLength, d -> 100);

            assertEquals(new PointCh(e1 - 1, n1), edge.pointAt(-1));
            assertEquals(new PointCh(e2 + 1, n1), edge.pointAt(edgeLength + 1));
        }
    }

    @Test
    void edgePointAtWorksWithEndAndMiddlePoints() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var dE = rng.nextDouble(-50_000, +50_000);
            var dN = rng.nextDouble(-50_000, +50_000);

            var mP = new PointCh(2_600_000, 1_200_000);
            var fP = new PointCh(mP.e() + dE, mP.n() + dN);
            var tP = new PointCh(mP.e() - dE, mP.n() - dN);
            var edgeLength = Math.hypot(dE, dN);
            var edge = new Edge(0, 1, fP, tP, edgeLength, d -> Double.NaN);
            assertEquals(fP, edge.pointAt(0));
            assertEquals(mP, edge.pointAt(Math.scalb(edgeLength, -1)));
            assertEquals(tP, edge.pointAt(edgeLength));
        }
    }

    @Test
    void edgeElevationAtWorksWithConstantProfile() {
        var fP = new PointCh(2_600_000, 1_200_000);
        var tP = new PointCh(2_600_001, 1_200_001);
        var edge = new Edge(0, 1, fP, tP, Math.sqrt(2), d -> Double.NaN);
        for (double x = -20; x <= 20; x += 1)
            assertTrue(Double.isNaN(edge.elevationAt(x)));
    }

    @Test
    void edgeElevationAtWorksWithWavyProfile() {
        var fP = new PointCh(2_600_000, 1_200_000);
        var tP = new PointCh(2_600_001, 1_200_001);
        var edge = new Edge(0, 1, fP, tP, Math.sqrt(2), Math::sin);
        for (double x = -20; x <= 20; x += 1)
            assertEquals(Math.sin(x), edge.elevationAt(x));
    }
}
