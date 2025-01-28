package ch.epfl.javelo;

import ch.epfl.javelo.gui.TileManager;
import com.sun.javafx.geom.Vec2d;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.transform.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PreconditionsTest {
    @Test
    void checkArgumentSucceedsForTrue() {
        assertDoesNotThrow(() -> {
            Preconditions.checkArgument(true);
        });
    }

    @Test
    void checkArgumentThrowsForFalse() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> {
            Preconditions.checkArgument(false);
        });
    }

    @Test
    void test() {
        System.out.println(Math.round(0.5));
        System.out.println(Math.round(0.2));

    }
}
