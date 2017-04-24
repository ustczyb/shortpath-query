package routing;

import util.Utility;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Nichlas on 25-03-2015.
 */
public class Building {
    /**
     * Is true if the building is meant to be placed like a multibuilding
     */
    private boolean isMultiBuilding = false;
    /**
    * ID of the building
     */
    private long id = 0;
    /**
    * Nodes inside the building
     */
    private ArrayList<Node> nodes = new ArrayList<>();
    /**
     * Edges inside the building
     */
    private ArrayList<Edge> edges = new ArrayList<>();
    /**
     * Nodes used for visualization
     */
    private ArrayList<Node> visualNodes = new ArrayList<>();
    /**
     * Edges used for visualization
     */
    private ArrayList<Edge> visualEdge = new ArrayList<>();
    /**
     * List of Rooms in the building
     */
    private ArrayList<Room> rooms = new ArrayList<>();
    /**
     * List of door nodes in the building
     */
    private ArrayList<Node> doorNodes = new ArrayList<>();

    //private Node doorNode = null;
    /**
     * Potential for a node to be a staircase going down
     */
    private Node downNode = null;
    /**
     * Potential for a node to be a staircase going up
     */
    private Node upNode;

    private int floorNumber;
    /*
    Width of building
     */
    private int width = 4;
    /*
    Height of building
     */
    private int height = 4;
    /*
    Buttom left x cord
     */
    private int buttomXCord;
    /*
    Buttom left y cord;
     */
    private int buttomYCord;

    public int getButtomXCord() {
        return buttomXCord;
    }

    public int getButtomYCord() {
        return buttomYCord;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() { return height;  }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public ArrayList<Node> getVisualNodes() {
        return visualNodes;
    }

    public void setVisualNodes(ArrayList<Node> visualNodes) {
        this.visualNodes = visualNodes;
    }

    public ArrayList<Edge> getVisualEdge() {
        return visualEdge;
    }

    public void setVisualEdge(ArrayList<Edge> visualEdge) {
        this.visualEdge = visualEdge;
    }

    public Node getUpNode() {
        return upNode;
    }

    public void setUpNode(Node upNode) {
        this.upNode = upNode;
    }

    public Node getDownNode() {
        return downNode;
    }

    public void setDownNode(Node downNode) {
        this.downNode = downNode;
    }

    public Building(long id, int width, int height, int buttomXCord, int buttomYCord) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.buttomXCord = buttomXCord;
        this.buttomYCord = buttomYCord;
    }

    public Building(long id, int width, int height, int buttomXCord, int buttomYCord, boolean isMultiBuilding) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.buttomXCord = buttomXCord;
        this.buttomYCord = buttomYCord;
        this.isMultiBuilding = isMultiBuilding;
    }

//    private void MakeMultiBuilding(){
//        double widthHeightRatio = width/height;
//        double numberOfBuildingBlocks = Math.ceil(widthHeightRatio);
//        int totalWidth = width;
//
//        width = width/(int)numberOfBuildingBlocks;
//
//        ArrayList<Node> originalNodes = new ArrayList<>(nodes);
//        ArrayList<Edge> originalEdges = new ArrayList<>(edges);
//        ArrayList<Node> originalVisualNodes = new ArrayList<>(visualNodes);
//        ArrayList<Edge> originalVisualEdges = new ArrayList<>(visualEdge);
//
//        Map<Node,Node> originalNodesMap = new HashMap<>();
//        Map<Node,Node> originalVisualNodesMap = new HashMap<>();
//
//        for (int i = 1; i < numberOfBuildingBlocks; i++) {
//            for (Node n : originalNodes) {
//                nodes.add(new Node(n.getX()+(width*i),n.getY(),Utility.getUniqueNodeID(),n.getFlag()));
//            }
//            for (Node n: originalVisualNodes) {
//                visualNodes.add(new Node(n.getX()+(width*i),n.getY(),Utility.getUniqueNodeID(),n.getFlag()));
//            }
//            for (Edge e: originalEdges) {
//                if(e.getName().contains("Stair")){
//                    continue;
//                }
//                edges.add()
//            }
//            for (Edge e : originalVisualEdges){
//
//            }
//        }
//    }


    public Building(){

    }

    public boolean isMultiBuilding() {
        return isMultiBuilding;
    }

    public void setMultiBuilding(boolean multiBuilding) {
        isMultiBuilding = multiBuilding;
    }

    @Override
    public String toString() {
        return "Building{" +
                "id=" + id +
//                ", doorCordX=" + doorNode.x +
//                ", doorCordY=" + doorNode.y +
                ", width=" + width +
                ", height=" + height +
                ", buttomXCord=" + buttomXCord +
                ", buttomYCord=" + buttomYCord +
                '}';
    }

    public long getId() {
        return id;
    }

//    public Node getDoorNode() { return doorNode; }

    public void InitializeIndoorStructure(){
        ArrayList<Node> tempNodes = Utility.getAllNodes();
        ArrayList<Edge> tempEdge = Utility.getAllEdges();

        for (Node n : nodes){
            tempNodes.add(n);
        }
        for (Node n : visualNodes){
            tempNodes.add(n);
        }

        for (Edge e : edges){
            tempEdge.add(e);
        }

        for (Edge e : visualEdge){
            tempEdge.add(e);
        }

        Utility.setAllEdges(tempEdge);
        Utility.setAllNodes(tempNodes);
    }

    public ArrayList<Node> getDoorNodes() {
        return doorNodes;
    }

    public void addDoorNode(Node node){
        doorNodes.add(node);
    }
}