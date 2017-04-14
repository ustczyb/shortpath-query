package edu.ustc.cs.alg.model.path;

import edu.ustc.cs.alg.model.edge.Edge;

import java.util.List;

/**
 * Created by zyb on 2017/3/16.
 */
public interface Path<V,E extends Edge> {

    List<V> getVertexList();
    List<E> getEdgeList();
    Double getWeight();
    Path<V, E> append(Path path) throws Exception;

}
