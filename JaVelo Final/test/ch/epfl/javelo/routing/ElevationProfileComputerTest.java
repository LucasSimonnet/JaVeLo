package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevationProfileComputerTest {

    static SingleRoute sR;

    static SingleRoute SVR;

    static List<Edge> singleRoute = new ArrayList<>();

    static List<Edge> single = new ArrayList<>();

    static Edge edge = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
            new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000), 1414,
            Functions.sampled(new float[]{348f, 357f, 368f, 343f, 370f, 368f, 382f, 374f, 360f, 347f, 333f, 342f, 326f, 312f, 299f}, 1414));

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

    static void createList () {
        singleRoute.add(edge);
        singleRoute.add(leila);
        singleRoute.add(marc);
        singleRoute.add(antoine);
        singleRoute.add(naj);
        singleRoute.add(lucas);

        single.add(leila);

        sR = new SingleRoute(singleRoute);
        SVR = new SingleRoute(single);
    }

    @Test
    void elevationProfileWorksOnKnownValues() {
        createList();
        ElevationProfile p = ElevationProfileComputer.elevationProfile(sR,500);
        System.out.println(p.maxElevation() +"\n"+ p.minElevation() +"\n"+ p.totalAscent() +"\n"+ p.totalDescent());
        System.out.println();

        ElevationProfile p2 = ElevationProfileComputer.elevationProfile(SVR,1414/14.0);
        System.out.println(p2.maxElevation() +"\n"+ p2.minElevation() +"\n"+ p2.totalAscent() +"\n"+ p2.totalDescent());
    }

    @Test
    void computerElevationTest(){
        Edge e1 = new Edge(0, 1,
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N),
                100, Functions.sampled(new float[]{(float)Double.NaN,0f, 50f, 100f}, 100));

        Edge e2 = new Edge(1, 2,
                new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 50),
                50, Functions.sampled(new float[]{100f, 90f, (float)Double.NaN, 70f, 60f, 100f}, 50));

        Edge e3 = new Edge(2, 3,
                new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 50), new PointCh(SwissBounds.MIN_E + 150, SwissBounds.MIN_N + 100),
                70, Functions.sampled(new float[]{100f, (float)Double.NaN,100f}, 70));

        Edge e4 = new Edge(3, 4,
                new PointCh(SwissBounds.MIN_E + 150, SwissBounds.MIN_N + 100), new PointCh(SwissBounds.MIN_E + 250, SwissBounds.MIN_N + 100),
                100, Functions.sampled(new float[]{100f, 0f,(float)Double.NaN}, 100));
        List<Edge> edges = new ArrayList<>();
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);
        edges.add(e4);

        SingleRoute route = new SingleRoute(edges);
        ElevationProfile ele = ElevationProfileComputer.elevationProfile(route, 1);
        assertEquals(100,ele.maxElevation());
        assertEquals(0, ele.minElevation());
        assertEquals(320, ele.length());
        assertEquals(140, ele.totalDescent(), 1);
        assertEquals(140, ele.totalAscent(), 1);

    }
}
