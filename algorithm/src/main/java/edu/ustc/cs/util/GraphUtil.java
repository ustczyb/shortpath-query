package edu.ustc.cs.util;

import org.jgrapht.Graph;

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
}
