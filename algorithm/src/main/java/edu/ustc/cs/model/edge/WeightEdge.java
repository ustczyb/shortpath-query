package edu.ustc.cs.model.edge;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Created by zyb on 2017/3/1.
 * jGraphT中有向边必须继承DefaultWeightedEdge并且重写getWeight方法，否则在使用jGraphT中的计算边权重的算法时边的权重为默认值1
 */
public class WeightEdge<V> extends DefaultWeightedEdge implements Edge<V> {
//TODO 优先队列错误问题
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
