package util;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import routing.*;
import util.*;

public class Utility {

    private static ArrayList<Edge> allEdges = new ArrayList<>();

    private static ArrayList<Node> allNodes = new ArrayList<>();

    private static ArrayList<Face> allFaces = new ArrayList<>();

    public static int numberOfReportedPositions; //Variable used to prevent method removeAllObjectsOfLayer from executing infinite loop

    private static long largestNodeID;

    public static String templatePath = "templates/buildingTemplates.xml";

    public static String dataPath = "";

    public static String faceFindingPath = "";

    public static double maxFacePerimeter = 5000*1.5;

    public static int maxMatrix = 6000+4000;

    public static int maxSId = 0;

    public static ArrayList<Node> nodesWithSIds = new ArrayList<>(); //TODO only used for testing

    /**
     * Center point of entire network, used for rotations
     */
    public static Point center;

    /**
     * Maximum number of transfers (max number of times a moving object can change destination to a new building)
     */
    private static int maxTranfers = 5;
    /**
     * Maximum dwellingtime for moving objects. When moving object is instantiated this value is used
     */
    private static int maxDwellingTime = 10;
    /**
     * Maximum number of floors for each building
     */
    private static int maxNumOfFloors = 1; //TODO Change to 1

    public static int getMaxNumOfFloors() {
        return maxNumOfFloors;
    }

    /**
     * Active floor in visualization
     */
    public static int actFloorVisual = 0;

    public static int getActFloorVisual() {
        return actFloorVisual;
    }

    public static void setActFloorVisual(int actFloorVisual) {
        Utility.actFloorVisual = actFloorVisual;
    }

    public static void setMaxNumOfFloors(int maxNumOfFloors) {
        Utility.maxNumOfFloors = maxNumOfFloors;
    }

    public static int getMaxTranfers() {
        return maxTranfers;
    }

    public static void setMaxTranfers(int maxTranfers) {
        Utility.maxTranfers = maxTranfers;
    }

    public static int getMaxDwellingTime() {
        return maxDwellingTime;
    }

    public static void setMaxDwellingTime(int maxDwellingTime) {
        Utility.maxDwellingTime = maxDwellingTime;
    }

    public static ArrayList<Edge> getAllEdges() {
        return allEdges;
    }

    public static void setAllEdges(ArrayList<Edge> allEdges) {
        Utility.allEdges = allEdges;
    }

    public static ArrayList<Face> getAllFaces() {
        return allFaces;
    }

    public static void setAllFaces(ArrayList<Face> allFaces) {
        Utility.allFaces = allFaces;
    }

    public static ArrayList<Node> getAllNodes() {
        return allNodes;
    }

    public static void removeEdgeFromAllEdges(Edge edge){
        allEdges.remove(edge);
    }

    public static void removeNodeFromAllNodes(Node node){
        allNodes.remove(node);
    }

    public static void initializeAllNodes(ArrayList<Node> allNodes) {
        Utility.allNodes = allNodes;
        largestNodeID = 0;

        for (Node n : allNodes){
            if (n.getID() > largestNodeID){
                largestNodeID = n.getID();
            }
        }
    }

    public static void setAllNodes(ArrayList<Node> allNodes) { Utility.allNodes = allNodes; }

    public static long getUniqueNodeID() {
        largestNodeID++;
        return largestNodeID;
    }

    public static double AngleEdgeToPoint(Point edgeStart, Point edgeEnd, Point target)
    {
        double shiftAngle = Math.toDegrees(Math.atan2(edgeEnd.x - edgeStart.x, edgeEnd.y - edgeStart.y));
        double targetAngle = Math.toDegrees(Math.atan2(target.x - edgeStart.x, target.y - edgeStart.y));
        if(shiftAngle < 0)
            shiftAngle += 360;
        if(targetAngle < 0)
            targetAngle += 360;

        return targetAngle - shiftAngle;
    }

    //Return
    public static boolean ValidateData(ArrayList<Node> nodes, ArrayList<Edge> edges)
    {
        //ArrayList<Node> nodes = BrinkhoffReadep2_readNodes("Bytedata.node");
        ArrayList<Face> faces = InputReader.ReadFile(nodes, "faces.txt");
        //allEdges = BrinkhoffReader.readEdges("Edgedata.edge",nodes);
        allEdges = edges;
        ArrayList<ArrayList<Edge>> faceEdges = new ArrayList<>();
        ArrayList<Node> faceNodes;

        for (Face face: faces)
        {
            faceNodes = face.getNodes();
            ArrayList<Edge> edgesInFace = GetEdgesFromNodes(faceNodes); //Edges might be wrong here!
            faceEdges.add(edgesInFace);
        }
        return true;
    }

    //Finds an edge containing the two input nodes
    public static Edge GetEdge(Node n1, Node n2, ArrayList<Edge> edges)
    {
        long id1 = n1.getID()*100000+n2.getID();
        for (Edge edge: edges)
        {
            /*
            System.out.println(Long.toString(n1.getID()));
            System.out.println(Long.toString(n2.getID()));

            if (Long.toString(edge.getId()).contains(Long.toString(n1.getID()))
                    && Long.toString(edge.getId()).contains(Long.toString(n2.getID()))){
                return edge;
            }*/

            /*if (id1 == edge.getID()) {
                return edge;
            }*/

            if (n1 == edge.getNode1() && n2 == edge.getNode2()) {
                return edge;
            }
            else if (n1 == edge.getNode2() && n2 == edge.getNode1()) {
                return edge;
            }

        }
        return null;
    }

    public static ArrayList<Edge> GetAllEdgesFromNode(Node n){
        ArrayList<Edge> result = new ArrayList<>();

        for (Edge e : allEdges){
            if (n == e.getNode1() || n == e.getNode2()){
                result.add(e);
            }
        }
        return result;
    }

    //Finds a list of nodes from a list using the GetEdge method
    public static ArrayList<Edge> GetEdgesFromNodes(List<Node> nodes)
    {
        ArrayList<Edge> returnEdges = new ArrayList<>();

        for (Node node1: nodes)
        {
            for(Node node2: nodes)
            {
                Edge e = GetEdge(node1,node2,allEdges);
                if (e != null && !returnEdges.contains(e))
                {
                    returnEdges.add(e);
                }
            }
        }
        //System.out.println("EDGES: " + returnEdges);

        return returnEdges;
    }

    public static int RoundUpIf0(double percent, double number){
        int res = (int)(((double)number / 100.0) * percent);

        if ((int)(((double)number / 100.0) * percent) < 1)
        {
            res = 1;
        }

        return (res);
    }

    //TODO: Does not always remove all single edge nodes for some reason. Might have to use getNextEdge to remove all edges connected to the node removed
    public static ArrayList<Node> removeSingleEdgeNodes(ArrayList<Node> nodes){

        ArrayList<Node> removedNodes = new ArrayList<>();

        ArrayList<Edge> tempEdges = Utility.getAllEdges();

        for (Node n : nodes){
            if (n.getNumOfEdges() < 2) {
                removedNodes.add(n);
            }
        }

        Node n1 = new Node();
        Node n2 = new Node();

        for (Node n : removedNodes){
            nodes.remove(n);
            tempEdges.remove(n.getFirstEdge());
            /* This should probably not be done, unless this function is run after faces are set
            for (Face f : n.getFirstEdge().getFaces()){
                f.removeEdge(n.getFirstEdge());
                f.removeNode(n);
            }*/
            if (n.getFirstEdge() != null) {
                if (n.getFirstEdge().getNode1() != null || n.getFirstEdge().getNode2() != null){
                    n1 = n.getFirstEdge().getNode1();
                    n2 = n.getFirstEdge().getNode2();

                    n1.removeEdge(n.getFirstEdge());
                    n2.removeEdge(n.getFirstEdge());
                }
            }
        }

        Utility.setAllEdges(tempEdges);

        for (Node n : nodes){
            if (n.getNumOfEdges() < 2 || n.getEdge().length < 2) {
                nodes = removeSingleEdgeNodes(nodes);
                break;
            }
        }

        return nodes;
    }

    public static boolean IsCorrectFace(Face f){
        for (Edge e : Utility.getAllEdges()){
            if (e.getFlag() == 0){
                if (f.getEdges().contains(e) && e.getFaces().contains(f)){
                    for (Building b : f.buildings){ //Checks if any of the edges in the building collides with any of the road edges contained in the face
                        for (Edge be : b.getEdges()){
                            if (DoesLinesIntersect(e,be)){
                                return false;
                            }
                        }
                    }
                }
                else if (!f.getEdges().contains(e) && !e.getFaces().contains(f)){ //Checks if the edge we are looking at is a part of the face
                    for (Edge me : f.getMaximumRectangleEdges()) //Checks if the edge intersects with any of the boundaries of the maximum rectangle of the face
                        if (DoesLinesIntersect(e,me)) {
                            return false;
                        }
                }
            }
        }

        for (Face of : Utility.getAllFaces())
        {
            if (of.getId() != f.getId()) {
                for (Node n : of.getNodes())
                {
                    if (IsInPolygon(n.x,n.y,
                            f.getMaximumRectangleNodes().get(0).x,f.getMaximumRectangleNodes().get(0).y,f.getMaximumRectangleNodes())){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //TODO måske lige find et andet navn..
    public static boolean IsFaceCorrectBasedOnNode(Node n, Face f){
        int numberOfEdgesInFace = 0;
        if (f.getEdges().contains(n.getFirstEdge())){
            numberOfEdgesInFace++;
        }

        Edge actEdge;

        while ((actEdge = n.getNextEdge()) != null){
            if (f.getEdges().contains(actEdge)){
                numberOfEdgesInFace++;
            }
        }
        if (numberOfEdgesInFace >= 2){
            return true;
        }
        return false;
    }

    //This function sorts out any wrong faces, if there are any
    public static ArrayList<Face> removeWrongFaces(ArrayList<Face> faces){
        ArrayList<Face> removedFaces = new ArrayList<>();

        for (Edge e : Utility.getAllEdges()){
            //Makes sure that the edge in the maximum rectangle are not considered
            if (e.getFlag() == 0) {
                for (Face f : faces){
                    if (f.getEdges().contains(e) && e.getFaces().contains(f)){
                        for (Building b : f.buildings){ //Checks if any of the edges in the building collides with any of the road edges contained in the face
                            for (Edge be : b.getEdges()){
                                if (DoesLinesIntersect(e,be)){
                                removedFaces.add(f);
                                }
                            }
                        }
                    }
                    else if (!f.getEdges().contains(e) && !e.getFaces().contains(f)){ //Checks if the edge we are looking at is a part of the face
                        for (Edge me : f.getMaximumRectangleEdges()) //Checks if the edge intersects with any of the boundaries of the maximum rectangle of the face
                            if (DoesLinesIntersect(e,me)) {
                                removedFaces.add(f);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < removedFaces.size(); i++) {
            faces.remove(removedFaces.get(i));
            removedFaces.get(i).DestroyFace();
        }

        for (Face f : faces){
            for (Face of : faces)
            {
                if (f.getId() != of.getId()) {
                    for (Node n : f.getMaximumRectangleNodes())
                    {
                        if (IsInPolygon(n.x,n.y,
                                of.getMaximumRectangleNodes().get(0).x,of.getMaximumRectangleNodes().get(0).y,of.getMaximumRectangleNodes())){
                            removedFaces.add(of);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < removedFaces.size(); i++) {
            faces.remove(removedFaces.get(i));
            removedFaces.get(i).DestroyFace();
        }

        //Runs function again if some faces where not removed TODO: MIGHT NOT BE NECESSARY
        /*outerloop:
        for (Edge e : Utility.getAllEdges()){
            //Makes sure that the edge in the maximum rectangle are not considered
            if (e.getEdgeClass() == 0) {
                for (Face f : faces){
                    if (!f.getEdges().contains(e)){ //Checks if the edge we are looking at is a part of the face
                        for (Edge me : f.getMaximumRectangleEdges())
                            if (DoesLinesIntersect(e.getNode1().x, e.getNode1().y, e.getNode2().x, e.getNode2().y,
                                    me.getNode1().x, me.getNode1().y, me.getNode2().x, me.getNode2().y)) {
                                faces = removeWrongFaces(faces);
                                break outerloop;
                            }
                    }
                }
            }
        }*/

        return faces;
    }

    private static int orientation(int p0_x, int p0_y, int p1_x, int p1_y, int p2_x, int p2_y)
    {
        // See 10th slides from following link for derivation of the formula
        // http://www.dcs.gla.ac.uk/~pat/52233/slides/Geometry1x1.pdf
        int val = (p1_y - p0_y) * (p2_x - p1_x) -
                (p1_x - p0_x) * (p2_y - p1_y);

        if (val == 0) return 0;  // colinear

        return (val > 0)? 1: 2; // clock or counterclock wise
    }

    private static boolean onSegment(int p0_x, int p0_y, int p1_x, int p1_y, int p2_x, int p2_y)
    {
        if (p1_x <= Math.max(p0_x, p2_x) && p1_x >= Math.min(p0_x, p2_x) &&
                p1_y <= Math.max(p0_y, p2_y) && p1_y >= Math.min(p0_y, p2_y))
            return true;

        return false;
    }

    //Checks if two edges intersect from http://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
    public static boolean DoesLinesIntersect(Edge e1, Edge e2) {

        int p0_x = e1.getNode1().x;
        int p0_y = e1.getNode1().y;
        int p1_x = e1.getNode2().x;
        int p1_y = e1.getNode2().y;
        int p2_x = e2.getNode1().x;
        int p2_y = e2.getNode1().y;
        int p3_x = e2.getNode2().x;
        int p3_y = e2.getNode2().y;

        // Find the four orientations needed for general and
        // special cases
        int o1 = orientation(p0_x,p0_y, p1_x,p1_y, p2_x,p2_y);
        int o2 = orientation(p0_x,p0_y, p1_x,p1_y, p3_x,p3_y);
        int o3 = orientation(p2_x,p2_y, p3_x,p3_y, p0_x,p0_y);
        int o4 = orientation(p2_x,p2_y, p3_x,p3_y, p1_x,p1_y);

        // General case
        if (o1 != o2 && o3 != o4)
            return true;

        // Special Cases
        // p0_x,p0_y, p1_x,p1_y and p2 are colinear and p2 lies on segment p0_x,p0_yp1_x,p1_y
        if (o1 == 0 && onSegment(p0_x,p0_y, p2_x,p2_y, p1_x,p1_y)) return true;

        // p0_x,p0_y, p1_x,p1_y and p2 are colinear and q2 lies on segment p0_x,p0_yp1_x,p1_y
        if (o2 == 0 && onSegment(p0_x,p0_y, p3_x,p3_y, p1_x,p1_y)) return true;

        // p2, q2 and p0_x,p0_y are colinear and p0_x,p0_y lies on segment p2q2
        if (o3 == 0 && onSegment(p2_x,p2_y, p0_x,p0_y, p3_x,p3_y)) return true;

        // p2, q2 and p1_x,p1_y are colinear and p1_x,p1_y lies on segment p2q2
        if (o4 == 0 && onSegment(p2_x,p2_y, p1_x,p1_y, p3_x,p3_y)) return true;

        return false; // Doesn't fall in any of the above cases
    }

    //Checks if a point is inside a polygon
    public static boolean IsInPolygon(int pointx, int pointy, int minx, int miny, ArrayList<Node> nodes) { //From http://stackoverflow.com/questions/217578/point-in-polygon-aka-hit-test/2922778#2922778
        int i, j;
        boolean c = false;
        for (i = 0, j = nodes.size() - 1; i < nodes.size(); j = i++) {
            if (((nodes.get(i).y - miny > pointy) != (nodes.get(j).y - miny > pointy)) &&
                    (pointx < ((nodes.get(j).x - minx) - (nodes.get(i).x - minx)) * (pointy - (nodes.get(i).y - miny)) / ((nodes.get(j).y - miny) - (nodes.get(i).y - miny)) + (nodes.get(i).x - minx)))
                c = !c;
        }
        return c;
    }

    //Set capacity of all room and edges in the rooms
    public static void updateRoomEdgeCapacities(int roomEdgeCapacity){
        int tempRoomCapacity = 0;
        for (Face f : Utility.getAllFaces()){
            for (Building b : f.buildings){
                for (Room r : b.getRooms()){
                    tempRoomCapacity = 0;
                    for (int i = 0; i < r.getEdges().size(); i++) {
                        tempRoomCapacity += roomEdgeCapacity;
                    }
                    for (Edge e : r.getEdges()){
                        e.setCapacity(tempRoomCapacity);
                    }
                    r.setCapacity(tempRoomCapacity);
                }
            }
        }
    }

    //TODO Should maybe move this function
    public static void assignsIDsForAllNodes(){
        for (int i = 0; i < allNodes.size()-1; i++) {
            allNodes.get(i).setsId(i);
        }
        System.out.println(allNodes.get(allNodes.size()-1));
    }

    public static Node NodeRotation(Node point, Point center, double angle){
        double x1 = point.x - center.x;
        double y1 = point.y - center.y;

        double x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle);
        double y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle);

        point.x = (int)(x2 + center.x);
        point.y = (int)(y2 + center.y);

        return point;
    }

    public static void writeGraphToOtherFormat(){
        try{
            ArrayList<Node> newNodesWithSIds = new ArrayList<>();

            for (Edge e: Utility.getAllEdges()){
                if (e.getNode1().getFirstEdge() == null){
                    e.getNode1().addEdge(e);
                }
                if (e.getNode2().getFirstEdge() == null){
                    e.getNode2().addEdge(e);
                }
            }
            String edgeFileName = "./dataset/SanJoaquin-d.gr/";
            String coordinateFileName = "./dataset/SanJoaquin-d.co";

            File eFile = new File(edgeFileName);
            File cFile = new File(coordinateFileName);

            if (!eFile.exists()){
                eFile.createNewFile();
            }

            if (!cFile.exists()){
                cFile.createNewFile();
            }

            PrintWriter edgeOut = new PrintWriter(edgeFileName);
            PrintWriter coordinateOut = new PrintWriter(coordinateFileName);

            int counter = 0;

            for (Node n: Utility.getAllNodes()) {
                if (n.getFirstEdge() == null || n.getMark() != 1 ){
                    continue;
                }
                if (n.getFirstEdge().getName().contains("walls") || n.getFirstEdge().getEdgeClass() == 9){
                    continue;
                }
                counter++;
                n.setsId(counter);
                n.writeSIDToStringSimple(coordinateOut);
                newNodesWithSIds.add(n);
            }
            coordinateOut.close();
            coordinateOut.println(counter);
            Utility.maxSId = counter;
            counter = 0;

            for (Edge e: Utility.getAllEdges()) {
                if (e.getName().contains("walls") || e.getEdgeClass() == 9 || e.getNode1() == null || e.getNode2() == null || e.getNode1().getMark() != 1 || e.getNode2().getMark() != 1){
                    continue;
                }
                e.writeSIDToStringSimple(edgeOut);
                counter++;
            }
            edgeOut.close();
            System.out.println("DONE");
            nodesWithSIds = newNodesWithSIds;
        }
        catch (Exception E){
            E.printStackTrace();
        }

    }

    // Distance between two points
    public static double EuclideanDistance(Point2D p1, Point2D p2){
        return (Math.sqrt(Math.pow(p2.getX() - p1.getX(),2) + Math.pow(p2.getY() - p1.getY(),2)));
    }

    public static void assignSIDs() {
        for (Edge e : Utility.getAllEdges()) {
            if (e.getNode1().getFirstEdge() == null) {
                e.getNode1().addEdge(e);
            }
            if (e.getNode2().getFirstEdge() == null) {
                e.getNode2().addEdge(e);
            }
        }

        int counter = 0;

        for (Node n : Utility.getAllNodes()) {
            if (n.getFirstEdge() == null) {
                n.setsId(Integer.MAX_VALUE);
                continue;
            }
            if (n.getFirstEdge().getName().contains("walls")) {
                n.setsId(Integer.MAX_VALUE);
                continue;
            }
            counter++;
            n.setsId(counter);
        }
        System.out.println("COUNTER NODES: " + counter);
        counter = 0;

        for (Edge e : Utility.getAllEdges()) {
            if (e.getName().contains("walls")) {
                continue;
            }
            counter++;
        }
        System.out.println("COUNTER EDGES: " + counter);
        System.out.println("DONE");
    }

    public static Node findNearestNodeOnRoad(Node node){
        double shortestDistance = Double.MAX_VALUE;
        Node closestNodeOnRoad = null;
        double newDistance;
        for (Node n : nodesWithSIds){
            if (!n.getName().contains("Floor") && !n.getName().contains("Building") && !n.getFirstEdge().getName().contains("walls") && n != node){
                newDistance = EuclideanDistance(new Point(n.x,n.y),new Point(node.x,node.y));
                if (newDistance < shortestDistance){
                    shortestDistance = newDistance;
                    closestNodeOnRoad = n;
                }

            }
        }
        return closestNodeOnRoad;
    }

    public static void writeGraphToOtherFormatNoBuildings(){
        try{

            for (Node n : Utility.getAllNodes()){
                n.setsId(0);
            }

            ArrayList<Node> newNodesWithSIds = new ArrayList<>();

            for (Edge e: Utility.getAllEdges()){
                if (e.getNode1().getFirstEdge() == null){
                    e.getNode1().addEdge(e);
                }
                if (e.getNode2().getFirstEdge() == null){
                    e.getNode2().addEdge(e);
                }
            }
            String edgeFileName = "./dataset/SanJoaquin-d.gr/";
            String coordinateFileName = "./dataset/SanJoaquin-d.co";

            File eFile = new File(edgeFileName);
            File cFile = new File(coordinateFileName);

            if (!eFile.exists()){
                eFile.createNewFile();
            }

            if (!cFile.exists()){
                cFile.createNewFile();
            }

            PrintWriter edgeOut = new PrintWriter(edgeFileName);
            PrintWriter coordinateOut = new PrintWriter(coordinateFileName);

            int counter = 0;

            for (Node n: Utility.getAllNodes()) {
                if (n.getFirstEdge() == null || n.getMark() != 1){
                    continue;
                }
                if (n.getFirstEdge().getName().contains("walls") || n.getFirstEdge().getEdgeClass() == 9 || n.getName().contains("Floor") || n.getName().contains("Building")){
                    continue;
                }
                counter++;
                n.setsId(counter);
                n.writeSIDToStringSimple(coordinateOut);
                newNodesWithSIds.add(n);
            }
            coordinateOut.close();
            coordinateOut.println(counter);
            Utility.maxSId = counter;
            counter = 0;

            for (Edge e: Utility.getAllEdges()) {
                if (e.getName().contains("walls") || e.getEdgeClass() == 9 || e.getNode1() == null || e.getNode2() == null
                        || e.getNode1().getMark() != 1 || e.getNode2().getMark() != 1 || e.getNode1().getName().contains("Floor") || e.getNode1().getName().contains("Floor")
                        || e.getNode2().getName().contains("Floor") || e.getNode2().getName().contains("Floor")){
                    continue;
                }
                e.writeSIDToStringSimple(edgeOut);
                counter++;
            }
            edgeOut.close();
            System.out.println("DONE");
            nodesWithSIds = newNodesWithSIds;
        }
        catch (Exception E){
            E.printStackTrace();
        }

    }
}
