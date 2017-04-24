package edu.ustc.cs.alg.model.edge;

import java.util.List;

/**
 * Created by zyb on 2017/2/28.
 */
public interface Edge<V> extends Comparable{

    V getAnotherVertex(V v);

    List<V> getVertexs();

    V getSource();

    V getTarget();

    Double getLength();
}
