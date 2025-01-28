package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RoutePoint;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Point2D;

import java.util.function.Consumer;

/**
 * Permet de gèrer l'affichage du fond de carte au-dessus duquel sont superposés l'itinéraire et les points de passage.
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */
public final class AnnotatedMapManager {
    private static final int START_ZOOM = 12;
    private static final double START_X = 543200;
    private static final double START_Y = 370650;
    private static final int MIN_PIXEL = 15;

    private final Pane pane;
    private final ObjectProperty<Point2D> mouse;
    private final ObjectProperty<MapViewParameters> property;
    private final ReadOnlyObjectProperty<Route> route;
    private final SimpleDoubleProperty mousePosition;

    /**
     *  Permet de construire un gestionnaire utile à l'affichage du fond de carte.
     *
     * @param graph
     *      Graphe du réseau routier,
     * @param tileManager
     *      Gestionnaire de tuiles OpenStreetMap
     * @param routeBean
     *      Le bean de l'itinéraire
     * @param error
     *      Un consommateur d'erreurs permettant de signaler une erreur
     */
    public AnnotatedMapManager (Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> error) {

        route = routeBean.routeProperty();
        MapViewParameters mapView = new MapViewParameters(START_ZOOM, START_X, START_Y );
        property = new SimpleObjectProperty<>(mapView);

        WaypointsManager waypointsManager = new WaypointsManager(graph, property , routeBean.waypoints(), error);
        BaseMapManager baseMapManager = new BaseMapManager(tileManager, waypointsManager, property);
        RouteManager routeManager = new RouteManager(routeBean, property);

        pane = new StackPane(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());
        pane.getStylesheets().add("map.css");

        mouse = new SimpleObjectProperty<>();

        pane.setOnMouseExited(m -> mouse.set(null));

        pane.setOnMouseMoved(m -> mouse.set(new Point2D(m.getX(), m.getY())));

        mousePosition = new SimpleDoubleProperty();

        property.addListener((p,oldP,newP)-> mousePositionProperty() );

        mousePosition.bind(Bindings.createDoubleBinding(this::mousePositionProperty
                ,route, property, mouse));
    }

    /**
     * Permet de calculer si la position de la souris est à moins de 15 pixels de la route
     *
     * @return la position sur la route la plus proche de la souris si elle est à moins de 15 pixels de celle-ci,
     * Double.NaN sinon
     */
    private double mousePositionProperty(){
        Point2D m = mouse.get();
        MapViewParameters map = property.get();
        Route r = route.get();
        if (r==null || m==null)
            return Double.NaN;

        PointWebMercator p = map.pointAt(m.getX(), m.getY());
        PointCh mousePoint = p.toPointCh();
        if (mousePoint == null)
            return Double.NaN;

        RoutePoint closestPoint = r.pointClosestTo(mousePoint);
        PointWebMercator point = PointWebMercator.ofPointCh(closestPoint.point());

        double difX = map.viewX(point) - map.viewX(p);
        double difY = map.viewY(point) - map.viewY(p);

        if (Math2.norm(difX,difY) < MIN_PIXEL) {
            return closestPoint.position();
        }
        else
            return Double.NaN;
    }

    /**
     * Permet de retourner le panneau contenant la carte annotée
     *
     * @return le panneau contenant la carte annotée
     */
    public Pane pane () {
        return  pane;
    }

    /**
     * Permet de retourner la propriété contenant la position du pointeur de la souris le long de l'itinéraire
     *
     * @return la propriété contenant la position du pointeur de la souris le long de l'itinéraire
     */
    public ReadOnlyDoubleProperty mousePositionOnRouteProperty () {
        return mousePosition;
    }

}

