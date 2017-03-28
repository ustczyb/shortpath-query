package edu.ustc.cs.alg;

import edu.ustc.cs.model.edge.Edge;
import edu.ustc.cs.model.path.ShortestPath;

import java.util.List;

/**
 * Created by zyb on 2017/3/6.
 */
public interface ShortestPathStrategy<V, E extends Edge> {
    //ShortestPath getPath(V source, V sink);
    List<V> getPathVertex(V source, V sink);
    double getPathWeight(V source, V sink);
}
