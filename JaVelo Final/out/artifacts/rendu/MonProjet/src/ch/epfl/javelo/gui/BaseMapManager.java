package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Gère l'affichage et l'interaction avec le fond de carte
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */
public final class BaseMapManager {
    private final TileManager tileManager;
    private final ObjectProperty <MapViewParameters> property;
    private final Canvas canvas;
    private final Pane pane;
    private final ObjectProperty <Point2D> lastPosition;

    private static final int TILE_SIZE = 256, ZOOM_MAX = 19, ZOOM_MIN = 8, MAGIC_VALUE_250 = 250;

    private boolean redrawNeeded = true;

    /**
     * Permet de creer un gestionnaire de fond de carte
     * @param tileManager
     *          Gestionnaire de tuile
     * @param waypointsManager
     *          Gestion des points de passages
     * @param property
     *          Propriété contenant les paramètres de la carte
     */
    public BaseMapManager (TileManager tileManager, WaypointsManager waypointsManager,
                           ObjectProperty<MapViewParameters> property) {
        this.tileManager = tileManager;
        this.property = property;

        canvas = new Canvas();
        pane = new Pane();

        canvas
                .widthProperty()
                .addListener( (old,newp,p) -> redrawOnNextPulse());

        canvas
                .heightProperty()
                .addListener( (old,newp,p) -> redrawOnNextPulse());

        property.addListener( (old,newp,p) -> redrawOnNextPulse());

        canvas
                .widthProperty()
                .bind(pane.widthProperty());

        canvas
                .heightProperty()
                .bind(pane.heightProperty());

        pane
                .getChildren()
                .add(canvas);

        canvas
                .sceneProperty()
                .addListener((p,oldS,newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        redrawOnNextPulse();

        pane.setOnMouseClicked(m -> {
            if (m.isStillSincePress()) {
                MapViewParameters map = property.get();
                PointWebMercator p = map.pointAt(m.getX(), m.getY());
                PointCh pointCh = p.toPointCh();
                waypointsManager.addWayPoint(pointCh);
            }
        });

        lastPosition = new SimpleObjectProperty<>();

        pane.setOnMousePressed(m -> lastPosition.set( new Point2D(m.getX(),m.getY() ) ) );

        pane.setOnMouseDragged(m -> {
            MapViewParameters map = property.get();

            Point2D lastPoint = lastPosition.get();
            Point2D movingPoint = new Point2D(m.getX(),m.getY());
            Point2D difference = lastPoint.subtract(movingPoint);
            Point2D newTopLeft = map
                    .topLeft()
                    .add(difference);

            property.set(map.withMinXY(newTopLeft.getX(), newTopLeft.getY()) );
            lastPosition.set(movingPoint);
        });

        SimpleLongProperty minScrollTime = new SimpleLongProperty();

        pane.setOnScroll(m -> {

            MapViewParameters map = property.get();

            long currentTime = System.currentTimeMillis();

            if (currentTime < minScrollTime.get())
                return ;
            minScrollTime.set(currentTime+MAGIC_VALUE_250);
            double zoomDelta = Math.signum(m.getDeltaY());

            int newZoom = (int)Math2.clamp(ZOOM_MIN,(zoomDelta+map.zoomLevel()),ZOOM_MAX);
            PointWebMercator p = map.pointAt(m.getX(),m.getY());

            double newMapX = p.xAtZoomLevel(newZoom)-m.getX();
            double newMapY = p.yAtZoomLevel(newZoom)-m.getY();
            property.set( new MapViewParameters(newZoom, newMapX, newMapY) );
        });

    }

    /**
     * Permet de retourner le panneau JavaFX affichant le fond de carte
     *
     * @return le panneau JavaFX affichant le fond de carte
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Permet d'effectuer le dessin de la carte si et seulement si celui-ci est necessaire
     */
    private void redrawIfNeeded() {

        if (!redrawNeeded) return;
        redrawNeeded = false;

        MapViewParameters map = property.get();
        int zoomLevel = map.zoomLevel();
        GraphicsContext context = canvas.getGraphicsContext2D();

        int firstTileX = tile(map.x());
        int firstTileY = tile(map.y());
        int lastTileX = tile(map.x() + canvas.getWidth() );
        int lastTileY = tile(map.y() + canvas.getHeight() );

        int x = firstTileX*TILE_SIZE - (int)map.x() ;
        int y = firstTileY*TILE_SIZE - (int)map.y() ;

        for (int tileX=firstTileX ; tileX <= lastTileX ; ++tileX) {

            for (int tileY=firstTileY ; tileY <= lastTileY ; ++tileY){

                try {
                    if (TileManager.TileId.isValid(zoomLevel,tileX,tileY)) {
                        Image im = tileManager.imageForTileAt(new TileManager.TileId(zoomLevel, tileX, tileY));
                        context.drawImage(im, x, y);
                    }

                } catch (IOException ignored) {}
                y+=TILE_SIZE;
            }
            y = firstTileY*TILE_SIZE - (int)map.y() ;
            x+=TILE_SIZE;
        }
    }

    private int tile ( double x ) {return ((int)x )/TILE_SIZE;}

    /**
     *  Permet de retarder le dessin jusqu'au prochain battement
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

}