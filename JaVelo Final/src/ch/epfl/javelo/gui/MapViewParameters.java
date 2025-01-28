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
     * Permet d'obtenir les coordonnées du coin haut-gauche de la partie de la carte visible
     *
     * @return les coordonnées du coin haut-gauche sous la forme d'un objet de type Point2D
     */
    public Point2D topLeft () {
        return new Point2D(x,y);
    }

    /**
     * Permet de retourner une instance de MapViewParameters dont le coin haut-gauche est différent
     *
     * @param x
     *          Coordonnée x d'un point
     * @param y
     *          Coordonnée y d'un point
     *
     * @return une instance de MapViewParameters avec niveau de zoom identique mais coordonneés x/y différentes
     */
    public MapViewParameters withMinXY(double x, double y) {
        return new MapViewParameters(this.zoomLevel, x, y);
    }

    /**
     * Permet de déterminer la position du point passé en arguments sur la carte
     *
     * @param x
     *          Coordonnée x d'un point
     * @param y
     *          Coordonnée y d'un point
     *
     * @return le pointWebMercator correspondant aux coordonnées prises en arguments
     */
    public PointWebMercator pointAt(double x, double y) {
        return PointWebMercator.of(this.zoomLevel, x+this.x, y+this.y);
    }

    /**
     * Permet de déterminer la coordonnée x d'un pointWebMercator par rapport au coin haut-gauche
     *
     * @param point
     *          Point en coordonnées WebMercator
     *
     * @return la coordonnée x correspondante au point pris en argument
     */
    public double viewX(PointWebMercator point) {
        return point.xAtZoomLevel(zoomLevel)-this.x;
    }

    /**
     * Permet de déterminer la coordonnée y d'un pointWebMercator par rapport au coin haut-gauche
     *
     * @param point
     *          Point en coordonnée WebMercator
     *
     * @return la coordonnée y correspondante au point pris en argument
     */
    public double viewY(PointWebMercator point) {
        return point.yAtZoomLevel(zoomLevel)-this.y;
    }

}
