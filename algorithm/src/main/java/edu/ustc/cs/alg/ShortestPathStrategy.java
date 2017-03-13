package edu.ustc.cs.alg;

import edu.ustc.cs.model.edge.Edge;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;

import java.util.List;

/**
 * Created by zyb on 2017/3/6.
 */
public interface ShortestPathStrategy<V, E extends Edge> {
    List<V> getPath(V source, V sink);
    double getPathWeight(V source, V sink);
}
