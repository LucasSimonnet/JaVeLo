package ch.epfl.javelo.gui;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

public class TileManagerTest extends Application {

    @Test
    public void tileManagerAtWorksOnRandomValues() throws IOException {
        /*
        TileManager.TileId t = new TileManager.TileId(1,1,1);
        TileManager.TileId u = new TileManager.TileId(1,0,1);
        TileManager.TileId v = new TileManager.TileId(1,1,0);
        TileManager.TileId w = new TileManager.TileId(1,0,0);
        Path p = Path.of("tiles");
        TileManager ti = new TileManager(p,"tile.openstreetmap.org");
        System.out.println(ti.imageForTileAt(t));
        System.out.println(ti.imageForTileAt(u));
        System.out.println(ti.imageForTileAt(v));
        System.out.println(ti.imageForTileAt(w));
        System.out.println(ti.imageForTileAt(t));

         */
        TileManager tm = new TileManager( Path.of("."), "tile.openstreetmap.org" );
        System.out.println(tm.imageForTileAt( new TileManager.TileId(19, 271725, 185422) ) );
    }

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TileManager tm = new TileManager(
                Path.of("."), "tile.openstreetmap.org");
        Image tileImage = tm.imageForTileAt(
        new TileManager.TileId(19, 271725, 185422));

        for (int i = 0 ; i<=103 ; ++i) {
            tm.imageForTileAt(new TileManager.TileId(19,i,i));
        }
        tm.imageForTileAt(new TileManager.TileId(19,4,4));
        tm.imageForTileAt(new TileManager.TileId(19,104,104));
        Platform.exit();
    }
}


