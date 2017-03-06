package edu.ustc.cs.model.edge;

import edu.ustc.cs.model.edge.Edge;

import java.util.List;

/**
 * Created by zyb on 2017/3/1.
 */
public class ShortCut<V> implements Edge<V> {

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
    public Double length() {
        return length;
    }

}
