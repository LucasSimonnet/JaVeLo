package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebMercatorTest {

    @Test
    void xWorksOnKnownValues() {
        assertEquals(0.518275214444,WebMercator.x(0.114826558880097182));
    }

    @Test
    void yWorksOnKnownValues() {
        assertEquals(0.353664894749,WebMercator.y(0.8119602873906342),1e-12);
    }

    @Test
    void lonWorksOnKnownValues() {
        assertEquals(0.114826558880097182,WebMercator.lon(0.518275214444));
    }

    @Test
    void latWorksOnKnownValues() {
        assertEquals(0.8119602873906342,WebMercator.lat(0.353664894749));
    }

}
