package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.ElevationProfileComputer;
import ch.epfl.javelo.routing.RouteComputerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class GpxGeneratorTest {

    @Test
    void writeGpxTest() throws IOException {
        ElevationProfile profile = ElevationProfileComputer.elevationProfile(RouteComputerTest.r,1);
        GpxGenerator.writeGpx("Route Javelo",RouteComputerTest.r,profile);
    }

}
