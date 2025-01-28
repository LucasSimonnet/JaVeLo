package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

/**
 * Représente un planificateur d'itinéraire
 *
 *
 * @author Abbassi Najmeddine (341889)
 * @author Simonnet Lucas (345619)
 */
public final class RouteComputer {

    private final Graph graph ;
    private final CostFunction costFunction;

    public final static float CHECKED_DISTANCE = Float.NEGATIVE_INFINITY;
    public final static double UNCHECKED_DISTANCE = Double.POSITIVE_INFINITY;

    /**
     *
     * @param graph
     *          graphe sur lequel l'itinéraire est calculé
     * @param costFunction
     *          fonction de coût
     */
   public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
   }

    /**
     * Permet de calculer l'itinéraire de coût total minimal entre startNodeId et endNodeId
     *
     * @param startNodeId
     *              Noeud de départ de l'itinéraire
     * @param endNodeId
     *              Noeud d'arrivée de l'itinéraire
     *
     * @return l'itinéraire de coût total minimal allant de startNodeId à endNodeId
     * ou null si aucun itinéraire n'existe.
     *
     * @throws IllegalArgumentException si startNodeId et endNodeId sont identiques
     */
    public Route bestRouteBetween (int startNodeId, int endNodeId){

       Preconditions.checkArgument(startNodeId != endNodeId);

       PointCh endNode = graph.nodePoint(endNodeId);

       PriorityQueue<WeightedNode> p = new PriorityQueue<>();

       double [] nodeDistance = new double [graph.nodeCount()];
       int [] predecessor = new int [graph.nodeCount()];
       Arrays.fill(nodeDistance,UNCHECKED_DISTANCE);
       nodeDistance[startNodeId] = 0;

       p.add(new WeightedNode(startNodeId,0));

       while (!p.isEmpty()){
           int indexN = p.remove().nodeId;
           if (nodeDistance[indexN]!= CHECKED_DISTANCE) {
               if (indexN == endNodeId) {
                   return createRoute(indexN, startNodeId, predecessor);
               }
                checkNodeDistance(indexN,nodeDistance,predecessor,p,endNode);
           }
       }
       return null;
   }


    private record WeightedNode(int nodeId, float distance)
            implements Comparable<WeightedNode> {
        @Override
        public int compareTo(WeightedNode that) {
            return Float.compare(this.distance, that.distance);
        }
    }

   private void checkNodeDistance(int indexN, double[] nodeDistance, int[] predecessor,
                                  PriorityQueue<WeightedNode> queue, PointCh endNode) {

       int firstEdgeId = graph.nodeOutEdgeId(indexN, 0);
       int lastEdgeId = firstEdgeId + graph.nodeOutDegree(indexN);

       for (int indexA = firstEdgeId; indexA < lastEdgeId; ++indexA) {
           int toN = graph.edgeTargetNodeId(indexA);
           double lengthA = graph.edgeLength(indexA) * costFunction.costFactor(indexN, indexA);
           double d = nodeDistance[indexN] + lengthA;

           if (d < nodeDistance[toN]) {
               nodeDistance[toN] = d;
               predecessor[toN] = indexN;
               PointCh actualNode = graph.nodePoint(toN);
               queue.add(new WeightedNode(toN, (float) (d + actualNode.distanceTo(endNode))));
           }
       }
       nodeDistance[indexN] = CHECKED_DISTANCE;
   }

   private Route createRoute(int indexN, int startNodeId, int[] predecessor) {
       List<Edge> edges = new LinkedList<>();
       while (indexN != startNodeId) {

           int fromPoint = predecessor[indexN];
           int firstEdgeId = graph.nodeOutEdgeId(fromPoint, 0);
           int lastEdgeId = firstEdgeId + graph.nodeOutDegree(fromPoint);

           for (int edgeId = firstEdgeId; edgeId < lastEdgeId; ++edgeId) {
               if (graph.edgeTargetNodeId(edgeId) == indexN) {
                   edges.add(0,Edge.of(graph, edgeId, fromPoint, indexN));
                   indexN = fromPoint;
               }
           }
       }
       return new SingleRoute(edges);
   }
}