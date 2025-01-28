package ch.epfl.javelo.gui;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.List;
import java.util.function.Consumer;

/**
 * Gère l'affichage et l'interaction avec les points de passage
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */
public final class WaypointsManager {
    private final Graph graph;
    private final ObjectProperty<MapViewParameters> property;
    private final ObservableList<Waypoint> waypoints;
    private final Consumer<String> errors;
    private final Pane pane;
    private final ObjectProperty<Point2D> mousePosition;

    private static final int MAX_RAYON = 1000, POINT_NULL = -1, MIN_LIST_SIZE = 2,
            LAST_MIN_LIST_INDEX = 1, FIRST_LIST_INDEX = 0;

    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> property,
                            ObservableList<Waypoint> waypoints, Consumer<String> errors) {

        this.graph = graph;
        this.property = property;
        this.waypoints = waypoints;
        this.errors = errors;
        mousePosition = new SimpleObjectProperty<>();
        pane = new Pane();

        waypoints.forEach( point -> {
            createGroup(point);
            placePoint(point);
        });

        waypoints.addListener((ListChangeListener<? super Waypoint>) w -> {
            pane
                    .getChildren()
                    .clear();

            waypoints.forEach( point -> {
                createGroup(point);
                placePoint(point);
            });
        });

        property.addListener( (old,newp,p) -> waypoints.forEach(this::placePoint) );

    }

    /**
     * Permet de creer un groupe représentant graphiquement un point de passage
     *
     * @param w
     *          Point de passage
     */
    private void createGroup(Waypoint w) {
        Group g = new Group();
        List<Node> gChildren = g.getChildren();
        List<String> gStyleClass = g.getStyleClass();

        SVGPath outside = createPin("pin_outside");
        gChildren.add(outside);
        outside.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");

        SVGPath inside = createPin("pin_inside");
        gChildren.add(inside);
        inside.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");

        gStyleClass.add("pin");

        pinColor(w,g);

        pane.getChildren().add(g);
    }

    private SVGPath createPin(String s) {
        SVGPath path = new SVGPath();
        path
                .getStyleClass()
                .add(s);

        return path;
    }

    /**
     * Permet d'attribuer la bonne classe de style à chaque group
     *
     * @param w
     *      Point de passage attaché au group
     * @param g
     *      Group représentant graphiquement le waypoint
     */
    private void pinColor(Waypoint w, Node g) {
        List<String> styleClass = g.getStyleClass();

        if (styleClass.size() == MIN_LIST_SIZE)
            styleClass.remove(LAST_MIN_LIST_INDEX);

        if ( waypoints
                .get(FIRST_LIST_INDEX)
                .equals(w) )

            styleClass.add("first");

        else if ( waypoints
                .get( waypoints.size()-1 )
                .equals(w) )

            styleClass.add("last");

        else
            styleClass.add("middle");
    }

    /**
     * Permet de placer les points de passages
     *
     * @param w
     *      Point de passage
     */
    private void placePoint(Waypoint w) {
        MapViewParameters map = property.get();
        double topLeftX = map.x();
        double topLeftY = map.y();

        int index = waypoints.indexOf(w);
        Node group = pane
                .getChildren()
                .get(index);

        PointWebMercator point = PointWebMercator.ofPointCh(w.position());

        double xW = point.xAtZoomLevel(map.zoomLevel());
        group.setLayoutX(xW - topLeftX);

        double yW = point.yAtZoomLevel(map.zoomLevel());
        group.setLayoutY(yW - topLeftY);


        group.setOnMousePressed(m ->
            mousePosition.set( group.localToParent(m.getX(),m.getY()) )
        );

        group.setOnMouseDragged( m -> {
            Point2D newMouse = group.localToParent(m.getX(),m.getY());
            Point2D oldMouse = mousePosition.get();
            double x = group.getLayoutX() - oldMouse.getX() + newMouse.getX();
            double y = group.getLayoutY() - oldMouse.getY() + newMouse.getY();
            group.setLayoutX(x);
            group.setLayoutY(y);
            mousePosition.set(newMouse);
        });

        group.setOnMouseReleased(m -> {
            MapViewParameters newMap = property.get();
            double topX = newMap.x();
            double topY = newMap.y();

            PointWebMercator p = PointWebMercator.ofPointCh(w.position());

            double x = p.xAtZoomLevel(newMap.zoomLevel());
            double y = p.yAtZoomLevel(newMap.zoomLevel());

            if (m.isStillSincePress()) {
                waypoints.remove(w);
                pane.getChildren().remove(group);
            } else {
                double xPos = group.getLayoutX();
                double yPos = group.getLayoutY();
                PointCh pointM = newMap
                        .pointAt(xPos, yPos)
                        .toPointCh();

                int node = pointM == null ? POINT_NULL :graph.nodeClosestTo(pointM, MAX_RAYON);

                if (node != POINT_NULL) {
                    waypoints.remove(w);
                    waypoints.add(index,new Waypoint(pointM,node));
                }
                else {
                    group.setLayoutX(x - topX);
                    group.setLayoutY(y - topY);
                    errors.accept("Aucune route à proximité !");
                }
            }
        });

    }

    /**
     * Permet de retourner le panneau contenant les points de passage
     *
     * @return le panneau contenant les points de passage
     */
    public Pane pane() {
        pane.setPickOnBounds(false);
        return pane;
    }

    /**
     * Permet d'ajouter un nouveau point de passage au nœud du graphe qui en est le plus proche
     *
     * @param point
     *          point en coordonnées suisses
     */
    public void addWayPoint(PointCh point) {
        int nodeClosest = (point==null)?POINT_NULL:graph.nodeClosestTo(point,MAX_RAYON);

        if (nodeClosest != POINT_NULL) {
            Waypoint newWaypoint = new Waypoint(point, nodeClosest);
            waypoints.add(newWaypoint);
        } else
            errors.accept("Aucune route à proximité !");
    }
}
