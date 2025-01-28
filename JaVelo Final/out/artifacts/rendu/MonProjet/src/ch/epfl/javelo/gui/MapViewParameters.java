package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * Représente les paramètres du fond de carte présenté dans l'interface graphique
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 *
 * @param zoomLevel
 *          Niveau de zoom
 *
 * @param x
 *          Coordonnée x (dans le système de coordonnées Web Mercator) du coin haut-gauche de
 *          la portion de carte affichée
 *
 * @param y
 *          Coordonnée y (dans le système de coordonnées Web Mercator) du coin haut-gauche de
 *          la portion de carte affichée
 */
public record MapViewParameters(int zoomLevel, double x, double y) {

    /**
     * Determine les coordonnées du coin haut-gauche sous la forme d'un objet de type Point2D
     *
     * @return les coordonnées du coin haut-gauche sous la forme d'un objet de type Point2D
     */
    public Point2D topLeft () {
        return new Point2D(x,y);
    }

    /**
     * Permet de retourner une instance de MapViewParameters identique au récepteur
     *
     * @param x
     *          Coordonnée x d'un point
     * @param y
     *          Coordonnée y d'un point
     *
     * @return une instance de MapViewParameters identique au récepteur
     */
    public MapViewParameters withMinXY(double x, double y) {
        return new MapViewParameters(this.zoomLevel, x, y);
    }

    /**
     * Permet de determiner le point du coin haut-gauche sous la forme d'une instance de PointWebMercator
     *
     * @param x
     *          Coordonnée x d'un point
     * @param y
     *          Coordonnée y d'un point
     *
     * @return le point pris en argument ous la forme d'une instance de PointWebMercator
     */
    public PointWebMercator pointAt(double x, double y) {
        return PointWebMercator.of(this.zoomLevel, x+this.x, y+this.y);
    }

    /**
     * Permet de determiner la coordonnée x d'un point en coordonnée WebMercator par rapport au coin haut-gauche
     * de la portion de carte affichée à l'écran
     *
     * @param point
     *          Point en coordonnée WebMercator
     *
     * @return  la position x  correspondante au point, exprimée par rapport au coin haut-gauche de la portion de
     * carte affichée à l'écran
     */
    public double viewX(PointWebMercator point) {
        return point.xAtZoomLevel(zoomLevel)-this.x;
    }

    /**
     * Permet de determiner la coordonnée y d'un point en coordonnée WebMercator par rapport au coin haut-gauche
     * de la portion de carte affichée à l'écran
     *
     * @param point
     *          Point en coordonnée WebMercator
     *
     * @return  la position y  correspondante au point, exprimée par rapport au coin haut-gauche de la portion de
     * carte affichée à l'écran
     */
    public double viewY(PointWebMercator point) {
        return point.yAtZoomLevel(zoomLevel)-this.y;
    }

}
