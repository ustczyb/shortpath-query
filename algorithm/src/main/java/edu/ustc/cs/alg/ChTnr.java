package edu.ustc.cs.alg;

import edu.ustc.cs.model.edge.Edge;
import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyb on 2017/3/10.
 */
public class ChTnr<V,E extends Edge> implements ShortestPathStrategy<V,E> {

    private Graph<V,E> graph;
    private List<V> order;
    private List<V> transNode;

    public ChTnr(Graph<V,E> graph){
        this.graph = graph;
        order = generateOrder(graph);
        transNode = order.subList(0, (int) Math.sqrt(graph.vertexSet().size()));
    }

    public List<V> generateOrder(Graph<V,E> graph){
        List<V> order = new ArrayList<V>();
        //TODO generate the order
        return order;
    }

    private List<V> findAccessNode(V v){
        List<V> list = new ArrayList<V>();
        //TODO
        return list;
    }

    public void init(){

    }

    @Override
    public List<V> getPath(V source, V sink) {
        return null;
    }

    @Override
    public double getPathWeight(V source, V sink) {
        return 0;
    }
}
