package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MultiRouteTest {
    static double [] segmentsPositions = {0,2000,4000,9000};
    static double length = 9000;
    static List<String> t = new ArrayList<>();

    @Test
    void indexSegmentAtTest() {
        List<Route> routes = new ArrayList<>();
        List<Route> routeList = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        Edge e = new Edge(0,0,new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N),new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N),1000,null);
        edges.add(e);

        SingleRoute s  = new SingleRoute(edges);
        SingleRoute s1  = new SingleRoute(edges);
        SingleRoute s2 = new SingleRoute(edges);
        routes.add(s);
        routes.add(s1);
        routes.add(s2);

        MultiRoute m = new MultiRoute(routes);
        MultiRoute m1 = new MultiRoute(routes);
        MultiRoute m2 = new MultiRoute(routes);
        routeList.add(m);
        routeList.add(m1);
        routeList.add(m2);
        MultiRoute vrairoutes = new MultiRoute(routeList);
        assertEquals(0,vrairoutes.indexOfSegmentAt(0));
        assertEquals(2,vrairoutes.indexOfSegmentAt(3000));
        assertEquals(4,vrairoutes.indexOfSegmentAt(4500));
        assertEquals(8,vrairoutes.indexOfSegmentAt(8100));
    }

    void constructorThrowsOnValidArguments () {
        List<Edge> edges = new ArrayList<>();
        Edge e = new Edge(0,0,new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N),new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N),1000,null);
        edges.add(e);
        SingleRoute s  = new SingleRoute(edges);
        List<Route> routes = new ArrayList<>();




        assertThrows(IllegalArgumentException.class, () -> {
            new MultiRoute(routes);
        });
        routes.add(s);


        assertDoesNotThrow(() -> {
            new MultiRoute(routes);
        });

    }

    @Test
    void length() {
        List<Route> routes = new ArrayList<>();
        List<Route> routeList = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        Edge e = new Edge(0,0,new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N),new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N),1000,null);
        edges.add(e);

        SingleRoute s  = new SingleRoute(edges);
        SingleRoute s1  = new SingleRoute(edges);
        SingleRoute s2 = new SingleRoute(edges);
        routes.add(s);
        routes.add(s1);
        routes.add(s2);

        MultiRoute m = new MultiRoute(routes);
        MultiRoute m1 = new MultiRoute(routes);
        MultiRoute m2 = new MultiRoute(routes);
        routeList.add(m);
        routeList.add(m1);
        routeList.add(m2);

        MultiRoute vrairoutes = new MultiRoute(routeList);
        routeList.remove(m1);
        routeList.remove(m2);
        MultiRoute vrairoutes1 = new MultiRoute(routeList);
        routeList.add(s);
        MultiRoute vrairoutes2 = new MultiRoute(routeList);
        routeList.remove(m);
        MultiRoute vrairoutes3 = new MultiRoute(routeList);

        assertEquals(9000,vrairoutes.length());
        assertEquals(3000,vrairoutes1.length());
        assertEquals(4000,vrairoutes2.length());
        assertEquals(1000,vrairoutes3.length());
    }

    @Test
    void edgesTest() {
        List<Route> routes = new ArrayList<>();
        List<Route> routeList = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        Edge e = new Edge(0,0,new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N),new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N),1000,null);
        edges.add(e);

        SingleRoute s  = new SingleRoute(edges);
        SingleRoute s1  = new SingleRoute(edges);
        SingleRoute s2 = new SingleRoute(edges);
        routes.add(s);
        routes.add(s1);
        routes.add(s2);

        MultiRoute m = new MultiRoute(routes);
        MultiRoute m1 = new MultiRoute(routes);
        MultiRoute m2 = new MultiRoute(routes);
        routeList.add(m);
        routeList.add(m1);
        routeList.add(m2);

        MultiRoute vrairoutes = new MultiRoute(routeList);
        routeList.remove(m1);
        routeList.remove(m2);
        MultiRoute vrairoutes1 = new MultiRoute(routeList);
        routeList.add(s);
        MultiRoute vrairoutes2 = new MultiRoute(routeList);
        routeList.remove(m);
        MultiRoute vrairoutes3 = new MultiRoute(routeList);

        assertEquals(edges,vrairoutes3.edges());
        for (int i = 0 ; i<8 ; ++i) {
            edges.add(e);
        }
        assertEquals(edges,vrairoutes.edges());
    }
    @Test
    void pointsTest (){
        Edge edge = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000), 1000, null);
        Edge leila = new Edge(1, 2, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000),
                new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000), 1000, null);
        Edge marc = new Edge(2, 3, new PointCh(SwissBounds.MIN_E +  1000, SwissBounds.MIN_N +  1000),
                new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N+1000), 500, null);
        Edge antoine = new Edge(3, 4, new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N +1000),
                new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N + 1250 ), 250,  null);
        Edge naj = new Edge(4, 5, new PointCh(SwissBounds.MIN_E+1500, SwissBounds.MIN_N+1250),
                new PointCh(SwissBounds.MIN_E + 1750, SwissBounds.MIN_N + 1250), 250, null);
        Edge lucas = new Edge(5, 6, new PointCh(SwissBounds.MIN_E+1750, SwissBounds.MIN_N+1250),
                new PointCh(SwissBounds.MIN_E + 1750, SwissBounds.MIN_N + 1750), 500,null);
        List<Edge> e = new ArrayList<>();
        e.add(edge);
        e.add(leila);
        e.add(marc);
        e.add(antoine);
        e.add(naj);
        e.add(lucas);
        List<Route> r = new ArrayList<>();
        r.add(new SingleRoute(e));
        MultiRoute m = new MultiRoute(r);
        List<PointCh> points = new ArrayList<>();
        points.add(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N));
        points.add(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N+1000));
        points.add(new PointCh(SwissBounds.MIN_E+1000, SwissBounds.MIN_N+1000));
        points.add(new PointCh(SwissBounds.MIN_E+1500, SwissBounds.MIN_N+1000));
        points.add(new PointCh(SwissBounds.MIN_E+1500, SwissBounds.MIN_N+1250));
        points.add(new PointCh(SwissBounds.MIN_E+1750, SwissBounds.MIN_N+1250));
        points.add(new PointCh(SwissBounds.MIN_E+1750, SwissBounds.MIN_N+1750));
        assertEquals(points,m.points());
    }

    @Test
    void pointAtTest () {
        Edge edge = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000), 1000, null);
        Edge leila = new Edge(1, 2, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000),
                new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000), 1000, null);
        Edge marc = new Edge(2, 3, new PointCh(SwissBounds.MIN_E +  1000, SwissBounds.MIN_N +  1000),
                new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N+1000), 500, null);
        Edge antoine = new Edge(3, 4, new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N +1000),
                new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N + 1250 ), 250,  null);
        Edge naj = new Edge(4, 5, new PointCh(SwissBounds.MIN_E+1500, SwissBounds.MIN_N+1250),
                new PointCh(SwissBounds.MIN_E + 1750, SwissBounds.MIN_N + 1250), 250, null);
        Edge lucas = new Edge(5, 6, new PointCh(SwissBounds.MIN_E+1750, SwissBounds.MIN_N+1250),
                new PointCh(SwissBounds.MIN_E + 1750, SwissBounds.MIN_N + 1750), 500,null);
        List<Edge> e = new ArrayList<>();
        e.add(edge);
        e.add(leila);
        e.add(marc);
        e.add(antoine);
        e.add(naj);
        e.add(lucas);
        List<Route> r = new ArrayList<>();
        r.add(new SingleRoute(e));
        MultiRoute m = new MultiRoute(r);
        double es = SwissBounds.MIN_E;
        double n = SwissBounds.MIN_N;
        assertEquals(new PointCh(es,n),m.pointAt(-2937));
        assertEquals(new PointCh(es,n),m.pointAt(0));
        assertEquals( new PointCh(es+1750,n+1750), m.pointAt(3501));
        assertEquals( new PointCh(es+1750,n+1750), m.pointAt(3500));
        assertEquals( new PointCh(es+100,n+1000), m.pointAt(1100));
        assertEquals( new PointCh(es+600,n+1000), m.pointAt(1600));
        assertEquals( new PointCh(es+ 1500,n+1150), m.pointAt(2650));
        assertEquals( new PointCh(es+ 1750,n+1550), m.pointAt(3300));
    }

    @Test
    void nodeClosestToTest () {
        Edge edge = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000), 1000, null);
        Edge leila = new Edge(1, 2, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000),
                new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000), 1000, null);
        Edge marc = new Edge(2, 3, new PointCh(SwissBounds.MIN_E +  1000, SwissBounds.MIN_N +  1000),
                new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N+1000), 500, null);
        Edge antoine = new Edge(3, 4, new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N +1000),
                new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N + 1250 ), 250,  null);
        Edge naj = new Edge(4, 5, new PointCh(SwissBounds.MIN_E+1500, SwissBounds.MIN_N+1250),
                new PointCh(SwissBounds.MIN_E + 1750, SwissBounds.MIN_N + 1250), 250, null);
        Edge lucas = new Edge(5, 6, new PointCh(SwissBounds.MIN_E+1750, SwissBounds.MIN_N+1250),
                new PointCh(SwissBounds.MIN_E + 1750, SwissBounds.MIN_N + 1750), 500,null);
        List<Edge> e = new ArrayList<>();
        e.add(edge);
        e.add(leila);
        e.add(marc);
        e.add(antoine);
        e.add(naj);
        e.add(lucas);
        List<Route> r = new ArrayList<>();
        r.add(new SingleRoute(e));
        MultiRoute m = new MultiRoute(r);
        double es = SwissBounds.MIN_E;
        double n = SwissBounds.MIN_N;
        assertEquals(0,m.nodeClosestTo(-2937));
        assertEquals(0,m.nodeClosestTo(0));
        assertEquals(6, m.nodeClosestTo(3501));
        assertEquals(6, m.nodeClosestTo(3500));
        assertEquals(1, m.nodeClosestTo(1100));
        assertEquals(2, m.nodeClosestTo(1600));
        assertEquals(4, m.nodeClosestTo(2650));
        assertEquals(5, m.nodeClosestTo(3250));
    }

    @Test
    void pointClosestTo() {
        Edge edge = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000), 1000, null);
        Edge leila = new Edge(1, 2, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000),
                new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000), 1000, null);
        Edge marc = new Edge(2, 3, new PointCh(SwissBounds.MIN_E +  1000, SwissBounds.MIN_N +  1000),
                new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N+1000), 500, null);
        Edge antoine = new Edge(3, 4, new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N +1000),
                new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N + 1250 ), 250,  null);
        Edge naj = new Edge(4, 5, new PointCh(SwissBounds.MIN_E+1500, SwissBounds.MIN_N+1250),
                new PointCh(SwissBounds.MIN_E + 1750, SwissBounds.MIN_N + 1250), 250, null);
        Edge lucas = new Edge(5, 6, new PointCh(SwissBounds.MIN_E+1750, SwissBounds.MIN_N+1250),
                new PointCh(SwissBounds.MIN_E + 1750, SwissBounds.MIN_N + 1750), 500,null);
        List<Edge> e = new ArrayList<>();
        e.add(edge);
        e.add(leila);
        e.add(marc);
        e.add(antoine);
        e.add(naj);
        e.add(lucas);
        List<Route> r = new ArrayList<>();
        r.add(new SingleRoute(e));
        MultiRoute m = new MultiRoute(r);
        double es = SwissBounds.MIN_E;
        double n = SwissBounds.MIN_N;
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),0,0),m.pointClosestTo(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),0,200),m.pointClosestTo(new PointCh(SwissBounds.MIN_E +200, SwissBounds.MIN_N)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N+600),+600,400), m.pointClosestTo(new PointCh(SwissBounds.MIN_E +400, SwissBounds.MIN_N+600)));
        //assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N + 1250 ),2750,180), m.pointClosestTo(new PointCh(SwissBounds.MIN_E +1400, SwissBounds.MIN_N+1400)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 1700, SwissBounds.MIN_N + 1250 ),2950,30), m.pointClosestTo(new PointCh(SwissBounds.MIN_E +1700, SwissBounds.MIN_N+1220)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 1750, SwissBounds.MIN_N + 1750 ),3500,250), m.pointClosestTo(new PointCh(SwissBounds.MIN_E +2000, SwissBounds.MIN_N+1750)));
    }

    @Test
    void elevationAtTest (){
        Edge edge = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000), 1000,
                Functions.sampled(new float[]{348f, 357f, 368f, 343f, 370f, 368f, 382f, 374f, 360f, 347f, 333f}, 1000));
        Edge leila = new Edge(1, 2, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000),
                new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000), 1000,
                Functions.sampled(new float[]{333f, 357f, 368f, 343f, 370f, 368f, 382f, 374f, 360f, 347f, 333f}, 1000));

        List<Edge> e = new ArrayList<>();
        e.add(edge);
        e.add(leila);
        List<Route> r = new ArrayList<>();
        r.add(new SingleRoute(e));
        MultiRoute m = new MultiRoute(r);
        double es = SwissBounds.MIN_E;
        double n = SwissBounds.MIN_N;
        assertEquals(348f,m.elevationAt(-2999));
        assertEquals(348f,m.elevationAt(0));
        assertEquals(333f,m.elevationAt(2000));
        assertEquals(333f,m.elevationAt(4390));
        assertEquals(343f,m.elevationAt(1300));
    }

    @Test

    void testTotal(){
        Edge marco = new Edge(1, 2, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 100),
                new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100), 100, Functions.sampled(new float[] {250f, 300f, 350f, 400f, 4050}, 100));
        Edge titoune = new Edge(0,1, new PointCh (SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 100), 100, Functions.constant(250) );
        List<Edge> edges = new ArrayList<>() ;
        edges.add(titoune);
        edges.add(marco);
        SingleRoute BG = new SingleRoute(edges);

        Edge marcus = new Edge(2, 3, new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100),
                new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 200), 100, Functions.sampled(new float[] {400f, 450f, 350f, 300f}, 100));

        List<Edge> edges1 = new ArrayList <>();
        edges1.add(marcus);
        SingleRoute bg2 = new SingleRoute(edges1);

        Edge e1 = new Edge(3, 4, new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 200),
                new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 250), 50, Functions.constant(Double.NaN));
        Edge e2 = new Edge(4,5,new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 250),
                new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 250), 100, Functions.sampled(new float[] {300f, 375f, 425f, 250f, 50f}, 100 ));
        Edge e3 = new Edge(5, 6,new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 250),
                new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 300), 50, Functions.constant(50));
        List<Edge> edges2 = new ArrayList <>();
        edges2.add(e1);
        List<Edge> edges3 = new ArrayList <>();
        edges3.add(e2);
        edges3.add(e3);


        SingleRoute s1 = new SingleRoute(edges2);
        SingleRoute s2 = new SingleRoute(edges3);
        List<Route> singleRoutes = new ArrayList<>();
        singleRoutes.add(s1);
        singleRoutes.add(s2);
        MultiRoute nosloc = new MultiRoute(singleRoutes);

        List<Route> routes = new ArrayList <>();
        routes.add(BG);
        routes.add(bg2);
        routes.add(nosloc);
        MultiRoute marc = new MultiRoute(routes);

        assertEquals(500, marc.length() );

        assertEquals(0, marc.indexOfSegmentAt(153));
        assertEquals(1, marc.indexOfSegmentAt(299));
        assertEquals(3, marc.indexOfSegmentAt(487.452));
        assertEquals(2, marc.indexOfSegmentAt(312));
        assertEquals(0, marc.indexOfSegmentAt(-1000));
        assertEquals(3, marc.indexOfSegmentAt(1000));

        List<Edge> edgeFinale = new ArrayList<>();
        edgeFinale.add(titoune);
        edgeFinale.add(marco);
        edgeFinale.add(marcus);
        edgeFinale.add(e1);
        edgeFinale.add(e2);
        edgeFinale.add(e3);

        assertArrayEquals(edgeFinale.toArray(), marc.edges().toArray());

        List<PointCh> pointsMarc = new ArrayList<>();
        pointsMarc.add(new PointCh (SwissBounds.MIN_E, SwissBounds.MIN_N));
        pointsMarc.add(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 100));
        pointsMarc.add(new PointCh(SwissBounds.MIN_E +100 , SwissBounds.MIN_N + 100));
        pointsMarc.add(new PointCh(SwissBounds.MIN_E +100 , SwissBounds.MIN_N + 200));
        pointsMarc.add(new PointCh(SwissBounds.MIN_E +100 , SwissBounds.MIN_N + 250));
        pointsMarc.add(new PointCh(SwissBounds.MIN_E +200 , SwissBounds.MIN_N + 250));
        pointsMarc.add(new PointCh(SwissBounds.MIN_E +200 , SwissBounds.MIN_N + 300));

        assertArrayEquals(pointsMarc.toArray(), marc.points().toArray());
        assertEquals(new PointCh(SwissBounds.MIN_E + 75, SwissBounds.MIN_N + 100), marc.pointAt(175));
        assertEquals(new PointCh(SwissBounds.MIN_E , SwissBounds.MIN_N ), marc.pointAt(0));
        assertEquals(new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 300), marc.pointAt(1000));
        assertEquals(new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 271), marc.pointAt(471));

        assertEquals(350,marc.elevationAt(150));
        assertEquals(280,marc.elevationAt(115));
        assertEquals(Double.NaN, marc.elevationAt(340));
        assertEquals(50, marc.elevationAt(450));
        assertEquals(0, marc.nodeClosestTo(-100));
        assertEquals(0, marc.nodeClosestTo(45));
        assertEquals(1, marc.nodeClosestTo(75));
        assertEquals(1, marc.nodeClosestTo(133.2883));
        assertEquals(2, marc.nodeClosestTo(198));
        assertEquals(3, marc.nodeClosestTo(320));
        assertEquals(4, marc.nodeClosestTo(350));
        assertEquals(5, marc.nodeClosestTo(401));
        assertEquals(6, marc.nodeClosestTo(500));
        assertEquals(6, marc.nodeClosestTo(5000));

        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 220), 320, 40),
                marc.pointClosestTo(new PointCh(SwissBounds.MIN_E + 60, SwissBounds.MIN_N + 220)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 140, SwissBounds.MIN_N + 250), 390, 30),
                marc.pointClosestTo(new PointCh(SwissBounds.MIN_E + 140, SwissBounds.MIN_N + 220)));


    }

}
