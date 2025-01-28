package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.binarySearch;

/**
 * Permet de représenter un itinéraire simple, reliant un point à un autre sans points de passages intermédiaires
 *
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */
public final class SingleRoute implements Route {

    private final List<Edge> edges;

    private final List<PointCh> points;

    private final double[] edgesPositions;

    /**
     * Crée l'itinéraire simple composé des arêtes données
     *
     * @param edges
     *          Liste d'arêtes
     *
     * @throws IllegalArgumentException si la liste d'arêtes est vide
     */
    public SingleRoute(List<Edge> edges) {
        this.edges = List.copyOf(edges);
        Preconditions.checkArgument(!edges.isEmpty());

        edgesPositions = new double[edges().size()+1];
        int tabIndex = 1;
        for (Edge edge : edges)
            edgesPositions[tabIndex] = edge.length() + edgesPositions[tabIndex++ - 1];

        List<PointCh> points = new ArrayList<>();
        PointCh firstPoint = edges
                .get(0)
                .fromPoint();
        points.add(firstPoint);
        for (Edge edge : edges)
            points.add(edge.toPoint());
        this.points = List.copyOf(points);
    }

    /**
     * Calcule l'index du segment de l'itinéraire contenant la position donnée
     *
     * @param position
     *          Position sur l'itinéraire du point exprimé en coordonnées suisses donné
     *
     * @return 0 pour un itinéraire simple
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * @return la longueur totale de l'itinéraire, en mètres
     */
    @Override
    public double length() {
        return edgesPositions[edgesPositions.length-1];
    }

    /**
     * @return la liste des arêtes composants l'itinéraire
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }

    /**
     * @return la liste des points situés aux extrémités des arêtes de l'itinéraire
     */
    @Override
    public List<PointCh> points() {
        return points;
    }

    /**
     * @param position
     *          Position sur l'itinéraire du point exprimé en coordonnées suisses donné
     *
     * @return le point se trouvant à la position donnée le long de l'itinéraire
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0,position,length());
        int edgeIndex = edgeSearch(position);
        return edges
                .get(edgeIndex)
                .pointAt(position -edgesPositions[edgeIndex]);
    }

    /**
     * @param position
     *          Position sur l'itinéraire du point exprimé en coordonnées suisses donné
     *
     * @return l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0,position,length());
        int edgeIndex = edgeSearch(position);
        Edge edge = edges.get(edgeIndex);
        position = Math2.clamp(0,position,length());
        double edgePosition = position - edgesPositions[edgeIndex];
        PointCh point = edge.pointAt(edgePosition);

        double distanceToEndPoint = point.distanceTo(edge.toPoint());
        double distanceToStartPoint = point.distanceTo(edge.fromPoint());

        return ( distanceToEndPoint < distanceToStartPoint ) ? edge.toNodeId() : edge.fromNodeId();
    }

    /**
     * @param point
     *          Point en coordonnées suisses
     *
     * @return le point de l'itinéraire se trouvant le plus proche du point de référence donné
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint closestPoint = RoutePoint.NONE;

        int  edgeIndex = 0;
        for (Edge edge : edges) {
            double newPosition = Math2.clamp(0,edge.positionClosestTo(point),edge.length() );
            PointCh newPoint = edge.pointAt(newPosition);
            double edgeDistance = newPoint.distanceTo(point);
            RoutePoint routePoint = new RoutePoint(newPoint,
                    newPosition + edgesPositions[edgeIndex], edgeDistance);
            closestPoint = closestPoint.min(routePoint);
            ++edgeIndex;
        }

        return closestPoint;
    }

    /**
     * @param position
     *          Position sur l'itinéraire du point exprimé en coordonnées suisses donné
     *
     * @return l'altitude à la position donnée le long de l'itinéraire si l'arête possédant le point a un profil,
     * retourne Double.Nan si elle n'en a pas
     */
    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0,position,length());
        int edgeIndex = edgeSearch(position);

        double edgePosition = position - edgesPositions[edgeIndex];
        return edges
                .get(edgeIndex)
                .elevationAt(edgePosition);
    }

    /**
     * @param position
     *          Position sur l'itinéraire du point exprimé en coordonnées suisses donné
     *
     * @return l'index de l'arête contenant la position donnée en paramètre
     */
    private int edgeSearch(double position) {
        int edgeIndex = binarySearch(edgesPositions, position);
        if (edgeIndex == edges.size())
            return edgeIndex-1;
        if (edgeIndex >= 0)
            return edgeIndex;

        return -(edgeIndex + 2);

    }
}

