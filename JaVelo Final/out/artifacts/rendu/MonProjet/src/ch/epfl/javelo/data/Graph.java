package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

/**
 * Permet de représenter le graphe JaVelo
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */
public final class Graph {

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;
    /**
     *
     * @param nodes
     *          Graphe comportant les noeuds
     * @param sectors
     *          Graphe comportant les secteurs
     * @param edges
     *          Graphe comportant les arêtes
     * @param attributeSets
     *          Graphe comportant les ensembles d'attributs
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    private static ByteBuffer bufferConstructor(Path path) throws IOException {
        ByteBuffer buffer;
        try (FileChannel channel = FileChannel.open(path)) {
            buffer = channel.map( FileChannel.MapMode.READ_ONLY, 0, channel.size() );
        }
        return buffer;
    }
    /**
     * Permet de charger un graphe depuis un répertoire
     *
     * @param basePath
     *          chemin d'accés au répertoire
     * @return le graphe JaVelo obtenu
     *
     * @throws IOException
     *          en cas d'erreur d'entrée/sortie
     */
    public static Graph loadFrom(Path basePath) throws IOException {

        Path nodesPath = basePath.resolve("nodes.bin");
        IntBuffer nodesBuffer = bufferConstructor(nodesPath).asIntBuffer();
        GraphNodes nodes = new GraphNodes(nodesBuffer);

        Path sectorsPath = basePath.resolve("sectors.bin");
        ByteBuffer sectorsBuffer = bufferConstructor(sectorsPath);
        GraphSectors sectors = new GraphSectors(sectorsBuffer);

        Path edgesPath = basePath.resolve("edges.bin");
        ByteBuffer edgesBuffer = bufferConstructor(edgesPath);

        edgesPath = basePath.resolve("profile_ids.bin");
        IntBuffer profilesBuffer = bufferConstructor(edgesPath).asIntBuffer();

        edgesPath = basePath.resolve("elevations.bin");
        ShortBuffer elevationsBuffer = bufferConstructor(edgesPath).asShortBuffer();

        GraphEdges edges = new GraphEdges(edgesBuffer, profilesBuffer, elevationsBuffer);

        Path attributesPath = basePath.resolve("attributes.bin");
        LongBuffer attributeBuffer = bufferConstructor(attributesPath).asLongBuffer();

        int size = attributeBuffer.capacity();
        List<AttributeSet> attributeSets = new ArrayList<>(size) {};
        for (int attribute=0 ; attribute < size ;++attribute)
            attributeSets.add( new AttributeSet( attributeBuffer.get(attribute) ) );

        return new Graph( nodes, sectors, edges, attributeSets );
    }
    /**
     * Permet de calculer le nombre de noeud d'un graphe
     *
     * @return le nombre total de noeuds dans le graphe
     */
    public int nodeCount() {
        return nodes.count();
    }

    /**
     * Permet de déterminer la position d'un noeud dont l'identité est donnée en paramètre
     *
     * @param nodeId
     *          Identité d'un noeud
     * @return la position du nœud d'identité donnée
     */
    public PointCh nodePoint(int nodeId) {
        return new PointCh( nodes.nodeE(nodeId), nodes.nodeN(nodeId) );
    }

    /**
     * Permet de déterminer le nombre  d'aretes d'un noeud dont l'identite est donnee en paramètre
     *
     * @param nodeId
     *           Identité d'une arête
     * @return le nombre d'arêtes sortant du nœud d'identité donnée
     */
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    /**
     * Permet de déterminer la edgeIndex-ième arête sortant d'un noeud dont l'identité est donnée en paramètre
     * @param nodeId
     *           Identité d'un noeud
     * @param edgeIndex
     *           Identité d'une arête
     * @return l'identité de la edgeIndex-ième arête sortant du nœud d'identité donnée
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId( nodeId, edgeIndex );
    }

    /**
     * Donne l'identité du noeud situé au plus proche du point
     * @param point
     *          Point en coordonnées suisses
     *
     * @param searchDistance
     *          Distance maximale
     * @return  l'identité du nœud se trouvant le plus proche du point donné
     */

    public int nodeClosestTo(PointCh point, double searchDistance) {
        double distMin = Double.POSITIVE_INFINITY;
        int nodeClosest = -1;
        List<GraphSectors.Sector> listSectors = sectors.sectorsInArea( point, searchDistance );

        for (GraphSectors.Sector sector : listSectors) {

            for (int nodeId = sector.startNodeId() ; nodeId < sector.endNodeId() ; ++nodeId) {
                PointCh node = nodePoint(nodeId);
                double newDist = node.squaredDistanceTo(point);

                if (nodeClosest != -1) {

                    if ( newDist < distMin ) {
                        nodeClosest = nodeId;
                        distMin = newDist;
                    }

                } else if ( node.distanceTo(point) <= searchDistance ) {
                    nodeClosest = nodeId;
                    distMin = newDist;
                }
            }
        }
        return nodeClosest;
    }


    /**
     * Permet de déterminer l'identité du noeud destination d'une arête dont l'identité est donnée
     * @param edgeId
     *          Identité d'une arête
     * @return  l'identité du nœud destination de l'arête d'identité donnée
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * Permet de savoir dans quel sens va une arête d'identité donnée selon la voie OSM dont elle provient
     * @param edgeId
     *          Identité d'une arête
     * @return vrai si et seulement si l'arête d'identité donnée va dans le sens
     * contraire de la voie OSM dont elle provient
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * Permet de connaitre l'ensemble des attributs OSM attachés à une arête dont l'identité est donnée
     *
     * @param edgeId
     *          Identité d'une arête
     *
     * @return  l'ensemble des attributs OSM attachés à l'arête d'identité donnée,
     */

    public AttributeSet edgeAttributes(int edgeId) {
        int attIndex = edges.attributesIndex(edgeId);
        return attributeSets.get(attIndex);
    }

    /**
     *  Permet de déterminer la longueur (en mètres) de l'arête d'identité donnée
     *
     * @param edgeId
     *          Identité d'une arête
     *
     * @return la longueur (en mètres) de l'arête d'identité donnée
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * Permet de déterminer le dénivelé positif total de l'arête d'identité donnée
     *
     * @param edgeId
     *          Identité d'une arête
     *
     * @return le dénivelé positif total de l'arête d'identité donnée
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * Permet sous la forme d'une fonction de determiner le profil en long d'une arête dont l'identité est donnée
     *
     * @param edgeId
     *          Identité d'une arête
     *
     * @return le profil en long de l'arête d'identité donnée,
     * sous la forme d'une fonction si l'arête possède un profil
     *         sinon cette fonction retourne Double.NaN pour n'importe quel argument
     */
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        return ( edges.hasProfile(edgeId) ) ?
                Functions.sampled( edges.profileSamples(edgeId), edgeLength(edgeId) )
                : Functions.constant(Double.NaN);
    }

}
