package edu.ustc.cs.alg.model.path;

import edu.ustc.cs.alg.model.edge.Edge;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyb on 2017/3/16.
 */
public class ShortestPath<V, E extends Edge> implements Path<V, E>, Comparable {

    private List<V> vertexList;
    private List<E> edgeList;
    private Double weight;

    public ShortestPath(List<V> vertexList, List<E> edgeList, Double weight) {
        this.vertexList = vertexList;
        this.edgeList = edgeList;
        this.weight = weight;
    }

    public ShortestPath(List<E> edgeList) {
        this.edgeList = edgeList;
        vertexList = new ArrayList<V>();
        weight = 0.0;
        for(int i = 0; i < edgeList.size(); i++){
            Edge edge = edgeList.get(i);
            weight += edge.getLength();
            vertexList.addAll(edge.getVertexs());
            if(i < edgeList.size() -1){
                vertexList.remove(vertexList.size() - 1);
            }
        }
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

    @Override
    public int compareTo(Object o) {
        ShortestPath path2 = (ShortestPath) o;
        if(weight > path2.getWeight()){
            return 1;
        } else if(weight < path2.getWeight()){
            return -1;
        } else{
            return 0;
        }
    }

    public ShortestPath reverse() {
        ShortestPath path = new ShortestPath(reverse(vertexList), reverse(edgeList), weight);
        return path;
    }

    private <T> List<T> reverse(List<T> list){
        int size = list.size();
        int i = 0;
        List res = new ArrayList(size);
        for(T v : list){
            res.add(size - 1 - i, v);
            i++;
        }
        return res;
    }

}
