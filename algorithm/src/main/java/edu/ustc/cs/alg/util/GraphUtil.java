package edu.ustc.cs.alg.util;

import edu.ustc.cs.alg.model.edge.Edge;
import edu.ustc.cs.alg.model.path.ShortestPath;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.Hashtable;
import java.util.Set;

/**
 * Created by zyb on 2017/3/13.
 */
public class GraphUtil {

    public static <V,E> Hashtable<V,Integer> getVexIndex(Graph<V,E> graph){
        Set<V> set = graph.vertexSet();
        Hashtable<V,Integer> hashtable = new Hashtable<V, Integer>(set.size());
        int i = 0;
        for(V v : set){
            hashtable.put(v, i++);
        }
        return hashtable;
    }

    /*
    把b图添加到a图中
     */
    public static <V, E> void addGraph(Graph<V, Edge> a, Graph<V, Edge> b){
        for(V v :b.vertexSet()){
            a.addVertex(v);
        }
        for(Edge<V> e : b.edgeSet()){
            a.addEdge(e.getSource(),e.getTarget(),e);
        }
    }

    public static ShortestPath graphPath2SP(GraphPath graphPath){
        return new ShortestPath(graphPath.getVertexList(),graphPath.getEdgeList(),graphPath.getWeight());
    }


}
