package edu.ustc.cs.demo;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

/**
 * Created by zyb on 2017/2/28.
 */
public class BiDijkstra<V,E> {

    private Graph<V,E> graph;
    private DijkstraShortestPath<V,E> dijkstra;

    public BiDijkstra(Graph<V, E> graph) {
        this.graph = graph;
        dijkstra = new DijkstraShortestPath<V, E>(graph);
    }

    public GraphPath<V,E> getShortPath(V v1, V v2) {


        return null;
    }



}
