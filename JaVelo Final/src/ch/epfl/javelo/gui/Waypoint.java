package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Représente un point de passage
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 *
 * @param position
 *          Position du point de passage dans le système de coordonnées suisse
 * @param closestNodeId
 *          L'identité du nœud JaVelo le plus proche de ce point de passage
 *
 */
public record Waypoint(PointCh position, int closestNodeId) { }
