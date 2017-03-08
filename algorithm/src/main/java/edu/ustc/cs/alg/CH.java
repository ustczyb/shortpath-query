package edu.ustc.cs.demo;


import edu.ustc.cs.model.edge.Edge;
import edu.ustc.cs.model.edge.ShortCut;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.*;

/**
 * Created by zyb on 2017/2/22.
 */
public class CH<V, E extends Edge> implements ShortestPathAlgorithm<V,E> {

    private List<V> order;
    private WeightedGraph<V, Edge> graph;
    private DijkstraShortestPath<V, Edge> dijkstra;

    public CH(List<V> order, WeightedGraph<V, Edge> graph) {
        this.order = order;
        this.graph = graph;
        dijkstra = new DijkstraShortestPath<V, Edge>(graph);
    }

    public void init(){
        HashSet<V> hashSet = new HashSet<V>();
        for(V v : order){
            hashSet.add(v);
            Set<Edge> edges = graph.edgesOf(v);
            List<V> list = new ArrayList<V>();          //层次低于v的相邻顶点
            for(Edge e : edges){
                if(!hashSet.contains(e.getAnotherVertex(v))){
                    list.add((V) e.getAnotherVertex(v));
                }
            }
            for(int i = 0; i < list.size(); i ++){
                for(int j = i+1; j < list.size(); j++){
                    V vi = list.get(i);
                    V vj = list.get(j);
                    GraphPath graphPath = dijkstra.getPath(vi,vj);
                    List<V> path = graphPath.getVertexList();
                    Double length = graphPath.getWeight();
                    if(path.contains(v)){

                        ShortCut<V> shortCut = new ShortCut<V>();
                        shortCut.setSource(vi);
                        shortCut.setTarget(vj);
                        shortCut.setPath(path);
                        shortCut.setLength(length);

                        graph.addEdge(vi,vj,shortCut);
                    }
                }
            }
        }
    }

    public GraphPath<V,E> queryShortPath(V v1, V v2){
        Set<V> vexs = graph.vertexSet();
        int min = Math.min(order.indexOf(v1),order.indexOf(v2));
        for(V v : vexs){
            if(order.indexOf(v) < min){
                graph.removeVertex(v);
            }
        }
        return null;
    }

    @Override
    public GraphPath<V, E> getPath(V source, V sink) {
        return null;
    }

    @Override
    public double getPathWeight(V source, V sink) {
        return 0;
    }

    @Override
    public SingleSourcePaths<V, E> getPaths(V source) {
        return null;
    }
}
