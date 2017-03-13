package edu.ustc.cs.alg.slic;

import edu.ustc.cs.alg.ShortestPathStrategy;
import edu.ustc.cs.model.edge.Edge;
import org.jgrapht.Graph;

import java.util.List;

/**
 * Created by zyb on 2017/3/10.
 */
public class TNR<V, E extends Edge> implements ShortestPathStrategy<V,E> {

    Graph<V, Edge> graph;
    List<E> transNodes;

    public TNR(Graph<V, Edge> graph){
        this.graph = graph;
        generateTransNodes();
    }

    //生成中转节点
    public void generateTransNodes(){

    }

    public void init(){

    }

    @Override
    public List<V> getPath(V source, V sink) {
        return null;
    }

    @Override
    public double getPathWeight(V source, V sink) {
        return 0;
    }
}
