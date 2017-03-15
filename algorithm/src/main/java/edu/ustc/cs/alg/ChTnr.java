package edu.ustc.cs.alg;

import edu.ustc.cs.model.edge.Edge;
import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public ChTnr(Graph<V,E> graph,List<V> order){
        this.graph = graph;
        this.order = order;
        transNode = order.subList(0, (int) Math.sqrt(graph.vertexSet().size()));
    }

    public List<V> generateOrder(Graph<V,E> graph){
        List<V> order = new ArrayList<V>();
        //TODO generate the order
        return order;
    }

    private boolean isForwardEdge(Edge<V> edge, V v){
        if(order.indexOf(v) < order.indexOf(edge.getAnotherVertex(v))){
            return true;
        } else{
            return false;
        }
    }

    public Set<V> findForwardAccessNode(V v){
        Set<V> set = new HashSet<V>();
        for(Edge<V> edge : graph.edgesOf(v)){
            if(isForwardEdge(edge,v)){
                V w = edge.getAnotherVertex(v);
                if(transNode.contains(w)){
                    set.add(w);
                } else {
                    set.addAll(findForwardAccessNode(w));
                }
            }
        }
        return set;
    }

    public Set<V> findBackwardAccessNode(V v){
        Set<V> set = new HashSet<V>();
        for(Edge<V> edge : graph.edgesOf(v)){
            if(!isForwardEdge(edge,v)){
                V w = edge.getAnotherVertex(v);
                if(transNode.contains(w)){
                    set.add(w);
                } else {
                    set.addAll(findBackwardAccessNode(w));
                }
            }
        }
        return set;
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
