package edu.ustc.cs.model.edge;

/**
 * Created by zyb on 2017/3/1.
 */
public class WeightEdge<V> implements Edge<V> {

    private V source;
    private V target;
    private Double length;

    public WeightEdge(V source, V target, Double length) {
        this.source = source;
        this.target = target;
        this.length = length;
    }

    @Override
    public String toString() {
        return "WeightEdge{" +
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
    public Double length() {
        return length;
    }
}
