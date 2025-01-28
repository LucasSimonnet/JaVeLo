package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

/**
 * Permet de représenter le point d'un itinéraire le plus proche d'un point de référence donné qui se trouve
 * dans le voisinage de l'itinéraire
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */

public record RoutePoint(PointCh point, double position, double distanceToReference) {

    /**
     * représente un point inexistant
     */
    public static final RoutePoint NONE = new RoutePoint(null,Double.NaN,Double.POSITIVE_INFINITY);

    /**
     * Permet de donner un point identique au recepteur (this) mais dont la position est décalée de
     * la différence donnée
     *
     * @param positionDifference
     *          Difference de la position du point le long de l'itinéraire
     *
     * @return un point identique au recepteur (this) mais dont la position est décalée de la difference donnée
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return (positionDifference==0) ? this
        : new RoutePoint(point,position+positionDifference,distanceToReference);
    }
    /**
     *  Permet de determiner lequel des RoutePoints est le plus proche à la référence
     *
     * @param that
     *          Distance à la référence de la position donné
     *
     * @return this si sa distance à la référence est inférieure ou égale à celle de that, retourne that sinon
     */
    public RoutePoint min (RoutePoint that) {
        return ( distanceToReference <= that.distanceToReference() ) ? this : that;
    }

    /**
     *  Permet de determiner s'il faut creer une nouvelle instance de RoutePoint
     *
     * @param thatPoint
     *          Point de référence en coordonnées suisses
     *
     * @param thatPosition
     *          Position sur l'arête du point de référence exprimé en coordonnées suisses donné en paramètre
     *
     * @param thatDistanceToReference
     *          Distance en mètres entre le point et la référence
     *
     * @return this si sa distance à la référence est inférieure ou égale à thatDistanceToReference,
     * retourne une nouvelle instance de RoutePoint sinon
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        return ( distanceToReference <= thatDistanceToReference ) ? this
                : new RoutePoint(thatPoint,thatPosition,thatDistanceToReference);
    }

}
