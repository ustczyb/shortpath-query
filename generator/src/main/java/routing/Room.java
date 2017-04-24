package routing;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Ulf on 13/06/15.
 */
public class Room {

    ArrayList<Edge> edges = new ArrayList<>();
    ArrayList<Node> nodes = new ArrayList<>();
    int ID;

    short usage = 0;
    int capacity = 0;

    private int floor = 0;

    public Room(ArrayList<Edge> edges) {
        this.edges = edges;
        setRoomOnEdges();
    }

    public Room(int ID, ArrayList<Edge> edges) {
        this.ID = ID;
        this.edges = edges;
        setRoomOnEdges();
    }

    private void setRoomOnEdges(){
        for (Edge e : edges){
            e.setRoom(this);
        }
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<Edge> edges) {
        //Update the room capacity
        capacity = 0;
        for (Edge e : edges){
            capacity += e.getCapacity();
        }
        //Update the capacity for all edges inside the room so they are equal to the room capacity
        for (Edge e : edges){
            e.setCapacity(capacity);
        }
        this.edges = edges;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public void addNode(Node n){
        nodes.add(n);
    }

    public short getUsage() {
        return usage;
    }

    public void setUsage(short usage) {
        this.usage = usage;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void incUsage(){ //Increments the usage of the room and makes sure the all edges inside it have the same usage as the room
        usage++;
        for (Edge e : edges){
            e.setUsage(usage);
        }
    }

    public void decUsage(){
        usage--;
        for (Edge e : edges){
            e.setUsage(usage);
        }
    }
}
