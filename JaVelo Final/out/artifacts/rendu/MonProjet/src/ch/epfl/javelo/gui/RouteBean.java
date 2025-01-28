package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Représente un bean JavaFX regroupant les propriétés relatives aux points de passage et à l'itinéraire correspondant
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */
public final class RouteBean {

    private final RouteComputer r;
    private final ObservableList <Waypoint> waypoints;
    private final ObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<ElevationProfile> elevationProfile;

    private static final int INITIAL_CAPACITY = 100, MAX_STEP_LENGTH = 5, MIN_LIST_LENGTH = 1;
    private static final float LOAD_FACTOR = 0.75f;

    private final LinkedHashMap<Pair<Integer,Integer>, Route > cacheMemory
            = new LinkedHashMap<>(INITIAL_CAPACITY,LOAD_FACTOR,true);


    /**
     * Construit un bean JavaFX regroupant les propriétés relatives aux points de passage et à l'itinéraire
     * correspondant grâce au calculateur d'itinéraire
     *
     * @param r
     *      calculateur d'itinéraire
     */
    public RouteBean (RouteComputer r){
        this.r= r;

        route = new SimpleObjectProperty<>();

        elevationProfile = new SimpleObjectProperty<>();

        highlightedPosition = new SimpleDoubleProperty();

        waypoints = FXCollections.observableArrayList();

        waypoints.addListener( (ListChangeListener<? super Waypoint>) o -> createRoute());

    }

    /**
     * Permet de retourner la propriété de la position mise en évidence
     * @return la propriété de la position mise en évidence
     */
    public DoubleProperty highlightedPositionProperty (){
        return highlightedPosition;
    }

    /**
     * Permet de retourner le contenu de la propriété de la position mise en évidence
     * @return le contenu de la propriété de la position mise en évidence
     */
    public double highlightedPosition () {
        return highlightedPosition.get();
    }

    /**
     * Permet de stocker une valeur de type double dans la propriété de la position mise en évidence
     * @param value
     *      valeur stocker dans la propriété de la position mise en évidence
     */
    public void setHighlightedPosition (double value){
        highlightedPosition.set(value);
    }

    /**
     * Permet de retourner la liste des points de passages
     * @return la liste des points de passages
     */
    public ObservableList <Waypoint> waypoints () {
        return waypoints;
    }

    /**
     * Permet de retourner la propriété contenant la route
     * @return la propriété contenant la route
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }

    /**
     * Permet de retourner la route
     * @return la route
     */
    public Route route () {
        return route.get();
    }

    /**
     * Permet de retourner le profil de l'itinéraire
     * @return le profil de l'itinéraire
     */
    public ReadOnlyObjectProperty<ElevationProfile> profileProperty() {
        return elevationProfile;
    }

    /**
     * Permet de créer l'itinéraire
     */
    private void createRoute() {
        int waypointsSize = waypoints().size();
        List<Route> allRoutes = new ArrayList<>();

        boolean isNull = false;
        if (waypointsSize > MIN_LIST_LENGTH) {
            for(int indexWaypoint = 0 ; indexWaypoint<waypointsSize - 1 ; ++indexWaypoint) {
                int startNodeID = waypoints
                        .get(indexWaypoint)
                        .closestNodeId();
                int endNodeID = waypoints
                        .get(indexWaypoint+1)
                        .closestNodeId();

                if (startNodeID == endNodeID)
                    continue;

                Pair<Integer,Integer> newKey = new Pair<>(startNodeID,endNodeID);
                Route singleRoute;
                if ( cacheMemory.containsKey(newKey) )
                    singleRoute = cacheMemory.get(newKey);
                else {
                    singleRoute = r.bestRouteBetween(startNodeID, endNodeID);
                    isFull();
                    cacheMemory.put(newKey,singleRoute);
                }
                if (singleRoute==null){
                    isNull = true;
                    break;
                }
                allRoutes.add(singleRoute);
            }
        }

        if (!( isNull || allRoutes.isEmpty() ) ) {

            route.set(new MultiRoute(allRoutes));

            elevationProfile.set(
                    ElevationProfileComputer.elevationProfile(route.get(), MAX_STEP_LENGTH));
        } else {
            route.set(null);
            elevationProfile.set(null);
        }
    }

    /**
     * Permet de savoir la positions de  l'index des segments non vide le long de l'itinéraire
     * @param position
     *      position le long de l'itinéraire
     * @return l'index des segments non vide selon la position le long de l'itinéraire
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {

            int n1 = waypoints
                    .get(i)
                    .closestNodeId();

            int n2 = waypoints
                    .get(i + 1)
                    .closestNodeId();

            if (n1 == n2) index += 1;
        }

        return index;
    }

    /**
     * Permet de savoir si le cache memoire a atteint sa taille maximale
     */
    private void isFull() {
        if (cacheMemory.size() >= INITIAL_CAPACITY) {
            for ( Pair<Integer,Integer> LRU : cacheMemory.keySet()) {
                cacheMemory.remove(LRU);
                break;
            }
        }
    }
}
