package edu.ustc.cs.alg.model.edge;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

/**
 * Created by zyb on 2017/3/1.
 */
public class ShortCut<V> extends DefaultWeightedEdge implements Edge<V> {

    private Double length;
    private List<V> path;
    V source;
    V target;

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public List<V> getPath() {
        return path;
    }

    public void setPath(List<V> path) {
        this.path = path;
    }

    public V getSource() {
        return source;
    }

    public void setSource(V source) {
        this.source = source;
    }

    public V getTarget() {
        return target;
    }

    public void setTarget(V target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "ShortCut{" +
                "length=" + length +
                ", path=" + path +
                ", source=" + source +
                ", target=" + target +
                '}';
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
        return path;
    }

    @Override
    public double getWeight() {
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
