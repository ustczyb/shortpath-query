package edu.ustc.cs.demo;


import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Set;

/**
 * Created by zyb on 2017/2/22.
 */
public class CH<V, Edge> {

    private List<V> order;
    private Graph<V, Edge> graph;
    private DijkstraShortestPath<V, Edge> dijkstra;

    public CH(List<V> order, Graph<V, Edge> graph) {
        this.order = order;
        this.graph = graph;
        dijkstra = new DijkstraShortestPath<V, Edge>(graph);
    }

    public void init(){
        
    }
}
