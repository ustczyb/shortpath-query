package edu.ustc.cs.demo;

import edu.ustc.cs.model.edge.Edge;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;

/**
 * Created by zyb on 2017/2/28.
 */
public class BiDijkstra<V,E extends Edge> implements ShortestPathAlgorithm<V,E> {

    private Graph<V,E> graph;
    private GraphPath<V,E> path;

    public BiDijkstra(Graph<V, E> graph) {
        this.graph = graph;
    }


    @Override
    public GraphPath getPath(V v1, V v2) {
        return null;
    }

    @Override
    public double getPathWeight(V v1, V v2) {
        return 0;
    }

    @Override
    public SingleSourcePaths getPaths(V v) {
        return null;
    }
}
