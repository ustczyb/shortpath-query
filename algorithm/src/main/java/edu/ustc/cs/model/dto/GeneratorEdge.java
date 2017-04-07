package edu.ustc.cs.model.dto;

import edu.ustc.cs.model.edge.Edge;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyb on 2017/4/7.
 */
public class GeneratorEdge<V> extends DefaultWeightedEdge implements Edge<V> {

    private long id;
    private V source;
    private V target;
    private Double length;

    public GeneratorEdge(V source, V target, Double length) {
        this.source = source;
        this.target = target;
        this.length = length;
    }

    @Override
    public String toString() {
        return "GeneratorEdge{" +
                "source=" + source +
                ", target=" + target +
                ", length=" + length +
                '}';
    }

    public V getSource() {
        return source;
    }

    public V getTarget() {
        return target;
    }

    @Override
    public V getAnotherVertex(V v) {
        if(source.equals(v)){
            return target;
        } else if(target.equals(v)){
            return source;
        } else {
            return null;
        }
    }

    @Override
    public List<V> getVertexs() {
        List<V> result = new ArrayList<V>(2);
        result.add(source);
        result.add(target);
        return result;
    }

    @Override
    public double getWeight(){
        return length;
    }

    @Override
    public Double getLength(){
        return length;
    }

    @Override
    public int compareTo(Object o) {
        Edge e = (Edge) o;
        if(getWeight() > e.getLength()){
            return 1;
        } else if(getWeight() == e.getLength()){
            return 0;
        } else {
            return -1;
        }
    }
}
