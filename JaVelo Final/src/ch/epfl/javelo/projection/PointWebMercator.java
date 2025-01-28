package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * Représente un point dans le système de coordonnées suisse
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */

public record PointWebMercator(double x, double y) {

    /**
     * Puissance à laquelle deux est élevée pour obtenir le nombre de pixels de large de l'image au niveau de zoom 0
     */
    private static final int EIGHT = 8;

    /**
     *  Valide les coordonnées qu'il reçoit
     *
     * @param x
     *      Coordonnée x dans le système WebMercator du point
     * @param y
     *      Coordonnée y dans le système WebMercator du point
     * @throws IllegalArgumentException
     *      si au moins une des coordonnées x ou y n'est pas compris dans [0;1]
     */
    public PointWebMercator {
        Preconditions.checkArgument(x<=1 && x>=0 && y<=1 && y>=0);
    }

    /**
     *  Applique le zoom aux coordonnées x et y du point dans le système WebMercator
     *
     * @param zoomLevel
     *      Niveau de zoom
     * @param x
     *      Coordonnée x du point dans le système WebMercator
     * @param y
     *      Coordonnée y du point dans le système WebMercator
     *
     * @return le point dont les coordonnées sont x et y au niveau de zoom zoomLevel
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        return new PointWebMercator( Math.scalb( x, -EIGHT-zoomLevel )
                , Math.scalb( y, -EIGHT-zoomLevel ) );
    }

    /**
     * Convertit un point en coordonnées suisses dans le système de WebMercator
     *
     * @param pointCh
     *      Point en coordonnes suisses
     *
     * @return le point Web Mercator correspondant au point du système de coordonnées suisse donné
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator( WebMercator.x( pointCh.lon() ), WebMercator.y( pointCh.lat() ) );
    }

    /**
     * Applique le zoom sur la coordonnée x d'un point en coordonnées Web Mercator
     *
     * @param zoomLevel
     *      Niveau de zoom
     *
     * @return la coordonnée x au niveau de zoom donné
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb( this.x, EIGHT+zoomLevel );
    }

    /**
     * Applique le zoom sur la coordonnée y d'un point en coordonnées Web Mercator
     *
     * @param zoomLevel
     *      Niveau de zoom
     *
     * @return la coordonnée y au niveau de zoom donné
     */
    public double yAtZoomLevel (int zoomLevel) {
        return Math.scalb( this.y, EIGHT+zoomLevel );
    }

    /**
     * Convertit la coordonnée x du point, dans le système Web Mercator en la longitude du point,
     * en coordonnées WGS 84
     *
     * @return la longitude du point, en radians
     */
    public double lon () {
        return WebMercator.lon(this.x);
    }

    /**
     * Convertit la coordonnée y des coordonnées Web Mercator en la longitude, en coordonnées WGS 84, du point this
     * @return la latitude du point, en radians
     */
    public double lat (){
        return WebMercator.lat(this.y);
    }

    /**
     * Convertit un point dans le système Web Mercator en coordonnées suisses.
     *
     * @return le point de coordonnées suisses se trouvant à la même position que le récepteur (this), null sinon
     */
    public PointCh toPointCh (){
        double e = Ch1903.e( this.lon(), this.lat() );
        double n = Ch1903.n( this.lon(), this.lat() );
        return ( SwissBounds.containsEN(e,n) ) ? new PointCh(e,n) : null ;
    }
}
