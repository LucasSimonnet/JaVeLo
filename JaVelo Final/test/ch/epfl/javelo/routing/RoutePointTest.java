package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.random.RandomGenerator;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RoutePointTest {

        RoutePoint p1 = new RoutePoint(new PointCh(SwissBounds.MIN_E + 10000.2, SwissBounds.MIN_N + 10000.2), 0,
                15);
        RoutePoint p2 = new RoutePoint(new PointCh(SwissBounds.MIN_E + 256, SwissBounds.MIN_N + 364.8), 160,
                12);


        @Test
        void withPositionShiftedByWorks(){
            Assertions.assertEquals(23.6, p1.withPositionShiftedBy(23.6).position());
            Assertions.assertEquals(113, p2.withPositionShiftedBy(-47).position());

        }

        @Test
        void minTest(){
            Assertions.assertEquals(SwissBounds.MIN_E + 256, p1.min(p2).point().e());
            Assertions.assertEquals(SwissBounds.MIN_N + 100000, p2.min(new PointCh(SwissBounds.MIN_E,
                    SwissBounds.MIN_N + 100000), 3, 3).point().n());
            Assertions.assertEquals(SwissBounds.MIN_E + 10000.2, p1.min(new PointCh(SwissBounds.MIN_E,
                    SwissBounds.MIN_N + 100000), 3, 33).point().e());
            Assertions.assertEquals(SwissBounds.MIN_N + 364.8, p2.min(p1).point().n());
        }

    @Test
    void routePointNoneIsDefinedCorrectly() {
        assertNull(RoutePoint.NONE.point());
        assertTrue(Double.isNaN(RoutePoint.NONE.position()));
        Assertions.assertEquals(Double.POSITIVE_INFINITY, RoutePoint.NONE.distanceToReference());
    }

    @Test
    void routePointWithPositionShiftedShiftsPositionAndNothingElse() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var e = 2_600_000 + rng.nextDouble(-50_000, 50_000);
            var n = 1_200_000 + rng.nextDouble(-50_000, 50_000);
            var pointCh = new PointCh(e, n);
            var position = rng.nextDouble(0, 200_000);
            var distanceToReference = rng.nextDouble(0, 1_000);
            var routePoint = new RoutePoint(pointCh, position, distanceToReference);
            var positionShift = rng.nextDouble(-position, 200_000);
            var routePointShifted = routePoint.withPositionShiftedBy(positionShift);
            Assertions.assertEquals(pointCh, routePointShifted.point());
            Assertions.assertEquals(distanceToReference, routePointShifted.distanceToReference());
            Assertions.assertEquals(position + positionShift, routePointShifted.position());
        }
    }

    @Test
    void routePointMin1Works() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var point1 = randomRoutePoint(rng);
            var point2 = randomRoutePoint(rng);
            if (point1.distanceToReference() < point2.distanceToReference()) {
                Assertions.assertEquals(point1, point1.min(point2));
                Assertions.assertEquals(point1, point2.min(point1));
            } else if (point2.distanceToReference() < point1.distanceToReference()) {
                Assertions.assertEquals(point2, point1.min(point2));
                Assertions.assertEquals(point2, point2.min(point1));
            }
        }
    }

    @Test
    void routePointMin2Works() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var point1 = randomRoutePoint(rng);
            var point2 = randomRoutePoint(rng);
            if (point1.distanceToReference() < point2.distanceToReference()) {
                Assertions.assertEquals(point1, point1.min(point2.point(), point2.position(), point2.distanceToReference()));
                assertNotEquals(point2, point2.min(point1.point(), point1.position(), point1.distanceToReference()));
            } else if (point2.distanceToReference() < point1.distanceToReference()) {
                Assertions.assertEquals(point2, point1.min(point2.point(), point2.position(), point2.distanceToReference()));
                assertNotEquals(point1, point2.min(point1.point(), point1.position(), point2.distanceToReference()));
            }
        }
    }

    private RoutePoint randomRoutePoint(RandomGenerator rng) {
        var e = 2_600_000 + rng.nextDouble(-50_000, 50_000);
        var n = 1_200_000 + rng.nextDouble(-50_000, 50_000);
        var pointCh = new PointCh(e, n);
        var position = rng.nextDouble(0, 200_000);
        var distanceToReference = rng.nextDouble(0, 1_000);
        return new RoutePoint(pointCh, position, distanceToReference);
    }

}
