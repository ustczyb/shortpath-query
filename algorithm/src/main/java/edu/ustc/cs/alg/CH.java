package edu.ustc.cs.alg;


import edu.ustc.cs.model.edge.Edge;
import edu.ustc.cs.model.edge.ShortCut;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.ListSingleSourcePathsImpl;

import java.util.*;

/**
 * Created by zyb on 2017/2/22.
 */
public class CH<V, E extends Edge> implements ShortestPathStrategy<V,E> {

    private List<V> order;
    private Graph<V, Edge> graph;
    private DijkstraShortestPath<V, Edge> dijkstra;

    public CH(List<V> order, Graph<V, Edge> graph) {
        this.order = order;
        this.graph = graph;
        dijkstra = new DijkstraShortestPath<V, Edge>(graph);
    }

    public List<V> generateOrder(Graph<V,E> graph){
        List<V> order = new ArrayList<V>();
        //TODO generate the order
        return order;
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

    @Override
    public List<V> getPath(V source, V sink){
        return getGraphPath(source,sink).getVertexList();
    }

    public GraphPath<V, Edge> getGraphPath(V source, V sink) {
        Set<V> vexs = graph.vertexSet();
        int min = Math.min(order.indexOf(source),order.indexOf(sink));
        List<V> list = new ArrayList<V>();
        for(V v : vexs){
            if(order.indexOf(v) < min){
                list.add(v);
            }
        }
        //TODO 可优化 手写双向dijstra算法可以减少图的复制操作
        graph.removeAllVertices(list);
        BidirectionalDijkstraShortestPath<V,Edge> bidirectionalDijkstraShortestPath = new BidirectionalDijkstraShortestPath<V, Edge>(graph);
        return bidirectionalDijkstraShortestPath.getPath(source,sink);
    }

    @Override
    public double getPathWeight(V source, V sink) {
        GraphPath<V, Edge> p = getGraphPath(source, sink);
        if (p == null) {
            return Double.POSITIVE_INFINITY;
        } else {
            return p.getWeight();
        }
    }

    public ShortestPathAlgorithm.SingleSourcePaths<V, Edge> getPaths(V source) {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException("graph must contain the source vertex");
        }

        Map<V, GraphPath<V, Edge>> paths = new HashMap<>();
        for (V v : graph.vertexSet()) {
            paths.put(v, getGraphPath(source, v));
        }
        return new ListSingleSourcePathsImpl<V,Edge>(graph, source, paths);
    }
}
