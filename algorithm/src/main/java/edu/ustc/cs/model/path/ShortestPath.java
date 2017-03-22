package edu.ustc.cs.model.path;

import edu.ustc.cs.model.edge.Edge;
import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyb on 2017/3/16.
 */
public class ShortestPath<V, E extends Edge> implements Path<V, E> {

    private List<V> vertexList;
    private List<E> edgeList;
    private Double weight;

    public ShortestPath(List<V> vertexList, List<E> edgeList, Double weight) {
        this.vertexList = vertexList;
        this.edgeList = edgeList;
        this.weight = weight;
    }

    public ShortestPath(List<V> vertexList) {
        this.vertexList = vertexList;
    }

    @Override
    public List getVertexList() {
        return vertexList;
    }

    @Override
    public List getEdgeList() {
        return edgeList;
    }

    @Override
    public Double getWeight() {
        return weight;
    }

    @Override
    public Path<V, E> append(Path path) throws Exception {
        if(this.vertexList.get(vertexList.size()-1).equals(path.getVertexList().get(0))){
            List<V> vList = new ArrayList<V>();
            vList.addAll(vertexList);
            vList.remove(vertexList.size()-1);
            vList.addAll(path.getVertexList());
            List<E> eList = new ArrayList<E>();
            eList.addAll(edgeList);
            eList.addAll(path.getEdgeList());
            Double weight = this.weight + path.getWeight();
            Path<V, E> appendPath = new ShortestPath<>(vList,eList,weight);
            return appendPath;
        } else{
            throw new Exception("cannot append these two paths");
        }

    }
}
