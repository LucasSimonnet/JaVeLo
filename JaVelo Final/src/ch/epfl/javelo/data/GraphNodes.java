package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * Représente le tableau de tous les nœuds du graphe JaVelo
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 *
 *
 * @param buffer
 *          la mémoire tampon contenant la valeur des attributs de la totalité des nœuds du graphe
 */

public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    /**
     * Nombre de bits représentant la partie décimale
     */
    private static final int FOUR = 4;

    /**
     * Nombre de bits représentant la partie entière
     */
    private static final int TWENTY_EIGHT = 28;

    /**
     * Donne le nombre de noeuds contenus dans la mémoire tampon
     *
     * @return le nombre total de noeuds
     */
    public int count() {
        return buffer.capacity()/NODE_INTS;
    }

    /**
     * Donne la coordonnée Est dans le système suisse d'un noeud donné
     *
     * @param nodeId
     *          identité d'un noeud
     *
     * @return la coordonnée Est du noeud d'index nodeId
     */
    public double nodeE(int nodeId) {
        int nodeIndex = NODE_INTS*nodeId;
        return Q28_4.asDouble( buffer.get( nodeIndex ) );
    }

    /**
     * Donne la coordonnée Nord dans le système suisse d'un noeud donné
     *
     * @param nodeId
     *          identité d'un noeud
     *
     * @return la coordonnée Nord du noeud d'index nodeId
     */
    public double nodeN(int nodeId) {
        int nodeIndex = NODE_INTS*nodeId + OFFSET_N ;
        return Q28_4.asDouble( buffer.get(nodeIndex) );
    }

    /**
     * Donne le nombre d'arêtes sortant d'un noeud donné
     *
     * @param nodeId
     *          identité d'un noeud
     *
     * @return le nombre d'arêtes sortant du noeud d'index nodeId
     */
    public int outDegree(int nodeId) {
        int nodeIndex = NODE_INTS*nodeId + OFFSET_OUT_EDGES ;
        int edgeNumber = buffer.get(nodeIndex);
        return Bits.extractUnsigned(edgeNumber,TWENTY_EIGHT,FOUR);
    }

    /**
     * Donne l'identité de la edgeIndex-ième arête sortant d'un noeud donné
     *
     * @param nodeId
     *          identité d'un noeud
     * @param edgeIndex
     *           index de l'arête sortant du noeud nodeId
     *
     * @return l'identité de l'arête souhaité
     */

    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        int nodeIndex = NODE_INTS*nodeId + OFFSET_OUT_EDGES ;
        int firstEdgeId = buffer.get(nodeIndex);
        return Bits.extractUnsigned(firstEdgeId,0,TWENTY_EIGHT) + edgeIndex;
    }
}
