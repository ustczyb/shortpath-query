package edu.ustc.cs.model.edge;

/**
 * Created by zyb on 2017/2/28.
 */
public interface Edge<V> {

    V getAnotherVertex(V v);

    /**
     *  边的长度
     */
    Double length();
}
