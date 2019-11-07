package edu.ustc.cs.alg.model.edge;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zyb on 2017/2/28.
 */
public interface Edge<V> extends Comparable, Serializable{

    /**
     * 获取另一个顶点
     * @param v
     * @return
     */
    V getAnotherVertex(V v);

    /**
     * 获取边的两个顶点
     * @return
     */
    List<V> getVertexs();

    /**
     * 获取有向边的起点
     * @return
     */
    V getSource();

    /**
     * 获取有向边的终点
     * @return
     */
    V getTarget();

    Double getLength();
}
