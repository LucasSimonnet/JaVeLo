package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;


public class SingleRouteTest{


    static SingleRoute sR;

    static SingleRoute ssR;

    static List<Edge> singleRoute = new ArrayList<>();

    static List<Edge> singleRoute1 = new ArrayList<>();

    static List<Edge> singleRoute2 = new ArrayList<>();

    static Edge edge = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
            new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000), 1414,
            Functions.sampled(new float[]{348f, 357f, 299f, 319f, 328f, 308f, 382f, 333f, 322f, 336f, 319f, 342f, 322f, 312f, 314f}, 1414));

    static Edge leila = new Edge(1, 2, new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000),
            new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N + 1500), 1118,
            Functions.constant(Double.NaN));

    static Edge marc = new Edge(2, 3, new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N + 1500),
            new PointCh(SwissBounds.MIN_E + 2500, SwissBounds.MIN_N), 2549,
            Functions.sampled(new float[]{348f, 357f, 299f, 319f, 328f, 308f, 382f, 333f, 322f, 336f, 319f, 342f, 322f, 312f, 314f}, 2549));

    static Edge antoine = new Edge(3, 4, new PointCh(SwissBounds.MIN_E + 2500, SwissBounds.MIN_N),
            new PointCh(SwissBounds.MIN_E + 5748, SwissBounds.MIN_N + 2757), 4260,
            Functions.sampled(new float[]{348f, 357f, 299f, 319f, 328f, 308f, 382f, 333f, 322f, 336f, 319f, 342f, 322f, 312f, 314f}, 4260));

    static Edge naj = new Edge(4, 5, new PointCh(SwissBounds.MIN_E+5748, SwissBounds.MIN_N+2757),
            new PointCh(SwissBounds.MIN_E + 885, SwissBounds.MIN_N + 4299), 5101,
            Functions.constant(Double.NaN));

    static Edge lucas = new Edge(5, 6, new PointCh(SwissBounds.MIN_E+885, SwissBounds.MIN_N+4299),
            new PointCh(SwissBounds.MIN_E + 1757, SwissBounds.MIN_N + 4966), 1097,
            Functions.sampled(new float[]{348f, 357f, 299f, 319f, 328f, 308f, 382f, 333f, 322f, 336f, 319f, 342f, 322f, 312f, 314f}, 1097));

    static Edge edge1 = new Edge(7, 8, new PointCh(SwissBounds.MIN_E+100000, SwissBounds.MIN_N+100000),
            new PointCh(SwissBounds.MIN_E+105000 , SwissBounds.MIN_N + 105000), Math.sqrt(50000000),
            Functions.sampled(new float[]{348f, 357f, 299f, 319f, 328f, 308f, 382f, 333f, 322f, 336f, 319f, 342f, 322f, 312f, 314f}, Math.sqrt(50000000)));

    static Edge edge2 = new Edge(8, 9, new PointCh(SwissBounds.MIN_E+105000, SwissBounds.MIN_N+105000),
            new PointCh(SwissBounds.MIN_E+105000 , SwissBounds.MIN_N + 100000), 5000,
            Functions.sampled(new float[]{348f, 357f, 299f, 319f, 328f, 308f, 382f, 333f, 322f, 336f, 319f, 342f, 322f, 312f, 314f}, 5000));

    @Test
    void ConstructorThrowsOnInvalidIndexList() {
        Assertions.assertThrows(IllegalArgumentException.class,() -> {
            new SingleRoute(singleRoute2);
        });

        Assertions.assertDoesNotThrow(()-> new SingleRoute(singleRoute));
    }

    static void createList () {
        singleRoute.add(edge);
        singleRoute.add(leila);
        singleRoute.add(marc);
        singleRoute.add(antoine);
        singleRoute.add(naj);
        singleRoute.add(lucas);

        singleRoute1.add(edge1);
        singleRoute1.add(edge2);

        sR = new SingleRoute(singleRoute);
        ssR = new SingleRoute(singleRoute1);
    }

    @Test
    void lengthWorksOnKnownValues() {
        createList();
        Assertions.assertEquals(15539, sR.length());
    }

    @Test
    void edgesWorksOnKnownValues() {
        Assertions.assertEquals(singleRoute,sR.edges());
    }

    @Test
    void pointsWorksOnKnownValues() {
        List<PointCh> points = new ArrayList<>();
        points.add(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N));
        points.add(new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000));
        points.add(new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N + 1500));
        points.add(new PointCh(SwissBounds.MIN_E + 2500, SwissBounds.MIN_N));
        points.add(new PointCh(SwissBounds.MIN_E + 5748, SwissBounds.MIN_N + 2757));
        points.add(new PointCh(SwissBounds.MIN_E + 885, SwissBounds.MIN_N + 4299));
        points.add( new PointCh(SwissBounds.MIN_E + 1757, SwissBounds.MIN_N + 4966));
        Assertions.assertEquals(points,sR.points());
    }

    @Test
    void pointAtWorksOnKnownValues() {
        Assertions.assertEquals(new PointCh(SwissBounds.MIN_E+750,SwissBounds.MIN_N+750),sR.pointAt(3.0/4.0*1414));
        Assertions.assertEquals(new PointCh(SwissBounds.MIN_E+1000, SwissBounds.MIN_N+1000),sR.pointAt(1414));
        System.out.println(sR.pointAt(9809));
    }

    @Test
    void nodeClosestToWorksOnKnownValues() {
      Assertions.assertEquals(4,sR.nodeClosestTo(8081));
      Assertions.assertEquals(4,sR.nodeClosestTo(10341));
      Assertions.assertEquals(6,sR.nodeClosestTo(15142));
      Assertions.assertEquals(0,sR.nodeClosestTo(252));
      Assertions.assertEquals(1,sR.nodeClosestTo(1801));
      Assertions.assertEquals(2,sR.nodeClosestTo(2214));
      Assertions.assertEquals(3,sR.nodeClosestTo(4032));
    }

    @Test
    void pointClosestToWorksOnKnownValues() {
        Assertions.assertEquals(new RoutePoint( new PointCh(SwissBounds.MIN_E + 102500, SwissBounds.MIN_N+102500),Math.sqrt(50000000)/2.0,Math.sqrt(50000000)/2.0),ssR.pointClosestTo(new PointCh(SwissBounds.MIN_E + 100000, SwissBounds.MIN_N+105000)));
        Assertions.assertEquals( new RoutePoint(new PointCh(SwissBounds.MIN_E + 100000, SwissBounds.MIN_N + 100000),0,
                 Math.sqrt(2000000))
                ,ssR.pointClosestTo(new PointCh(SwissBounds.MIN_E + 99000, SwissBounds.MIN_N + 99000)));
        Assertions.assertEquals( new RoutePoint(new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N + 1500),2532,
                        Math.sqrt(20000))
                ,sR.pointClosestTo(new PointCh(SwissBounds.MIN_E + 2100, SwissBounds.MIN_N + 1600)));
        Assertions.assertEquals( new RoutePoint(new PointCh(SwissBounds.MIN_E + 105000, SwissBounds.MIN_N + 105000),Math.sqrt(50000000),
                        Math.sqrt(2000000))
                ,ssR.pointClosestTo(new PointCh(SwissBounds.MIN_E + 106000, SwissBounds.MIN_N + 106000)));
        Assertions.assertEquals( new RoutePoint(new PointCh(SwissBounds.MIN_E + 1757, SwissBounds.MIN_N + 4966),15539,
                        lucas.pointAt(Math2.clamp(0,lucas.positionClosestTo(new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N + 99000) ),lucas.length()) ).distanceTo(new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N + 99000)) )
                ,sR.pointClosestTo(new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N + 99000)));
         }

    @Test
    void elevationAtWorksOnKnownValues() {
        Assertions.assertEquals(Double.NaN,sR.elevationAt(1600));
        Assertions.assertEquals(Double.NaN,sR.elevationAt(10000));
        Assertions.assertEquals(345f,sR.elevationAt(101*5+50.5));
        Assertions.assertEquals(348f,sR.elevationAt(2532));
        Assertions.assertEquals(Double.NaN,sR.elevationAt(1600));
        Assertions.assertEquals(354.75,sR.elevationAt(5081+(4260/14.0)*3.0/4.0));
        Assertions.assertEquals(382-49*6.0/10.0,sR.elevationAt(14442+(1097/14.0)*6.6),1e-1);
    }
}
