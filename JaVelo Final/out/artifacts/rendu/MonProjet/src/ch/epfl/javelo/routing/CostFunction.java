package ch.epfl.javelo.routing;

public interface CostFunction {
    /**
     * @param nodeId
     *          identité du noeud de départ de l'arête d'identité edgeId
     * @param edgeId
     *          identité d'une arête
     *
     * @return le facteur par lequel la longueur de l'arête d'identité edgeId,
     * partant du nœud d'identité nodeId, doit être multipliée
     */
    double costFactor(int nodeId, int edgeId);
}
