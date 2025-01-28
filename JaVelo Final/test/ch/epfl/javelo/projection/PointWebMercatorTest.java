package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PointWebMercatorTest {

    @Test
    void pointWebMercatorConstructorthrowsOnInvalidCoordinates() {

        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(2,0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(25,0.75);
        });
    }

    @Test
    void ofWorksOnKnownValues() {
        assertEquals(new PointWebMercator(0.518275214444,0.353664894749),PointWebMercator.of(19,69561722,47468099));
    }

    @Test
    void ofPointChWorksOnKnowsValues() {
        assertEquals(new PointWebMercator(0.518275214444,0.353664894749),PointWebMercator.ofPointCh(new PointCh(Ch1903.e(0.114826558880097182,0.8119602873906342),Ch1903.n(0.114826558880097182,0.8119602873906342))));
    }

    @Test
    void xAtZoomLevelWorksOnKnownValues() {
        PointWebMercator point = new PointWebMercator(0.518275214444,0.353664894749);
        assertEquals(69561722,point.xAtZoomLevel(19),1);
    }

    @Test
    void yAtZoomLevelWorksOnKnownValues() {
        PointWebMercator point = new PointWebMercator(0.518275214444,0.353664894749);
        assertEquals(47468099,point.yAtZoomLevel(19),1);
    }

    @Test
    void lonWorksOnKnownValues (){
        PointWebMercator point = new PointWebMercator(0.518275214444,0.353664894749);
        assertEquals(0.114826558880097182,point.lon());
    }

    @Test
    void latWorksOnKnownValues (){
        PointWebMercator point = new PointWebMercator(0.518275214444,0.353664894749);
        assertEquals(0.8119602873906342,point.lat());
    }

    @Test
    void toPointChWorsOnKnownValues() {
        PointWebMercator point1 = new PointWebMercator(0,0.353664894749);
        assertEquals(null,point1.toPointCh());

        PointWebMercator point2 = new PointWebMercator(0.518275214444,1);
        assertEquals(null,point2.toPointCh());

        PointWebMercator point3 = new PointWebMercator(0,0);
        assertEquals(null,point3.toPointCh());

        PointWebMercator point4 = new PointWebMercator(0.518275214444,0.353664894749);
        assertEquals(new PointCh(Ch1903.e(0.114826558880097182,0.8119602873906342),Ch1903.n(0.114826558880097182,0.8119602873906342)),point4.toPointCh());
    }

}
