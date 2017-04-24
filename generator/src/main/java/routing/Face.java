package routing;
import com.sun.javafx.geom.Vec2d;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import util.MaximumRectangle;
import util.Utility;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

public class Face {

    private long id = 0;

    /**
     * The nodes that make up the maximum rectangle of the face
     */
    private ArrayList<Edge> maximumRectangleEdges = new ArrayList<>();
    /**
     * The edges that make up the maximum rectangle of the face
     */
    private ArrayList<Node> maximumRectangleNodes = new ArrayList<>();
    /**
     * The nodes that the face consists of
     */
    private ArrayList<Node> nodes = new ArrayList<>();
    /**
     * The edges that the face consists of
     */
    private ArrayList<Edge> edges = new ArrayList<>();

    public ArrayList<Building> buildings = new ArrayList<>();

    /**
     * Angle used for rotating the face back and forth when finding the maximum rectangle
     */
    private double angle = 0;

    public Face(long id, ArrayList<Node> nodes, int numOfBuildings) {
        this.id = id;
        this.nodes = nodes;
        this.edges = Utility.GetEdgesFromNodes(nodes);

        for (Edge e : edges){
            e.addFaces(this);
        }
    }

    public long getId() { return id; }

    public ArrayList<Node> getNodes()
    {
        return nodes;
    }

    public ArrayList<Edge> getEdges() { return edges; }

    public void addNode(Node n){ nodes.add(n); }

    public void addEdge(Edge e){ edges.add(e); }

    public void removeEdge(Edge e) { edges.remove(e); }

    public ArrayList<Edge> getMaximumRectangleEdges() {
        return maximumRectangleEdges;
    }

    public void setMaximumRectangleEdges(ArrayList<Edge> maximumRectangleEdges) {
        this.maximumRectangleEdges = maximumRectangleEdges;
    }

    public ArrayList<Node> getMaximumRectangleNodes() {
        return maximumRectangleNodes;
    }

    public void setMaximumRectangleNodes(ArrayList<Node> maximumRectangleNodes) {
        this.maximumRectangleNodes = maximumRectangleNodes;
    }

    //Finds the lowest x in the face
    public int LowerX(){
        int res = Integer.MAX_VALUE;
        for (Node n : nodes){
            if (n.x < res){
                res = n.x;
            }
        }
        return res;
    }

    //Finds the highest y in face
    public int HighestY(){
        int res = 0;
        for (Node n : nodes){
            if (n.y > res){
                res = n.y;
            }
        }
        return res;
    }

    /**
     * Removes all building related nodes from the network.
     */
    public void DestroyFace(){
        for (Building building : buildings){
            for (Node node : building.getNodes()){
                Utility.removeNodeFromAllNodes(node);
            }
            for (Edge edge : building.getEdges()){
                Utility.removeEdgeFromAllEdges(edge);
            }
            for (Node node : building.getVisualNodes()){
                Utility.removeNodeFromAllNodes(node);
            }
            for (Edge edge : building.getVisualEdge()){
                Utility.removeEdgeFromAllEdges(edge);
            }
        }
    }

    /**
     * Rotates the face, finds the rotated maximumrectangle and rotates all nodes back again
     * @return is false if the face is incorrect, true if it is correct.
     */
    public boolean GenerateRotatedBuildings(){
        boolean success = true;
        int[][] faceMatrix = ToRotatedMatrix();
        if (faceMatrix != null){
            MaximumRectangle.FindMaximumRectangle(faceMatrix,this);
        }
        else {
            success = false;
        }
        RotateFaceBack(center);

        return success;
    }

    /**
     * Rotates the nodes in the face according to an edge of the face.
     * @return Returns a matrix based on the face which has been rotated
     */
    public int[][] ToRotatedMatrix(){
        Vec2d v1 = null;
        Vec2d v2 = null;

        v1 = NodesToVector(edges.get(0).getNode1(),edges.get(0).getNode2());
        v2 = new Vec2d(2,0);

        angle = Math.acos((v1.x*v2.x+v1.y*v2.y) / (Math.sqrt(v1.x*v1.x+v1.y*v1.y) * Math.sqrt(v2.x*v2.x+v2.y*v2.y)));
        SetCenter();

        boolean isFaceCorrect = true;

        for (Node node : nodes){//Rotates all nodes, also makes an extra check if the face is correct or not
            node = Utility.NodeRotation(node,center,angle);

            if (!Utility.IsFaceCorrectBasedOnNode(node,this)){
                isFaceCorrect = false;
            }
            node.isRotated = true;
            node.angle = angle;
        }

        if (!isFaceCorrect){
            return null;
        }

        return this.ToMatrix(nodes);
    }

    /**
     * Node used when rotating
     */
    private Point center = new Point();

    /**
     * Finds the center of the face
     */
    private void SetCenter(){
        double avgX = 0;
        double avgY = 0;

        for (Node n : nodes){
            avgX+=n.x;
            avgY+=n.y;
        }
        avgX /= nodes.size();
        avgY /= nodes.size();
        center.x = (int) avgX;
        center.y = (int) avgY;
    }

    private Vec2d NodesToVector (Node n1, Node n2){
        return (new Vec2d(n2.x-n1.x,n2.y-n1.y));
    }

    /**
     * rotates all points back to their original position
     * @param center the center of the face, which will be used for the rotation
     */
    public void RotateFaceBack(Point center){ //Rotates all points back to their original position
        for (Node node : nodes){
            Utility.NodeRotation(node,center,Math.toRadians(360)-angle);
            node.isRotated = false;
        }
        for (Node node : maximumRectangleNodes){
            Utility.NodeRotation(node,center,Math.toRadians(360)-angle);
            node.isRotated = false;
        }
        for (Building building : buildings){
            for (Node node : building.getNodes()){
                Utility.NodeRotation(node,center,Math.toRadians(360)-angle);
                node.isRotated = false;
            }
            for (Node node : building.getVisualNodes()){
                Utility.NodeRotation(node,center,Math.toRadians(360)-angle);
                node.isRotated = false;
            }
        }
    }

    //Changes the face to a matrix so it can be used with the maximum rectangle algorithm
    public int[][] ToMatrix(ArrayList<Node> nodes) {
        int east = 0, west = 0, north = 0, south = 0;
        int start = 0;
        for (Node node : nodes) { //Finds the smallest and largest x and y coordinates
            if (start == 0) {
                start = 1;
                east = node.x;
                west = node.x;
                north = node.y;
                south = node.y;
            } else {
                if (node.x > east)
                    east = node.x;
                else if (node.x < west)
                    west = node.x;
                if (node.y > north)
                    north = node.y;
                else if (node.y < south)
                    south = node.y;
            }
        }
        int x = east - west;
        int y = north - south;

        if (y <= 0 || x <= 0){
            return null;
        }

        if ((y+x) > Utility.maxMatrix){
            return null;
        }
        // Looks through the array and checks which points are inside the face and which aren't
        int[][] faceMatrix = new int[x][y];
        for (int i = 0; i < faceMatrix.length; i++) {
            //for (int j = faceMatrix[0].length-1; j >= 0; j--) {
            for (int j = 0; j < faceMatrix[0].length; j++) {
                if (Utility.IsInPolygon(i, j, west, south, nodes)) {
                    faceMatrix[i][j] = 1;
                }
                else {
                    faceMatrix[i][j] = 0;
                }
            }
        }
        return faceMatrix;
    }

    //Finds the Dot Product of two vectors
    private double DotProduct(Vec2d u, Vec2d v){
        return (u.x * v.x + u.y * v.y);
    }

    //Finds the distance from a line segment to a point Source:http://geomalgorithms.com/a02-_lines.html
    public double DistanceLineSegmentToPoint(int sx1, int sy1, int sx2, int sy2, int px, int py){
        Point2D s1 = new Point(sx1,sy1);
        Point2D s2 = new Point(sx2,sy2);
        Point2D p = new Point(px,py);

        Vec2d v = new Vec2d(s2.getX() - s1.getX(), s2.getY() - s1.getY());
        Vec2d w = new Vec2d(p.getX() - s1.getX(), p.getY() - s1.getY());

        double c1 = DotProduct(v,w);
        if ( c1 <= 0)
            return Utility.EuclideanDistance(p,s1);

        double c2 = DotProduct(v,v);
        if ( c2 <= c1)
            return Utility.EuclideanDistance(p,s2);

        double b = c1 / c2;
        Point2D Pb = new Point2D.Double(s1.getX() + b * v.x, s1.getY() + b * v.y);
        return Utility.EuclideanDistance(p,Pb);
    }

    /**
     * Connects the buildings to the road network and makes connections between multibuildings
     */
    public void SplitEdge() {

        //Prevents running this function on wrong faces with two or less edges
        if (edges.size() <= 2) {
            return;
        }

        ArrayList<Edge> allEdges = Utility.getAllEdges();
        ArrayList<Node> allNodes = Utility.getAllNodes();

        // Adds the building nodes to the network
        for (Building b : buildings) {
            b.InitializeIndoorStructure();
        }

        Building b = null;

        for (int i = 0 ; i < buildings.size() ; i++){
            b = buildings.get(i);

            if (b.getFloorNumber() != 0){
                continue;
            }

            doornodeloop:
            for (Node doorNode : b.getDoorNodes()){

                Edge shortestDistToEdge = new Edge();
                double shortestDist = Double.MAX_VALUE;
                double tempDist;
                boolean shortestDistOnEdge = true; //Value used for multibuildings. Is used to know if the shortest
                                                   // distance is on an outer edge or a doornode of another building in the face
                Node shortestDistanceNode = null;

                if (b.isMultiBuilding() && doorNode.getFlag() == 8 && i != buildings.size()-1){
                    if (buildings.get(i+1).getFloorNumber() == 0){
                        for (Node otherDoorNode : buildings.get(i+1).getDoorNodes()) {
                            if (otherDoorNode.getFlag() == 7){
                                Edge newEdge = new Edge(otherDoorNode.getID() * 100000 + doorNode.getID(), otherDoorNode, doorNode, "", (short) 1, (short) 0, 0);
                                newEdge.setEdgeClass((short) 8);
                                allEdges.add(newEdge);
                                Utility.setAllEdges(allEdges);
                                continue doornodeloop;
                            }
                        }
                    }
                }

                if ((doorNode.getFlag() == 7 || doorNode.getFlag() == 8) && b.isMultiBuilding()){
                    continue;
                }

                for (Edge e : edges){
                    tempDist = DistanceLineSegmentToPoint(e.getNode1().x, e.getNode1().y, e.getNode2().x, e.getNode2().y ,doorNode.x, doorNode.y);

                    if (tempDist < shortestDist) {
                        shortestDistToEdge = e;
                        shortestDist = tempDist;
                        shortestDistOnEdge = true;
                    }
                }

                Point closestPointOnEdge = FindClosestPointOnEdge(shortestDistToEdge.getNode1().toPoint(), shortestDistToEdge.getNode2().toPoint(), new Point(doorNode.x, doorNode.y));

                //This check is just to ensure that we don't make new edges if the closest point on the edge is equal to one of the endpoints of the edge
                if (!shortestDistToEdge.getNode1().compareCoordinates(closestPointOnEdge) &&
                        !shortestDistToEdge.getNode2().compareCoordinates(closestPointOnEdge) && shortestDistOnEdge) {
                    Node tempNode = new Node(closestPointOnEdge.x, closestPointOnEdge.y, Utility.getUniqueNodeID(), (short)0);
                    allNodes.add(tempNode);
                    Utility.setAllNodes(allNodes);

                    Edge newEdge1 = new Edge(tempNode.getID() * 100000 + shortestDistToEdge.getNode1().getID(), tempNode, shortestDistToEdge.getNode1(), shortestDistToEdge.getName(), (short) 0, (short) 0, 0);
                    Edge newEdge2 = new Edge(tempNode.getID() * 100000 + shortestDistToEdge.getNode2().getID(), tempNode, shortestDistToEdge.getNode2(), shortestDistToEdge.getName(), (short) 0, (short) 0, 0);
                    Edge newEdge3 = new Edge(tempNode.getID() * 100000 + doorNode.getID(), tempNode, doorNode, shortestDistToEdge.getName(), (short) 0, (short) 0, 0);

                    newEdge1.setEdgeClass((short)shortestDistToEdge.getEdgeClass());
                    newEdge2.setEdgeClass((short)shortestDistToEdge.getEdgeClass());
                    newEdge3.setEdgeClass((short)shortestDistToEdge.getEdgeClass());

                    allEdges.remove(shortestDistToEdge);
                    allEdges.add(newEdge1);
                    allEdges.add(newEdge2);
                    allEdges.add(newEdge3);
                    Utility.setAllEdges(allEdges);

                    newEdge1.addFaces(this);
                    newEdge2.addFaces(this);
                    newEdge3.addFaces(this);

                    for (Face f : shortestDistToEdge.getFaces()){
                        f.addEdge(newEdge1);
                        f.addEdge(newEdge2);
                        f.removeEdge(shortestDistToEdge);
                        f.addNode(tempNode);
                    }
                }
            }
        }
    }

    //Finding the point on an edge closest to another point
    Point FindClosestPointOnEdge(Point A, Point B, Point P) {
        Vec2d a_to_p = new Vec2d(P.x - A.x, P.y - A.y);
        Vec2d a_to_b = new Vec2d(B.x - A.x, B.y - A.y);

        double atb2 = (a_to_b.x * a_to_b.x) + (a_to_b.y * a_to_b.y);

        double atp_dot_atb = a_to_p.x * a_to_b.x + a_to_p.y * a_to_b.y;

        double t = atp_dot_atb / atb2;

        return (new Point((int)(A.x + a_to_b.x * t), (int)(A.y + a_to_b.y * t)));
    }

    public double getFacePerimeter(){
        double facePerimeter = 0;
        for (Edge e : edges){
            facePerimeter += e.getLength();
        }
        return facePerimeter;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
