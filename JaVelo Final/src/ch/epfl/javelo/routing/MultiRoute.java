package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Permet de représenter un itinéraire multiple (une séquence d'itinéraire  contigus nommés segments)
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */

public final class MultiRoute implements Route{

    private final List<Route> segments;

    private final List <Edge> edges;

    private final List <PointCh> points;

    private final double length;

    double[] segmentsPositions;

    /**
     * Construit un itinéraire multiple composé des segments donnés
     *
     * @param segments
     *          itinéraires
     *
     * @throws IllegalArgumentException si laliste de segments est vide
     */
    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);

        segmentsPositions = new double[segments.size()+1];
        int tabIndex = 0;
        for (Route s : segments)
            segmentsPositions[++tabIndex] = s.length() + segmentsPositions[tabIndex - 1];
        length = segmentsPositions[tabIndex];

        List <Edge> edges = new ArrayList<>();
        for (Route s : segments)
            edges.addAll(s.edges());
        this.edges = List.copyOf(edges);

        List<PointCh> points = new ArrayList<>();
        for (Route s : segments) {
            points.addAll(s.points());
            points.remove(points.size()-1);
        }
        List <PointCh> lastPointsList = segments
                .get(segments.size()-1)
                .points();
        int listSize = lastPointsList.size()-1 ;
        points.add( lastPointsList.get(listSize) );

        this.points = Collections.unmodifiableList(points);
    }

    /**
     * Permet de determiner quel est le segment de l'itinéraire qui possède a position en question
     * @param position
     *          Position sur l'itinéraire du point exprimé en coordonnées suisses donné
     *
     * @return l'index du segment de l'itinéraire contenant la position donnée
     */
    @Override
    public int indexOfSegmentAt(double position) {
        int indexSegment = 0, positionIndex = 0;
        position = Math2.clamp(0,position,length);

        for (double d : segmentsPositions) {
            if (d!=0) {
                Route segment = segments.get(positionIndex);
                double segmentPosition = position - segmentsPositions[positionIndex];
                if (d >= position) {
                    int segmentIndex = segment.indexOfSegmentAt(segmentPosition);
                    return indexSegment + segmentIndex;
                }
                else {
                    int segmentIndex = segment.indexOfSegmentAt(position) + 1 ;
                    indexSegment += segmentIndex;
                    ++positionIndex;
                }
            }
        }
        return indexSegment;
    }

    /**
     * Permet de déterminer la longueur de l'itinéraire, en mètres
     *
     * @return la longueur de l'itinéraire, en mètres
     */
    @Override
    public double length() {
        return length;
    }
    /**
     * Permet de déterminer la totalité des arêtes que possède l'itinéraire
     *
     * @return la liste de la totalité des arêtes de l'itinéraire
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }

    /**
     * Permet de déterminer  la totalité des points situés aux extrémités des arêtes de l'itinéraire
     *
     * @return la liste de la totalité des points situés aux extrémités des arêtes de l'itinéraire
     */
    @Override
    public List<PointCh> points() {
        return points;
    }

    /**
     * Permet de déterminer le point se trouvant à la position donnée le long de l'itinéraire
     *
     * @param position
     *          Position sur l'itinéraire du point exprimé en coordonnées suisses donné
     *
     * @return le point se trouvant à la position donnée le long de l'itinéraire
     */
    @Override
    public PointCh pointAt(double position) {
        int index = routeSearch(position);
        position = position - segmentsPositions[index] ;
        return segments
                    .get(index)
                    .pointAt(position);
    }

    /**
     * Permet de déterminer l'identité du nœud de l'itinéraire qui se trouve au plus proche
     * de la position donnée
     *
     * @param position
     *          Position sur l'itinéraire du point exprimé en coordonnées suisses donné
     *
     * @return l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     */
    @Override
    public int nodeClosestTo(double position) {
        int index = routeSearch(position);
        position = position - segmentsPositions[index] ;
        return segments
                    .get(index)
                    .nodeClosestTo(position);
    }

    /**
     * Permet de déterminer le point de l'itinéraire se trouvant le plus proche du point de référence donné
     *
     * @param point
     *          Point en coordonnées suisses
     *
     * @return le point de l'itinéraire se trouvant le plus proche du point de référence donné
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint closestPoint = segments
                                        .get(0)
                                        .pointClosestTo(point);
        int segmentIndex = 0;
        for (Route s : segments) {
            RoutePoint newPoint = s.pointClosestTo(point);
            closestPoint = closestPoint.min(newPoint);

            if (closestPoint.equals(newPoint)) {
                double segmentPosition = segmentsPositions[segmentIndex];
                closestPoint = closestPoint.withPositionShiftedBy(segmentPosition);
            }
            ++segmentIndex;
        }
        return closestPoint;
    }

    /**
     * Permet de déterminer l'altitude à la position donnée le long de l'itinéraire
     *
     * @param position
     *          Position sur l'itinéraire du point exprimé en coordonnées suisses donné
     *
     * @return l'altitude à la position donnée le long de l'itinéraire
     */
    @Override
    public double elevationAt(double position) {
        int index = routeSearch(position);
        position = position - segmentsPositions[index] ;
        return segments
                    .get(index)
                    .elevationAt(position);
    }


    /**
     * @param position
     *          position sur l'itinéraire
     *
     * @return l'index du segment contenant la position dans la liste de segment
     */
    private int routeSearch(double position) {
        if (position >= length())
            return segments.size() - 1;
        else if (position <= 0)
            return 0;
        else {
            int segmentIndex = -1;
            for (double l : segmentsPositions) {
                if (l >= position) break;
                ++segmentIndex;
            }
            return segmentIndex;
        }
    }
}






