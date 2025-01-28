package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * Représente un point dans le système de coordonnées suisse
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */

public record PointCh(double e, double n) {

    /**
     * Valide les coordonnées qu'il reçoit
     *
     * @param e
     *      Coordonnée E du point dans le système de coordonnées suisses
     * @param n
     *      Coordonnée E du point dans le système de coordonnées suisses
     *
     * @throws IllegalArgumentException
     *      si le couple de coordonnées (e,n) définit un point qui n'est pas dans les limites suisses
     */
    public PointCh {
        Preconditions.checkArgument( SwissBounds.containsEN(e,n)  );
    }

    /**
     * Permet d'effectuer un calcul d'une distance au carre entre deux points de coordonnées suisses
     *
     * @param that
     *      Point en coordonnées suisses
     *      
     * @return le carré de la distance en mètres séparant le récepteur (this) de l'argument that
     */
    public double squaredDistanceTo(PointCh that) {
        double eDifference = this.e() - that.e();
        double nDifference = this.n() - that.n();
        return Math2.squaredNorm( eDifference,nDifference );
    }

    /**
     * Permet d'effectuer un calcul de distance entre deux points de coordonnées suisses
     *
     * @param that
     *      Point en coordonnées suisses
     *
     * @return la distance en mètres séparant le récepteur (this) de l'argument that
     */
    public double distanceTo(PointCh that) {
        double eDifference = this.e() - that.e();
        double nDifference = this.n() - that.n();
        return Math2.norm( eDifference,nDifference );
    }

    /**
     * Appelle la méthode de même nom de la classe Ch1903
     *
     * @return la longitude du point, dans le système WGS84 en radians
     */
    public double lon() {
        return Ch1903.lon( this.e(), this.n() );
    }

    /**
     * Appelle la méthode de même nom de la classe Ch1903
     *
     * @return la latitude du point, dans le système WGS84 en radians
     */
    public double lat() {
        return Ch1903.lat( this.e(), this.n() );
    }

}
