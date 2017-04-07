package routing;
import util.Utility;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Nichlas on 23-04-2015.
 */

public class BuildingTemplate {

        public static Face insertTemplate(int faceWidth, int faceHeight, int[][] faceMatrix, int minFaceWidth, int minFaceHeight, Face currentFace, int multibuildingGeneration){
            boolean scanner = true; //Testing value. Used to activate and deactivate scanning of building templates

            //BUILDING: Variables
            int id = 0;
            double widthPercentage = 100.0;
            double heightPercentage = 100.0;
            double buttomXCoord = 0.0;
            double buttomYCoord = 0.0;
            Building newBuilding = new Building();
            ArrayList<Room> buildingRooms = new ArrayList<>();

            //NODE: Variables
            double xValuePercentage = 100.0;
            double yValuePercentage = 100.0;
            short floorNumber = 1;
            Node newNode;

            //ROOM Variables
            Room newRoom = null;
            int numberOfRoomEdges = 0;
            double xValuePercentageUpperRight = 0;
            double yValuePercentageUpperRight = 0;
            Node lowerLeftCorner;
            Node lowerRightCorner;
            Node upperRightCorner;
            Node upperLeftCorner;
            ArrayList<Edge> roomEdges = new ArrayList<>();
            ArrayList<Node> visualRoomNodes = new ArrayList<>();
            ArrayList<Edge> visualRoomEdges = new ArrayList<>();
            int roomNumber = 0;

            //ROOMNODE Variables
            ArrayList<Node> roomNodes = new ArrayList<>();

            //EDGE Variables
            int node1ID = 0;
            int node2ID = 0;
            Edge newEdge;

            ArrayList<ArrayList<Building>> templateList = new ArrayList<>();
            //ArrayList<Building> buildings = new ArrayList<>();
            ArrayList<Node> indoorNodes = new ArrayList<>();
            ArrayList<Edge> indoorEdges = new ArrayList<>();

            if (scanner) {

                int maxFloorNumber = Utility.getMaxNumOfFloors()-1;
                int minFloorNumber = 0;

                //Top floor for current building
                long topCurrentFloor = Math.round(Math.random() * (maxFloorNumber - minFloorNumber));
                //long topCurrentFloor = maxFloorNumber;

                Building tempBuilding;

                for (int j = 0; j <= topCurrentFloor ; j++) {

                    ArrayList<Building> buildings = new ArrayList<>();

                BufferedReader br = null;

                try {
                    String sCurrentLine;
                    br = new BufferedReader(new FileReader(Utility.templatePath));

                    while ((sCurrentLine = br.readLine()) != null){
                        Scanner sc = new Scanner(sCurrentLine);

                        if (sCurrentLine.contains("<building>")) {
                            roomNodes = new ArrayList<>(); //Resets variable for new building block

                            sCurrentLine = br.readLine();
                            sc = new Scanner(sCurrentLine);

                            sCurrentLine = returnValues(sCurrentLine);
                            sc = new Scanner(sCurrentLine);

                            id = sc.nextInt();

                            sCurrentLine = br.readLine();
                            sc = new Scanner(sCurrentLine);
                            sCurrentLine = br.readLine();
                            sc = new Scanner(sCurrentLine);

                            sCurrentLine = returnValues(sCurrentLine);
                            sc = new Scanner(sCurrentLine);

                            widthPercentage = sc.nextDouble();
                            heightPercentage = sc.nextDouble();
                            buttomXCoord = sc.nextDouble();
                            buttomYCoord = sc.nextDouble();

                            sCurrentLine = br.readLine();
                            sc = new Scanner(sCurrentLine);

                            boolean isMultiBuilding = false;

                            if (returnValues(sCurrentLine).equals("1")){
                                isMultiBuilding = true;
                            }

                            newBuilding = new Building(id, Utility.RoundUpIf0(widthPercentage, faceWidth - minFaceWidth),
                                    Utility.RoundUpIf0(heightPercentage, faceHeight - minFaceHeight), Utility.RoundUpIf0(buttomXCoord, faceWidth - minFaceWidth) + minFaceWidth,
                                    Utility.RoundUpIf0(buttomYCoord, faceWidth - minFaceHeight) + minFaceHeight, isMultiBuilding);

                                while ((sCurrentLine = br.readLine()) != null) {
                                    if (sCurrentLine.contains("<roomnode>")) {
                                        //Skips two lines
                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);
                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);

                                        sCurrentLine = returnValues(sCurrentLine);
                                        sc = new Scanner(sCurrentLine);

                                        xValuePercentage = sc.nextDouble();
                                        yValuePercentage = sc.nextDouble();

                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);
                                        sCurrentLine = returnValues(sCurrentLine);
                                        sc = new Scanner(sCurrentLine);

                                        roomNumber = sc.nextInt();

                                        newNode = new Node(Utility.RoundUpIf0(xValuePercentage, newBuilding.getWidth()) + newBuilding.getButtomXCord(),
                                                Utility.RoundUpIf0(yValuePercentage, newBuilding.getHeight()) + newBuilding.getButtomYCord(), Utility.getUniqueNodeID(), (short) 6);
                                        indoorNodes = newBuilding.getNodes();
                                        indoorNodes.add(newNode);
                                        newBuilding.setNodes(indoorNodes);
                                        roomNodes.add(newNode);
                                        newNode.setRoomNumber(roomNumber);
                                    }
                                    if (sCurrentLine.contains("<node>")) {
                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);
                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);

                                        sCurrentLine = returnValues(sCurrentLine);
                                        sc = new Scanner(sCurrentLine);

                                        xValuePercentage = sc.nextDouble();
                                        yValuePercentage = sc.nextDouble();

                                        newNode = new Node(Utility.RoundUpIf0(xValuePercentage, newBuilding.getWidth()) + newBuilding.getButtomXCord(),
                                                Utility.RoundUpIf0(yValuePercentage, newBuilding.getHeight()) + newBuilding.getButtomYCord(), Utility.getUniqueNodeID(), (short) 1);
                                        indoorNodes = newBuilding.getNodes();
                                        indoorNodes.add(newNode);
                                        newBuilding.setNodes(indoorNodes);
                                    }
                                    else if (sCurrentLine.contains("<downnode>")) {//Exclusively down or nothing
                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);
                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);

                                        sCurrentLine = returnValues(sCurrentLine);
                                        sc = new Scanner(sCurrentLine);

                                        xValuePercentage = sc.nextDouble();
                                        yValuePercentage = sc.nextDouble();

                                        newNode = new Node(Utility.RoundUpIf0(xValuePercentage, newBuilding.getWidth()) + newBuilding.getButtomXCord(),
                                                Utility.RoundUpIf0(yValuePercentage, newBuilding.getHeight()) + newBuilding.getButtomYCord(), Utility.getUniqueNodeID(), (short) 3);
                                        indoorNodes = newBuilding.getNodes();
                                        indoorNodes.add(newNode);
                                        newBuilding.setDownNode(newNode);
                                        newBuilding.setNodes(indoorNodes);
                                    }
                                    else if (sCurrentLine.contains("<upnode>")) {//Exclusively up or nothing
                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);
                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);

                                        sCurrentLine = returnValues(sCurrentLine);
                                        sc = new Scanner(sCurrentLine);

                                        xValuePercentage = sc.nextDouble();
                                        yValuePercentage = sc.nextDouble();

                                        newNode = new Node(Utility.RoundUpIf0(xValuePercentage, newBuilding.getWidth()) + newBuilding.getButtomXCord(),
                                                Utility.RoundUpIf0(yValuePercentage, newBuilding.getHeight()) + newBuilding.getButtomYCord(), Utility.getUniqueNodeID(), (short) 4);
                                        indoorNodes = newBuilding.getNodes();
                                        indoorNodes.add(newNode);
                                        newBuilding.setUpNode(newNode);
                                        newBuilding.setNodes(indoorNodes);
                                    }
                                    else if (sCurrentLine.contains("<door>")) {//Is either a door or down node
                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);
                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);

                                        sCurrentLine = returnValues(sCurrentLine);
                                        sc = new Scanner(sCurrentLine);

                                        xValuePercentage = sc.nextDouble();
                                        yValuePercentage = sc.nextDouble();

                                        newNode = new Node(Utility.RoundUpIf0(xValuePercentage, newBuilding.getWidth()) + newBuilding.getButtomXCord(),
                                                Utility.RoundUpIf0(yValuePercentage, newBuilding.getHeight()) + newBuilding.getButtomYCord(), Utility.getUniqueNodeID(), (short) 3);
                                        if (xValuePercentage == 100){
                                            newNode.setFlag((short) 8);
                                        }
                                        else if(xValuePercentage == 0){
                                            newNode.setFlag((short) 7);
                                        }
                                        indoorNodes = newBuilding.getNodes();
                                        indoorNodes.add(newNode);
                                        newBuilding.addDoorNode(newNode);
                                        //newBuilding.setDoorNode(newNode);
                                        newNode.setName("DOORNODE");
                                        if (newBuilding.getDownNode() == null) {
                                            newBuilding.setDownNode(newNode);
                                        }

                                        newBuilding.setNodes(indoorNodes);
                                    }
                                    else if (sCurrentLine.contains("<edge>")) { //Edge inside a building
                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);
                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);

                                        sCurrentLine = returnValues(sCurrentLine);
                                        sc = new Scanner(sCurrentLine);

                                        node1ID = sc.nextInt();

                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);
                                        sCurrentLine = returnValues(sCurrentLine);
                                        sc = new Scanner(sCurrentLine);

                                        node2ID = sc.nextInt();

                                        //Sets edges connected to roomNodes to have a flag for roomEdges
                                        if (newBuilding.getNodes().get(node1ID-1).getFlag() == 6 || newBuilding.getNodes().get(node2ID-1).getFlag() == 6) {
                                            newEdge = new Edge(newBuilding.getNodes().get(node1ID-1).getID() * 100000 + newBuilding.getNodes().get(node2ID-1).getID(),
                                                    newBuilding.getNodes().get(node1ID-1), newBuilding.getNodes().get(node2ID-1), "dwelling", (short) 6, (short) 0, 0);
                                            newEdge.setEdgeClass((short)10);
                                        }
                                        else { //Normal inside edges
                                            newEdge = new Edge(newBuilding.getNodes().get(node1ID - 1).getID() * 100000 + newBuilding.getNodes().get(node2ID - 1).getID(),
                                                    newBuilding.getNodes().get(node1ID - 1), newBuilding.getNodes().get(node2ID - 1), "inside", (short) 1, (short) 0, 0);
                                            newEdge.setEdgeClass((short)8);
                                        }

                                        indoorEdges = newBuilding.getEdges();
                                        indoorEdges.add(newEdge);
                                        newBuilding.setEdges(indoorEdges);
                                    }
                                    else if (sCurrentLine.contains("<room>")){
                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);
                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);

                                        sCurrentLine = returnValues(sCurrentLine);
                                        sc = new Scanner(sCurrentLine);

                                        //Adding the two nodes that makes up the rectangle of the room
                                        xValuePercentage = sc.nextDouble();
                                        yValuePercentage = sc.nextDouble();
                                        xValuePercentageUpperRight = sc.nextDouble();
                                        yValuePercentageUpperRight = sc.nextDouble();

                                        lowerLeftCorner = new Node(Utility.RoundUpIf0(xValuePercentage, newBuilding.getWidth()) + newBuilding.getButtomXCord(),
                                                Utility.RoundUpIf0(yValuePercentage, newBuilding.getHeight()) + newBuilding.getButtomYCord(), Utility.getUniqueNodeID(), (short) 5, floorNumber);
                                        visualRoomNodes = newBuilding.getVisualNodes();
                                        visualRoomNodes.add(lowerLeftCorner);

                                        lowerRightCorner = new Node(Utility.RoundUpIf0(xValuePercentageUpperRight, newBuilding.getWidth()) + newBuilding.getButtomXCord(),
                                                Utility.RoundUpIf0(yValuePercentage, newBuilding.getHeight()) + newBuilding.getButtomYCord(), Utility.getUniqueNodeID(), (short) 5, floorNumber);
                                        visualRoomNodes.add(lowerRightCorner);

                                        upperLeftCorner = new Node(Utility.RoundUpIf0(xValuePercentage, newBuilding.getWidth()) + newBuilding.getButtomXCord(),
                                                Utility.RoundUpIf0(yValuePercentageUpperRight, newBuilding.getHeight()) + newBuilding.getButtomYCord(), Utility.getUniqueNodeID(), (short) 5, floorNumber);
                                        visualRoomNodes.add(upperLeftCorner);

                                        upperRightCorner = new Node(Utility.RoundUpIf0(xValuePercentageUpperRight, newBuilding.getWidth()) + newBuilding.getButtomXCord(),
                                                Utility.RoundUpIf0(yValuePercentageUpperRight, newBuilding.getHeight()) + newBuilding.getButtomYCord(), Utility.getUniqueNodeID(), (short) 5, floorNumber);
                                        visualRoomNodes.add(upperRightCorner);

                                        visualRoomEdges = newBuilding.getVisualEdge();

                                        newEdge = new Edge(lowerLeftCorner.getID() * 100000 + lowerRightCorner.getID(),
                                                lowerLeftCorner, lowerRightCorner, "walls", (short) 5, (short) 0, 0);
                                        newEdge.setEdgeClass((short) 9);
                                        visualRoomEdges.add(newEdge);

                                        newEdge = new Edge(lowerLeftCorner.getID() * 100000 + upperLeftCorner.getID(),
                                                lowerLeftCorner, upperLeftCorner, "walls", (short) 5, (short) 0, 0);
                                        visualRoomEdges.add(newEdge);
                                        newEdge.setEdgeClass((short) 9);

                                        newEdge = new Edge(upperRightCorner.getID() * 100000 + upperLeftCorner.getID(),
                                                upperRightCorner, upperLeftCorner, "walls", (short) 5, (short) 0, 0);
                                        visualRoomEdges.add(newEdge);
                                        newEdge.setEdgeClass((short) 9);

                                        newEdge = new Edge(upperRightCorner.getID() * 100000 + lowerRightCorner.getID(),
                                                upperRightCorner, lowerRightCorner, "walls", (short) 5, (short) 0, 0);
                                        visualRoomEdges.add(newEdge);
                                        newEdge.setEdgeClass((short) 9);

                                        sCurrentLine = br.readLine();
                                        sc = new Scanner(sCurrentLine);

                                        sCurrentLine = returnValues(sCurrentLine);
                                        sc = new Scanner(sCurrentLine);

                                        roomEdges = new ArrayList<>();

                                        indoorEdges = newBuilding.getEdges();

                                        while (sc.hasNextInt()){
                                            id = sc.nextInt();
                                            roomEdges.add(indoorEdges.get(id - 1));
                                        }

                                        newBuilding.setVisualNodes(visualRoomNodes);
                                        newBuilding.setVisualEdge(visualRoomEdges);

                                        newRoom = new Room(roomEdges);

                                        buildingRooms = newBuilding.getRooms();
                                        buildingRooms.add(newRoom);
                                        newBuilding.setRooms(buildingRooms);
                                    }
                                    else if (sCurrentLine.contains("</building>")){
                                        break;
                                    }
                                }
                                for (int i = 0 ; i < newBuilding.getRooms().size()-1; i++){
                                    newBuilding.getRooms().get(i).setID(i+1);
                                }

                                for (Node n : roomNodes){
                                    newBuilding.getRooms().get(n.getRoomNumber()-1).addNode(n);
                                }

                                for (int i = 0; i < newBuilding.getRooms().size()-1; i++) { //Saves information about which building the building nodes are in and which rooms
                                    for (Node n : newBuilding.getRooms().get(i).getNodes()){
                                        n.setName("Building template: " + newBuilding.getId() + " ButtomX: " + newBuilding.getButtomXCord() + " ButtomY: " + newBuilding.getButtomYCord() + " Room: " + (i+1));
                                    }
                                }
                                for (Node n : newBuilding.getNodes()){
                                    if (!n.getName().contains("Building template")){
                                        n.setName("Building template: " + newBuilding.getId() + " ButtomX: " + newBuilding.getButtomXCord() + " ButtomY: " + newBuilding.getButtomYCord() + " Room: " + 0); //TODO Should be something else
                                    }
                                }
                                buildings.add(newBuilding);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

/*                int maxFloorNumber = Utility.getMaxNumOfFloors()-1;
                int minFloorNumber = 0;
*/
                int numBuildingTypes = (buildings.size()-1);

                    int index = (int)Math.round(Math.random()* numBuildingTypes);
                    if (multibuildingGeneration != 0){
                        index = multibuildingGeneration;
                    }
                    tempBuilding = buildings.get(index);

                    //Will continue to pick a new block template until one that has a door node is found, if the building is on the first floor
                    if (tempBuilding.getDoorNodes().size() == 0 && j == 0) {
                        while (tempBuilding.getDoorNodes().size() == 0){
                            index = (int)Math.round(Math.random()* numBuildingTypes);
                            tempBuilding = buildings.get(index);
                        }
                    }

                    if (tempBuilding.isMultiBuilding() && multibuildingGeneration == 0 && (int)Math.ceil(tempBuilding.getWidth()/tempBuilding.getHeight()) > 1){

                        int numberOfMultiBuildings = (int)Math.ceil(tempBuilding.getWidth()/tempBuilding.getHeight());
                        double widthOfFace = tempBuilding.getWidth();
                        double multibuildingWidth = widthOfFace/numberOfMultiBuildings;
                        for (int i = 1; i <= numberOfMultiBuildings; i++) {
                            insertTemplate(minFaceWidth+((int) multibuildingWidth*(i)),faceHeight,faceMatrix,minFaceWidth+((int) multibuildingWidth*(i-1)),minFaceHeight,currentFace,index);
                        }
                        return currentFace;
                    }

                    tempBuilding.setFloorNumber(j);
                    //New floor number, used to set it for edges and nodes
                    short floor = (short)j;
                    //Setting floor number of all nodes and saving floor number in name:

                    for (Node n : tempBuilding.getNodes()){
                        n.setFloor(floor);
                        if (!n.getName().contains(" Floor: ") && n.getName().contains("Building template:")) {
                            n.setName(n.getName() + " Floor: " + n.getFloor());
                        }
                    }
                    //Setting edge number of all edges and saving floor number in name:
                    for (Edge e : tempBuilding.getEdges()){
                        e.setFloor(floor);
                        if (!e.getName().contains(" Floor: ")) {
                            e.setName(e.getName() + " Floor: " + e.getFloor());
                        }
                    }
                    for (Node n : tempBuilding.getVisualNodes()){
                        n.setFloor(floor);
                        if (!n.getName().contains(" Floor: ")) {
                            n.setName(n.getName() + " Floor: " + n.getFloor());
                        }
                    }
                    for (Edge e : tempBuilding.getVisualEdge()){
                        e.setFloor(floor);
                        if (!e.getName().contains(" Floor: ")) {
                            e.setName(e.getName() + " Floor: " + e.getFloor());
                        }
                    }

                    double shortestdist = Double.MAX_VALUE;
                    double currentDist = Double.MAX_VALUE;
                    Node closestNode = null;

                    //If building block is not the ground floor, then connect this buildings downNode with previous buildings upNode
                    if (j > 0){
                        for (Building b: currentFace.buildings) {
                            if (b.getFloorNumber() == tempBuilding.getFloorNumber()-1){
                                currentDist = Utility.EuclideanDistance(new Point(tempBuilding.getDownNode().x,tempBuilding.getDownNode().y),new Point(b.getUpNode().x,b.getUpNode().y));
                                if (currentDist < shortestdist){
                                    shortestdist = currentDist;
                                    closestNode = b.getUpNode();
                                }
                            }
                        }
                        newEdge = new Edge(closestNode.getID() * 100000 + tempBuilding.getDownNode().getID(),
                                closestNode, tempBuilding.getDownNode(), "inside", (short) 1, (short) 1, 0);
                        newEdge.setEdgeClass((short)11);
                        newEdge.setFloor(floor);
                        newEdge.setName(newEdge.getName() + " Stair ");
                        indoorEdges = tempBuilding.getEdges();
                        indoorEdges.add(newEdge);
                        tempBuilding.setEdges(indoorEdges);
                    }

                    //New building is added to the face
                    currentFace.buildings.add(tempBuilding);
                }
            }

            /**
             * Old simpler template:
             */
            //currentFace.buildings.add(new Building(1, faceWidth - minFaceWidth, faceHeight - minFaceHeight, minFaceWidth, minFaceHeight, 1));

            return currentFace;
    }

    public static String returnValues (String s){
        String[] tmp = s.split(">", 2);

        tmp = tmp[1].split("<");

        s = tmp[0];

        return s;
    }

}