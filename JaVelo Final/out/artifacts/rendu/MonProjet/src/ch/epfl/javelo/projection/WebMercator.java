package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * Convertit des coordonnées WGS 84 en coordonnées Web Mercator
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */

public final class WebMercator {

    private WebMercator() {}

    /**
     * Convertit la longitude d'un point, en coordonnée WGS 84, en la coordonnée x des coordonnées Web Mercator
     *
     * @param lon
     *      Longitude du point en coordonnée WGS 84
     *
     * @return la coordonnée x de la projection d'un point se trouvant à la longitude lon, donnée en radians
     */
    public static double x (double lon) {
        return ( 1/(2*Math.PI) ) * ( lon + Math.PI );
    }

    /**
     * Convertit la latitude d'un point, en coordonnée WGS 84, en la coordonnée y des coordonnées Web Mercator
     *
     * @param lat
     *      Longitude du point en coordonnée WGS 84
     *
     * @return la coordonnée y de la projection d'un point se trouvant à la latitude lat, donnée en radians
     */
    public static double y (double lat) {
        return ( 1/(2*Math.PI) ) * ( Math.PI - Math2.asinh( Math.tan(lat) ) );
    }

    /**
     * Convertit la coordonnée x des coordonnées Web Mercator en la longitude, en coordonnées WGS 84, d'un point
     *
     * @param x
     *      Coordonnée x du point en coordonnées Web Mercator
     * @return la longitude en radians d'un point dont la projection se trouve à la coordonnée x
     */
    public static double lon (double x) {
        return (2 * Math.PI * x) - Math.PI;
    }

    /**
     * Convertit la coordonnée y des coordonnées Web Mercator en la latitude, en coordonnées WGS 84, d'un point
     *
     * @param y
     *      Coordonnée y du point en coordonnées Web Mercator
     * @return la latitude en radians d'un point dont la projection se trouve à la coordonnée y
     */
    public  static double  lat (double y) {
        return Math.atan( Math.sinh ( Math.PI - 2*Math.PI*y ) );
    }
}
