package edu.ustc.cs.alg.alg;

import edu.ustc.cs.alg.model.edge.Edge;

import java.util.List;

/**
 * Created by zyb on 2017/3/6.
 */
public interface ShortestPathStrategy<V, E extends Edge> {
    //ShortestPath getPath(V source, V sink);
    List<V> getPathVertex(V source, V sink);
    double getPathWeight(V source, V sink);
}
