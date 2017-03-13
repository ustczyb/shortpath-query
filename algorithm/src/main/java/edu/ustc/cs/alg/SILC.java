package edu.ustc.cs.alg;

import edu.ustc.cs.model.edge.Edge;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.GraphWalk;

import java.util.*;

/**
 * Created by zyb on 2017/3/7.
 * TODO 有向图无向图 图的格式 点的格式（坐标？）
 */
public class SILC<V,E extends Edge> implements ShortestPathStrategy<V,Edge> {

    Hashtable<V,Hashtable<V,HashSet<V>>> hashtable;
    private Graph<V, Edge> graph;
    private DijkstraShortestPath<V, Edge> dijkstra;

    public SILC(Graph<V,Edge> graph){
        this.graph = graph;
        dijkstra = new DijkstraShortestPath<V, Edge>(graph);
    }

    public void init(){
        hashtable = new Hashtable<>();
        Set<V> vSet = graph.vertexSet();
        //TODO 有向图的做法
        for(V v : vSet){
            Hashtable<V, HashSet<V>> table = new Hashtable<V, HashSet<V>>();
            for(V w : vSet){
                if(w != v){
                    GraphPath<V,Edge> path = dijkstra.getPath(v,w);
                    List<V> list = path.getVertexList();
                    //v->w最短路径上的第一个顶点
                    V firstV = list.get(1);
                    if(table.get(firstV) == null){
                        HashSet<V> set = new HashSet<>();
                        set.add(w);
                        table.put(firstV,set);
                    } else{
                        table.get(firstV).add(w);
                    }
                }
            }
            hashtable.put(v,table);
        }
    }

    @Override
    public List<V> getPath(V source, V sink) {
        if(source == sink){
            List<V> path = new ArrayList<V>();
            path.add(source);
            return path;
        } else {
            Hashtable<V, HashSet<V>> table = hashtable.get(source);
            for(V v : table.keySet()){
                HashSet<V> set = table.get(v);
                if(set.contains(sink)){
                    List<V> list =  getPath(v,sink);
                    list.add(source);
                    return list;
                }
            }
            return null;
        }
    }

    @Override
    public double getPathWeight(V source, V sink) {
        return 0;
    }

}
