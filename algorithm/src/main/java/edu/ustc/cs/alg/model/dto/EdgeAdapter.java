package edu.ustc.cs.alg.model.dto;

import edu.ustc.cs.alg.model.edge.Edge;
import org.jgrapht.graph.DefaultWeightedEdge;
import routing.Node;
import routing.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyb on 2017/4/7.
 * 边类的适配器，为了使generator生成的边能够让我们的算法使用（实现Edge接口）而不去更改generator的源码
 */
public class EdgeAdapter extends DefaultWeightedEdge implements Edge {

    private long id;
    private Node source;
    private Node target;
    private Double length;
    private Room room;
    private short floor;
    private short flag;
    private String name;
    private routing.Edge edge;

    public EdgeAdapter(routing.Edge edge){
        this.edge = edge;
        this.id = edge.getID();
        this.source = edge.getNode1();
        this.target = edge.getNode2();
        this.length = edge.getLength();
        this.room = edge.getRoom();
        this.floor = edge.getFloor();
        this.flag = edge.getFlag();
        this.name = edge.getName();
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public EdgeAdapter reverse(){
        EdgeAdapter reverseEdge = new EdgeAdapter(this.edge);
        reverseEdge.setSource(target);
        reverseEdge.setTarget(source);
        return reverseEdge;
    }

    @Override
    public String toString() {
        return "EdgeAdapter{" +
                "id=" + id +
                ", source=" + source.getID() +
                ", target=" + target.getID() +
                ", length=" + length +
                ", room=" + room +
                ", floor=" + floor +
                ", flag=" + flag +
                ", name='" + name + '\'' +
                '}';
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    @Override
    public Node getAnotherVertex(Object v) {
        if(source.equals(v)){
            return target;
        } else if(target.equals(v)){
            return source;
        } else {
            return null;
        }
    }

    @Override
    public List<Node> getVertexs() {
        List<Node> result = new ArrayList<Node>(2);
        result.add(source);
        result.add(target);
        return result;
    }

    @Override
    public double getWeight(){
        return length;
    }

    @Override
    public Double getLength(){
        return length;
    }

    @Override
    public int compareTo(Object o) {
        Edge e = (Edge) o;
        if(getWeight() > e.getLength()){
            return 1;
        } else if(getWeight() == e.getLength()){
            return 0;
        } else {
            return -1;
        }
    }
}
