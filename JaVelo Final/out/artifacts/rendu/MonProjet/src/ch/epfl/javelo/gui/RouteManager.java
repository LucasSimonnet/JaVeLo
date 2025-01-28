package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

import java.util.List;

/**
 * Permet de gèrer l'affichage de l'itinéraire et une partie de l'interaction avec lui.
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */
public final class RouteManager {
    private final RouteBean routeBean;
    private final ObjectProperty<MapViewParameters> property;

    private final Pane pane;
    private final Polyline routeLine;
    private final Circle disque;

    private static final int RADIUS = 5, REINITIALIZE_VALUE = 0;

    /**
     * Permet de construire un gestionnaire utile a l'affichage de l'itinéraire grâce au bean de l'itinéraire
     * et la  propriété JavaFX contenant les paramètres de la carte affichée.
     *
     * @param routeBean
     *      bean de l'itinéraire
     * @param property
     *      propriété JavaFX contenant les paramètres de la carte affichée
     */
    public RouteManager(RouteBean routeBean, ObjectProperty<MapViewParameters> property) {
        this.routeBean = routeBean;
        this.property = property;

        pane = new Pane();
        this.routeLine = new Polyline();
        routeLine.setId("route");
        this.disque = new Circle(RADIUS);
        disque.setId("highlight");

        property.addListener((o,oldO,newO) -> {
            if (oldO.zoomLevel() != newO.zoomLevel()) {
                drawRoute();
                placeDisque(routeBean.route());
            } else {
                Point2D difference = oldO
                        .topLeft()
                        .subtract(newO.topLeft());

                placeShape(routeLine, difference);
                placeShape(disque, difference);
            }

        });

        disque.setOnMouseClicked(m -> {
            MapViewParameters map = property.get();
            Point2D disqueOnPane = disque.localToParent( m.getX(),m.getY() );

            PointCh point = map
                    .pointAt( disqueOnPane.getX(), disqueOnPane.getY() )
                    .toPointCh();

            double position = routeBean.highlightedPosition();
            int nodeClosest = routeBean
                    .route()
                    .nodeClosestTo(position);

            int index = routeBean.indexOfNonEmptySegmentAt(position) + 1;

            routeBean
                        .waypoints()
                        .add( index, new Waypoint(point,nodeClosest) );
        });

        routeBean.highlightedPositionProperty().addListener( (p,oldP,newP) -> placeDisque(routeBean.route()));

        routeBean.routeProperty().addListener( (o,oldO,newO) -> {
            drawRoute();
            placeDisque(newO);
        });
        drawRoute();

        pane
                .getChildren()
                .addAll(routeLine,disque);
    }

    /**
     *  Permet de replacer une forme
     *
     * @param shape
     *      représente une forme
     *
     * @param differenceMap
     *      représente les coordonnée de la difference de la carte.
     */
    private void placeShape(Shape shape, Point2D differenceMap) {

        double x = shape.getLayoutX();
        double y = shape.getLayoutY();

        shape.setLayoutX( x + differenceMap.getX() );
        shape.setLayoutY( y + differenceMap.getY() );
    }

    /**
     * Permet de dessiner l'itinéraire
     */
    private void drawRoute() {
        Route routeB = routeBean.route();

        routeLine
                .getPoints()
                .clear();

        routeLine.setLayoutX(REINITIALIZE_VALUE);
        routeLine.setLayoutY(REINITIALIZE_VALUE);

        if (routeB!=null) {
            List<PointCh> points = routeB.points();
            points.forEach(p -> {
                Point2D point = coordinatesOnPane(p);

                routeLine.getPoints()
                         .addAll( point.getX(), point.getY() );
            });
            setShapesVisible(true);
        } else {
            setShapesVisible(false);
        }
    }

    private void setShapesVisible (boolean b){
        disque.setVisible(b);
        routeLine.setVisible(b);
    }

    /**
     * Permet de placer le disque de mise en evidence
     * @param route
     *      Représente l'itinéraire
     */
    private void placeDisque(Route route){
        double pos = routeBean.highlightedPosition();;
        if (pos>=0 && routeBean.route()!=null) {
            PointCh disquePoint = route.pointAt(routeBean.highlightedPosition());
            Point2D disqueOnPane = coordinatesOnPane(disquePoint);

            disque.setVisible(true);
            disque.setLayoutX(disqueOnPane.getX());
            disque.setLayoutY(disqueOnPane.getY());
        } else
            disque.setVisible(false);
    }

    /**
     * Determine les coordonnées sur le panneau du point en coordonnée Suisse
     * @param point
     *      Représente un point en coordonnée Suisse
     * @return le point sur le panneau du point initialement en coordonnée Suisse
     */
    private Point2D coordinatesOnPane(PointCh point) {

        MapViewParameters map = property.get();
        PointWebMercator p = PointWebMercator.ofPointCh(point);
        return new Point2D(map.viewX(p), map.viewY(p));

    }

    /**
     * Permet de retourner le panneau JavaFX contenant la ligne représentant l'itinéraire
     * et le disque de mise en évidence
     * @return le panneau JavaFX contenant la ligne représentant l'itinéraire et le disque de mise en évidence
     */
    public Pane pane() {

        pane.setPickOnBounds(false);
        return pane;

    }
}
