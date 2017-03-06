package edu.ustc.cs.demo;


import edu.ustc.cs.model.edge.Edge;
import edu.ustc.cs.model.edge.ShortCut;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.*;

/**
 * Created by zyb on 2017/2/22.
 */
public class CH<V, E extends Edge> {

    private List<V> order;
    private Graph<V, Edge> graph;
    private DijkstraShortestPath<V, Edge> dijkstra;

    public CH(List<V> order, Graph<V, Edge> graph) {
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
        return null;
    }

}
