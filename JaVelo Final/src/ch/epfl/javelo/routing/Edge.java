package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import java.util.function.DoubleUnaryOperator;

/**
 * Offre des méthodes et eregistrement permettant de représenter une arête d'un itinéraire
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length,
                   DoubleUnaryOperator profile) {
    /**
     * Permet la construction d'une instance de Edge
     *
     * @param graph
     *          Graphe JaVelo
     * @param edgeId
     *          Identité d'une arête
     * @param fromNodeId
     *          Identité du nœud de départ de l'arête
     * @param toNodeId
     *          Identité du nœud d'arrivée de l'arête
     * @return une instance de Edge
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge( fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId),
                graph.edgeLength(edgeId), graph.edgeProfile(edgeId) );
    }
    /**
     * Permet de déterminer la position en long d'une arête, en mètres,  qui se trouve au plus proche du point donné
     *
     * @param point
     *          Point en coordonnées suisses
     *
     * @return la position le long de l'arête (en mètres) qui se trouve la plus proche du point donné
     */
    public double positionClosestTo(PointCh point) {
        double position = Math2.projectionLength(fromPoint.e(),fromPoint.n(),toPoint.e(),toPoint.n(),
                point.e(),point.n() );
        return position;
    }
    /**
     * Calcule la position  sur une arête d'un point donné
     *
     * @param position
     *          Position sur l'arête du point exprimé en coordonnées suisses donné
     *
     * @return le point se trouvant à la position donnée sur l'arête, exprimée en mètres
     */
    public PointCh pointAt(double position) {
        if (length<=0)
            return fromPoint();

        double x = position/length;

        double e = Math2.interpolate(fromPoint.e(), toPoint.e(), x);
        double n = Math2.interpolate(fromPoint.n(), toPoint.n(), x);

        return new PointCh(e,n);
    }

    /**
     * Permet de déterminer l'altitute (en mètres) à la position donnée sur l'arête
     *
     * @param position
     *          Position sur l'arête du point exprimé en coordonnées suisses donné
     *
     * @return  l'altitude, en mètres, à la position donnée sur l'arête
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }

}
