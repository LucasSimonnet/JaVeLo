package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 *  Interface permettant de représenter un itinéraire simple ou multiple
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */
public interface Route {
    /**
     *  Permet de déterminer l'index du segment à la position donnée (en mètres)
     *
     * @param position
     *          Position sur l'itinéraire du point exprimé en coordonnées suisses donné
     *
     * @return l'index du segment à la position donnée (en mètres)
     */
    int indexOfSegmentAt(double position);

    /**
     * Permet de déterminer la longueur de l'itinéraire, en mètres
     *
     * @return la longueur de l'itinéraire, en mètres
     */
    double length();

    /**
     * Permet de déterminer la totalité des arêtes de l'itinéraire
     *
     * @return la totalité des arêtes de l'itinéraire
     */
    List<Edge> edges();

    /**
     * Permet de déterminer  la totalité des points situés aux extrémités des arêtes de l'itinéraire
     *
     * @return la totalité des points situés aux extrémités des arêtes de l'itinéraire
     */

    List<PointCh> points();

    /**
     * Permet de déterminer le point se trouvant à la position donnée le long de l'itinéraire
     *
     * @param position
     *          Position sur l'itinéraire du point exprimé en coordonnées suisses donné
     *
     * @return le point se trouvant à la position donnée le long de l'itinéraire
     */

    PointCh pointAt(double position);
    /**
     * Permet de déterminer l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche
     * de la position donnée
     *
     * @param position
     *          Position sur l'itinéraire du point exprimé en coordonnées suisses donné
     *
     * @return l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     */
    int nodeClosestTo(double position);
    /**
     * Permet de déterminer le point de l'itinéraire se trouvant le plus proche du point de référence donné
     *
     * @param point
     *          Point en coordonnées suisses
     *
     * @return le point de l'itinéraire se trouvant le plus proche du point de référence donné
     */
    RoutePoint pointClosestTo(PointCh point);

    /**
     * Permet de déterminer l'altitude à la position donnée le long de l'itinéraire
     *
     * @param position
     *          Position sur l'itinéraire du point exprimé en coordonnées suisses donné
     *
     * @return l'altitude à la position donnée le long de l'itinéraire
     */
    double elevationAt(double position);
}
