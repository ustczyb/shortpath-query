package generator2;

import java.awt.*;
import java.awt.Font;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.BooleanSupplier;

import drawables.*;
import javafx.util.Pair;
import nu.xom.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import routing.*;
import routing.Node;
import routing.Nodes;
import showmap.*;
import spatial.*;
import src.MainsCombined;
import util.*;

/**
 * Abstract controller applet for the computation of network-based spatiotemporal datasets.
 * The abstract methods allow to use user-defined classes determining the bahavior of the generator. 
 * Non-abtract subclasses are the classes generator2.DefaultDataGenerator, generator2.OracleDataGenerator,
 * and generator2.OracleSpatialDataGenerator.
 * It is also possible to run these subclasses as Java applications.
 * See the additional documentation for the supported properties in the property file.
 *
 * @version	2.10	19.08.2003	considering null routes, tuned
 * @version	2.00	04.09.2001	complete revision
 * @version	1.22	15.06.2001	report of external objects added
 * @version	1.21	07.06.2001	compute() and compute(Reporter) separated, mode protected
 * @version	1.20	07.05.2001	textfields for msd & report probability added
 * @version	1.12	11.04.2001	waittime, maxSpeedDivisor introduced, adapted to changed classes
 * @version	1.11	01.06.2000	changed creation of container with drawable objects
 * @version	1.10	30.04.2000	support of external objects, reporting improved
 * @version	1.00	16.01.2000	first version
 * @author FH Oldenburg
 */
public abstract class DataGenerator extends ShowNetworkMap implements java.awt.event.AdjustmentListener {

	/**
	 * Stores all moving objects
	 */
	protected ArrayList<MovingObject> listOfObjects = new ArrayList<>();

	/**
	 * Properties of the data generator
	 */
	protected Properties properties = new Properties();
	/**
	 * Name of the property file
	 */
	protected static String propFilename = "properties.txt";

	/**
	 * Current displayed time (0 = all times)
	 */
	protected int actTime = 0;

	/**
	 * The time object.
	 */
	protected Time time = null;
	/**
	 * The data space.
	 */
	protected DataSpace dataspace = null;
	/**
	 * The edge classes.
	 */
	protected EdgeClasses edgeClasses = null;
	/**
	 * The classes of the moving objects.
	 */
	protected ObjectClasses objClasses = null;
	/**
	 * The external objects.
	 */
	protected ExternalObjects extObjects = null;
	/**
	 * The classes of external objects.
	 */
	protected ExternalObjectClasses extObjClasses = null;
	/**
	 * The reporter.
	 */
	protected Reporter reporter = null;

	/**
	 * Property file error
	 */
	public static final int PROPERTY_FILE_ERROR = -1;
	/**
	 * Application start error
	 */
	public static final int APPLICATION_START_ERROR = -2;
	/**
	 * Network file error
	 */
	public static final int NETWORKFILE_ERROR = -3;

	/**
	 * Maximum value for number of external objects at the beginning
	 */
	public static int MAX_EXTOBJBEGIN = 100;
	/**
	 * Maximum value for number of external object classes
	 */
	public static int MAX_EXTOBJCLASSES = 10;
	/**
	 * Maximum value for number of external objects per time
	 */
	public static int MAX_EXTOBJPERTIME = 10;
	/**
	 * Maximum value for maximum time
	 */
	public static int MAX_MAXTIME = 64000;
	/**
	 * Maximum value for number of moving objects at the beginning
	 */
	public static int MAX_OBJBEGIN = 1000;
	/**
	 * Maximum value for number of moving object classes
	 */
	public static int MAX_OBJCLASSES = 20;
	/**
	 * Maximum value for number of moving objects per time
	 */
	public static int MAX_OBJPERTIME = 800;
	/**
	 * Minimum value for maximum time
	 */
	public static int MIN_MAXTIME = 5;
	
	/**
	 * Waiting period between two time stamps in msec (a value larger 0 is required for painting the objects while the computation)
	 */
	protected int waitingPeriod = 0;

	/**
	 * The frame containing the applet if it is running as application.
	 */
	protected static Frame frame = null;
	/**
	 * Compute button
	 */
	private Button computeButton = null;
	/**
	 * Add time button
	 */
	private Button addTimeButton = null;
	/**
	 * Apply changes to maximum number of floors
	 */
	private Button applyMaxNumOfFloors = null;
	/**
	 * Reads the current moving object output file and draws objects
	 */
	private Button readMovingObjects = null;
	/**
	 * True if no previous buildings exsist
	 */
	private Boolean buildingsExsist = false;
	/**
	 * Scrollbar
	 */
	private Scrollbar timeScrollbar = null;
	/**
	 * Maximum time label
	 */
	private Label maxTimeLabel = null;
	/**
	 * Maximum time text field
	 */
	private TextField maxTimeText = null;
	/**
	 * Number of object classes label
	 */
	private Label numObjClassesLabel = null;
	/**
	 * Number of moving object classes text field
	 */
	private TextField numObjClassesText = null;
	/**
	 * Number of external object classes text field
	 */
	private TextField numExtObjClassesText = null;
	/**
	 * Objects per time label
	 */
	private Label objPerTimeLabel = null;
	/**
	 * Moving objects per time text field
	 */
	private TextField objPerTimeText = null;
	/**
	 * Objects at beginning label
	 */
	private Label objBeginLabel = null;
	/**
	 * External objects per time text field
	 */
	private TextField extobjPerTimeText = null;
	/**
	 * External objects at the beginning text field
	 */
	private TextField extobjBeginText = null;
	/**
	 * Textfield for specifying the maximum number of floors
	 */
	private TextField maxNumOfFloorsText = null;
	/**
	 * Textfield for specifying the maximum dwellingtime
	 */
	private TextField maxDwellingTimeText = null;
	/**
	 * Textfield for number of transfers allowed for the moving objects
	 */
	private TextField maxNumOfTransfersText = null;
	/**
	 * Checkbox for showing buildings
	 */
	private Checkbox showBuildingsCheckBox = null;
	/**
	 * Checkbox for indoor edges
	 */
	private Checkbox indoorEdgesCheckBox = null;
	/**
	 * Show dwelling edges
	 */
	private Checkbox showDwellingEdges = null;
	/**
	 * Label for max number of floors
	 */
	private Label maxNumberOfFloorsLabel = null;
	/**
	 * Label for max dwellingtime
	 */
	private Label maxDwellingTimeLabel = null;

	private Label pleaseWaitLabel = null;
	/**
	 * Label for max number of transfers
	 */
	private Label maxNumOfTransfersLabel = null;
	/**
	 * Scrollbar for showing floors
	 */
	private Scrollbar floorScrollBar = null;
	/**
	 * Delete button
	 */
	private Button deleteButton = null;
	/**
	 * Maximum speed divisor label
	 */
	private Label msdLabel = null;
	/**
	 * Maximum speed divisor text field
	 */
	private TextField msdText = null;
	/**
	 * Moving objects at the beginning text field
	 */
	private TextField objBeginText = null;
	/**
	 * Report probability label
	 */
	private Label reportProbLabel = null;
	/**
	 * Report probability text field
	 */
	private TextField reportProbText = null;

/**
 * main entrypoint - starts the part when it is run as an application
 * @param nameOfApplet complete name of the calling subclass
 */
public static void main (String nameOfApplet) {
	try {
		frame = new Frame("Network Generator");
		final DataGenerator aDataGenerator;
		Class iiCls = Class.forName(nameOfApplet);
		ClassLoader iiClsLoader = iiCls.getClassLoader();
		aDataGenerator = (DataGenerator)java.beans.Beans.instantiate(iiClsLoader,nameOfApplet);
		frame.add("Center", aDataGenerator);
		Dimension size = aDataGenerator.getSize();
		size.setSize(size.width + 278, size.height + 50);
		frame.setSize(size);
		// add a windowListener for the windowClosedEvent 
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of generator2.DataGenerator "+exception);
		System.exit(APPLICATION_START_ERROR);
	}
}

/**
 * Evaluates action events.
 * @param e action event
 */
public void actionPerformed (ActionEvent e) {
	super.actionPerformed(e);
	if (e.getSource() == getComputeButton())
		compute();
	if ((e.getSource() == getAddTimeButton()) && (time != null)) {
		int newTime = actTime+1;
		if (newTime <= time.getMaxTime()) {
			setTime (newTime);
			setTimeScrollbar (newTime);
			repaint();
		}
	}
	if (e.getSource() == getDeleteButton()) {
		deleteObjects();
		drawableObjects.removeAllObjectsOfLayer(Drawable.OBJECTLAYER);
		deleteButton.setEnabled(false);
		getAddTimeButton().setEnabled(false);
		getComputeButton().setEnabled(true);
		getReadMovingObjects().setEnabled(true);
	}

	if (e.getSource() == getReadMovingObjects()){

		Utility.assignSIDs();

		int snodeNumber = 5;
		Node sNode = null;

		for (Node n : Utility.getAllNodes()){
			if (n.getsId() == snodeNumber){
				sNode = n;
				break;
			}
		}

		bfs(sNode,1);
		System.out.println("Done with bfs 1");
		System.out.println("sNode coords: " + sNode.x + "," + sNode.y);
		int i = 0;

		for (Node n : Utility.getAllNodes()){
			if (n.getMark() == 1){
				DrawableSymbol pathNode = new DrawableSymbol(n.x,n.y,"bfs1" + i);
				i++;
				n.setLayer(Drawable.OBJECTLAYER);
				n.getPresentation().setVisibility(true);
				n.getPresentation().setSize(5);
				n.getPresentation().setColor(Color.black);
				n.getPresentation().setFillColor(Color.blue);
				drawableObjects.addDrawable(pathNode);
			}
		}

		System.out.println("Nodes in BFS:" + i);

		ArrayList<Node> nodesToRemove = new ArrayList<>();
		ArrayList<Edge> edgesToRemove = new ArrayList<>();

		ArrayList<Node> allNodes = Utility.getAllNodes();
		ArrayList<Edge> allEdges = Utility.getAllEdges();

		for (Node n : Utility.getAllNodes()){
			if (n.getFirstEdge() != null){
				if (n.getMark() != 1 && !n.getFirstEdge().getName().contains("walls")){
					nodesToRemove.add(n);

					for (Edge edge : n.getConnectedEdges()){
						edgesToRemove.add(edge);
					}

					DrawableSymbol pathNode = new DrawableSymbol(n.x,n.y,"bfs2" + i);
					i++;
					n.setLayer(Drawable.OBJECTLAYER);
					n.getPresentation().setVisibility(true);
					n.getPresentation().setSize(5);
					n.getPresentation().setColor(Color.black);
					n.getPresentation().setFillColor(Color.red);
					drawableObjects.addDrawable(pathNode);
				}
			}
		}

		for (Node n : nodesToRemove){
			allNodes.remove(n);
		}
		for (Edge edge : edgesToRemove){
			allEdges.remove(edge);
		}

		Utility.setAllNodes(allNodes);
		Utility.setAllEdges(allEdges);

		repaint();

		Utility.writeGraphToOtherFormat();

		ArrayList<Pair<Integer,Integer>>QuerySet = new ArrayList<>();

		int setsFound = 0;
		int setsBothInBuildings = 0;
		int setOneInBuilding = 0;
		int numberOfSets = 100;

		Random r = new Random();
		int sNodeSId = r.nextInt(Utility.maxSId);
		int tNodesId = r.nextInt(Utility.maxSId);

		Pair<Integer,Integer>stPair = new Pair<>(sNodeSId,tNodesId);
		Pair<Integer,Integer>tsPair = new Pair<>(tNodesId,sNodeSId);

		while (setsFound < numberOfSets){ //Used for generating query sets

			while (QuerySet.contains(stPair) || QuerySet.contains(tsPair)){

				sNodeSId = r.nextInt(Utility.maxSId);
				tNodesId = r.nextInt(Utility.maxSId);
				while (sNodeSId == tNodesId) {
					tNodesId = r.nextInt(Utility.maxSId);
				}
				stPair = new Pair<>(sNodeSId,tNodesId);
				tsPair = new Pair<>(tNodesId,sNodeSId);
			}

			QuerySet.add(stPair);
			if (Utility.nodesWithSIds.get(sNodeSId).getName().contains("Building") && Utility.nodesWithSIds.get(tNodesId).getName().contains("Building")){
				setsBothInBuildings++;
			}
			else if(Utility.nodesWithSIds.get(sNodeSId).getName().contains("Building") || Utility.nodesWithSIds.get(tNodesId).getName().contains("Building")){
				setOneInBuilding++;
			}

			setsFound++;
		}

		for (Pair p : QuerySet){
			System.out.print("stPair(" + p.getKey() + "," + p.getValue() + "),");
		}

		System.out.println("\nSets both in buildings = " + setsBothInBuildings + " Sets one in building = " + setOneInBuilding);

//		QuerySet.add(new Pair<>(39234,284356));
//
//		//For generating Dijkstra's algorithm alternatives without buildings
		ArrayList<Pair<Node,Node>> QuerySetNodes = new ArrayList<>();

		System.out.println("No buildings QuerySet:");

		for (Pair<Integer,Integer> p : QuerySet){
			if ((Utility.nodesWithSIds.get(p.getKey()).getName().contains("Floor") || Utility.nodesWithSIds.get(p.getKey()).getName().contains("Building")) && (Utility.nodesWithSIds.get(p.getValue()).getName().contains("Floor") || Utility.nodesWithSIds.get(p.getValue()).getName().contains("Building"))){
				QuerySetNodes.add(new Pair<>(Utility.findNearestNodeOnRoad(Utility.nodesWithSIds.get(p.getKey())),Utility.findNearestNodeOnRoad(Utility.nodesWithSIds.get(p.getValue()))));
			}
			else if ((Utility.nodesWithSIds.get(p.getKey()).getName().contains("Floor") || Utility.nodesWithSIds.get(p.getKey()).getName().contains("Building")) && (!Utility.nodesWithSIds.get(p.getValue()).getName().contains("Floor") && !Utility.nodesWithSIds.get(p.getValue()).getName().contains("Building"))){
				QuerySetNodes.add(new Pair<>(Utility.findNearestNodeOnRoad(Utility.nodesWithSIds.get(p.getKey())),Utility.nodesWithSIds.get(p.getValue())));
			}
			else if ((!Utility.nodesWithSIds.get(p.getKey()).getName().contains("Floor") && !Utility.nodesWithSIds.get(p.getKey()).getName().contains("Building")) && (Utility.nodesWithSIds.get(p.getValue()).getName().contains("Floor") || Utility.nodesWithSIds.get(p.getValue()).getName().contains("Building"))){
				QuerySetNodes.add(new Pair<>(Utility.nodesWithSIds.get(p.getKey()),Utility.findNearestNodeOnRoad(Utility.nodesWithSIds.get(p.getValue()))));
			}
			else {
				QuerySetNodes.add(new Pair<>(Utility.nodesWithSIds.get(p.getKey()),Utility.nodesWithSIds.get(p.getValue())));
			}
		}

		Utility.writeGraphToOtherFormatNoBuildings();

		for (Pair<Node,Node> p : QuerySetNodes){
			System.out.print("stPair(" + p.getKey().getsId() + "," + p.getValue().getsId() + "),");
		}

		System.out.println("Number of nodes: " + QuerySetNodes.size());

//		readMovingObjectsOutput();
//		Node actNode = null;
//		Nodes n = net.getNodes();
//		try{
//			DataInput nodeIn = new DataInputStream(new FileInputStream("./dataset/Oldenburg-d.co"));
//			int counter = 0;
//			while((actNode = n.readNewData(nodeIn)) != null && counter < 10){
//				//System.out.println(actNode.getID() + " " + actNode.x + " " + actNode.y + " " + actNode.getName());
//				counter++;
//			}
//		}
//		catch (Exception ex){
//			ex.printStackTrace();
//		}
//		Edge actEdge = null;
//		try{
//			DataInput edgeIn = new DataInputStream(new FileInputStream("./dataset/Oldenburg-d.gr"));
//			int counter = 0;
//			Edges edges = net.getEdges();
//			Nodes nodes = net.getNodes();
//			while((actEdge = edges.readNewData(edgeIn, nodes)) != null && counter < 10){
//				System.out.println(actEdge.getID() + " " + actEdge.getNode1().getID() + " " + actEdge.getNode2().getID() + " " + actEdge.getLength() +
//						" " + actEdge.getEdgeClass() + " " + actEdge.getName());
//				counter++;
//			}
//		}
//		catch (Exception ex){
//			ex.printStackTrace();
//		}
	}

	if (e.getSource() == getApplyMaxNumOfFloors()){
		getPleaseWaitLabel().setVisible(true);
		Utility.setMaxNumOfFloors(Integer.parseInt(getMaxNumOfFloorsTextField().getText()));
		drawableObjects.removeAll();

		getMaxNumOfFloorsTextField().setEnabled(false);
		getApplyMaxNumOfFloors().setEnabled(false);
		Utility.setAllNodes(new ArrayList<>());
		Utility.setAllEdges(new ArrayList<>());
		Utility.setAllFaces(new ArrayList<>());
		net.setEdges(new Edges());
		net.setNodes(new Nodes(net.getEdges()));

		MainsCombined.generateNewIndoorTopology(true);
		/*try {
			DataInputStream nodeIn = new DataInputStream(new FileInputStream("./dataset/network.node"));
			DataInputStream edgeIn = new DataInputStream(new FileInputStream("./dataset/network.edge"));
			getNetwork().createByNetworkFiles(nodeIn, edgeIn, drawableObjects); //TODO Write in documentation that they cannot change the name of the file that is being read
		}
		catch (FileNotFoundException f){
			System.out.println("Input data not found, try restarting the program");
		}*/

		getFloorScrollBar().setValue(0);
		getFloorScrollBar().setMaximum(Integer.parseInt(getMaxNumOfFloorsTextField().getText()));
		getPleaseWaitLabel().setVisible(false);

		frame.hide(); //Todo remove all edges, nodes and faces before starting new main
		main ("generator2.DefaultDataGenerator");

		//repaint();
	}
}

/**
 * Adds components to the applet.
 */
protected void addComponentsToApplet () {
	super.addComponentsToApplet();
	// remove unnecessary components of the super class
	remove(getTagLabel());
	remove(getValueLabel());
 	remove(getClickInfoLabel());
 	remove(getShiftClickInfoLabel());
 	remove(getPressInfoLabel());
 	// add additional components
 	add(getComputeButton(), getComputeButton().getName());
	add(getAddTimeButton(), getAddTimeButton().getName());
	add(getTimeScrollbar(), getTimeScrollbar().getName());
	add(getMaxTimeLabel(), getMaxTimeLabel().getName());
	add(getMaxTimeTextField(), getMaxTimeTextField().getName());
	add(getReportProbLabel(), getReportProbLabel().getName());
	add(getReportProbTextField(), getReportProbTextField().getName());
	add(getMsdLabel(), getMsdLabel().getName());
	add(getMsdTextField(), getMsdTextField().getName());
	add(getNumObjClassesLabel(), getNumObjClassesLabel().getName());
	add(getNumObjClassesTextField(), getNumObjClassesTextField().getName());
	add(getNumExtObjClassesTextField(), getNumExtObjClassesTextField().getName());
	add(getObjPerTimeLabel(), getObjPerTimeLabel().getName());
	add(getObjPerTimeTextField(), getObjPerTimeTextField().getName());
	add(getObjBeginLabel(), getObjBeginLabel().getName());
	add(getObjBeginTextField(), getObjBeginTextField().getName());
	add(getExtObjPerTimeTextField(), getExtObjPerTimeTextField().getName());
	add(getExtObjBeginTextField(), getExtObjBeginTextField().getName());
	add(getDeleteButton(), getDeleteButton().getName());
	//NEW UI ELEMENTS
	add(getMaxDwellingTimeText(), getMaxDwellingTimeText().getName());
	add(getMaxNumOfFloorsTextField(), getMaxNumOfFloorsTextField().getName());
	add(getMaxNumOfTransfersText(), getMaxNumOfTransfersText().getName());
	add(getShowBuildingsCheckBox(), getShowBuildingsCheckBox().getName());
	add(getShowDwellingEdges(), getShowDwellingEdges().getName());
	add(getIndoorEdgesCheckBox(), getIndoorEdgesCheckBox().getName());
	add(getMaxNumberOfFloorsLabel(), getMaxNumberOfFloorsLabel().getName());
	add(getMaxDwellingTimeLabel(), getMaxDwellingTimeLabel().getName());
	add(getMaxNumOfTransfersLabel(),getMaxNumOfTransfersLabel().getName());
	add(getFloorScrollBar(), getFloorScrollBar().getName());
	add(getApplyMaxNumOfFloors(), getApplyMaxNumOfFloors().getName());
	add(getPleaseWaitLabel(), getPleaseWaitLabel().getName());
	add(getReadMovingObjects(), getReadMovingObjects().getName());
}

/**
 * Adds the components to the listeners.
 */
protected void addComponentsToListeners () {
	super.addComponentsToListeners();
	getComputeButton().addActionListener(this);
	getAddTimeButton().addActionListener(this);
	getTimeScrollbar().addAdjustmentListener(this);
	getDeleteButton().addActionListener(this);
	getApplyMaxNumOfFloors().addActionListener(this);
	getShowBuildingsCheckBox().addItemListener(this);
	getShowDwellingEdges().addItemListener(this);
	getIndoorEdgesCheckBox().addItemListener(this);
	getFloorScrollBar().addAdjustmentListener(this);
	getReadMovingObjects().addActionListener(this);
}

/**
 * Reacts on an adjustment event.
 * @param e adjustment event
 */
public void adjustmentValueChanged (java.awt.event.AdjustmentEvent e) {
	if ((e.getSource() == getTimeScrollbar()) && (time != null)) {
		int newTime = e.getValue() * time.getMaxTime() / (timeScrollbar.getMaximum()-timeScrollbar.getVisibleAmount());
		if (newTime != actTime); {
			setTime(newTime);
			repaint();
		}
	}
	if (e.getSource() == getFloorScrollBar()){
		if (drawableObjects != null) {
			drawableObjects.removeAllObjectsOfLayer(Drawable.BUILDINGLAYER);
			drawableObjects.removeAllObjectsOfLayer(Drawable.DWELLINGLAYER);
			drawableObjects.removeAllObjectsOfLayer(Drawable.INSIDELAYER);
			drawableObjects.removeAllObjectsOfLayer(Drawable.OBJECTLAYER);

			Utility.setActFloorVisual(getFloorScrollBar().getMinimum()
					+getFloorScrollBar().getMaximum()-getFloorScrollBar().getValue()-1);

			for (Edge edge : Utility.getAllEdges()){
				drawEdgeIfRightFloor(edge);
			}

			if (!getComputeButton().isEnabled()) {
				setTime(actTime);
			}

			repaint();
		}
	}
}
	/**
	 * Method that checks if an edge is supposed to be drawn or not
	 */
	private void drawEdgeIfRightFloor(Edge edge){
		if (edge.getFloor() == Utility.getActFloorVisual()){
			if (edge.getLayer() == 6 && getIndoorEdgesCheckBox().getState()) {
				drawableObjects.addDrawable(edge);
			}
			else if (edge.getLayer() == 7 && getShowDwellingEdges().getState()){
				drawableObjects.addDrawable(edge);
			}
			else if (edge.getLayer() == 5 && getShowBuildingsCheckBox().getState()) {
				drawableObjects.addDrawable(edge);
			}
		}
	}
	/**
	 * Reacts on item events
	 */
	public void itemStateChanged(ItemEvent e){
		super.itemStateChanged(e);
		//Showing and hiding building edges
		if (e.getSource() == getShowBuildingsCheckBox() && !getShowBuildingsCheckBox().getState()){
			if (drawableObjects != null) {
				drawableObjects.removeAllObjectsOfLayer(5);
				repaint();
			}
		}
		if (e.getSource() == getShowBuildingsCheckBox() && getShowBuildingsCheckBox().getState()){
			if (drawableObjects != null){
				for (Edge edge : Utility.getAllEdges()){
					if (edge.getName().contains("walls")){
						drawEdgeIfRightFloor(edge);
					}
				}
			}
			repaint();
		}
		//Showing and hiding indoor edges
		if (e.getSource() == getIndoorEdgesCheckBox() && !getIndoorEdgesCheckBox().getState()) {
			if (drawableObjects != null) {
				drawableObjects.removeAllObjectsOfLayer(6);
				repaint();
			}
		}
		if (e.getSource() == getIndoorEdgesCheckBox() && getIndoorEdgesCheckBox().getState()){
			if (drawableObjects != null) {
				for (Edge edge : Utility.getAllEdges()){
					if (edge.getName().contains("inside")){
						drawEdgeIfRightFloor(edge);
					}
				}
				repaint();
			}
		}
		//Showing and hiding dwelling edges
		if (e.getSource() == getShowDwellingEdges() && !getShowDwellingEdges().getState()) {
			if (drawableObjects != null) {
				drawableObjects.removeAllObjectsOfLayer(7);
				repaint();
			}
		}
		if (e.getSource() == getShowDwellingEdges() && getShowDwellingEdges().getState()){
			if (drawableObjects != null) {
				for (Edge edge : Utility.getAllEdges()){
					if (edge.getName().contains("dwelling")){
						drawEdgeIfRightFloor(edge);
					}
				}
				repaint();
			}
		}

	}

/**
 * Computes the position of the components.
 */
public void changeComponentPositions() {
	super.changeComponentPositions();
	getScaleLabel().setBounds(viewX, viewY+viewHeight+4, 70,23);
	getComputeButton().setBounds (viewX, viewY+viewHeight+48, 76,29);
	getAddTimeButton().setBounds (viewX+viewWidth-76, viewY+viewHeight+48, 76,29);
	getTimeScrollbar().setBounds (viewX, viewY+viewHeight+140, viewWidth,18);
	getNameLabel().setBounds (viewX+90, viewY+ viewHeight+4, 60,23);
	getMaxTimeLabel().setBounds (viewX, viewY+viewHeight+82, 140,23);
	getMaxTimeTextField().setBounds (viewX+150, viewY+viewHeight+82, 40,23);
	getNumObjClassesLabel().setBounds (viewX+viewWidth/2+25, viewY+viewHeight+82, 155,23);
	getNumObjClassesTextField().setBounds (viewX+viewWidth/2+185, viewY+viewHeight+82, 30,23);
	getNumExtObjClassesTextField().setBounds (viewX+viewWidth/2+220, viewY+viewHeight+82, 30,23);
	getObjBeginLabel().setBounds (viewX+viewWidth/2+25, viewY+viewHeight+1, 155,23);
	getObjBeginTextField().setBounds (viewX+viewWidth/2+185, viewY+viewHeight+1, 30,21);
	getExtObjBeginTextField().setBounds (viewX+viewWidth/2+220, viewY+viewHeight+1, 30,21);
	getObjPerTimeLabel().setBounds (viewX+viewWidth/2+25, viewY+viewHeight +23, 155,23);
	getObjPerTimeTextField().setBounds (viewX+viewWidth/2+185, viewY+viewHeight+ 23, 30,21);
	getExtObjPerTimeTextField().setBounds (viewX+viewWidth/2+220, viewY+viewHeight+23, 30,21);
	getCopyrightLabel().setBounds(viewX, viewY+viewHeight+180, viewWidth-100, 19);
	getReportProbLabel().setBounds (viewX, viewY+viewHeight + 112, 148,23);
	getReportProbTextField().setBounds (viewX+150, viewY +viewHeight+112, 40,23);
	getMsdLabel().setBounds (viewX+195, viewY+viewHeight+112, 260,23);
	getMsdTextField().setBounds (viewX+viewWidth/2+220, viewY+viewHeight+112, 30,23);
	getDeleteButton().setBounds (viewX+150, viewY+viewHeight+6, 75,29);
	//NEW UI ELEMENTS Todo remove this comment
	getMaxNumOfFloorsTextField().setBounds(viewX+viewWidth + (679-500), viewY+viewHeight+1, 21,21);
	getMaxDwellingTimeText().setBounds(viewX + viewWidth + (679-500), viewY + viewHeight + 23, 21, 21);
	getMaxNumOfTransfersText().setBounds(viewX + viewWidth + (679-500), viewY + viewHeight + 45, 21, 21);
	getShowBuildingsCheckBox().setBounds(viewX + viewWidth + (550-500), viewY + viewHeight + 67, 150, 21);
	getShowDwellingEdges().setBounds(viewX + viewWidth + (550-500), viewY + viewHeight + 89, 150, 21);
	getIndoorEdgesCheckBox().setBounds(viewX + viewWidth + (550-500), viewY + viewHeight + 111, 150, 21);
	getMaxNumberOfFloorsLabel().setBounds(viewX + viewWidth + (550-500), viewY+viewHeight+1,150,21);
	getMaxDwellingTimeLabel().setBounds(viewX + viewWidth + (550-500), viewY + viewHeight + 23,150,21);
	getMaxNumOfTransfersLabel().setBounds(viewX + viewWidth + (550-500), viewY + viewHeight + 45, 150, 21);
	getFloorScrollBar().setBounds(viewX + viewWidth + (750-500), viewY + viewHeight, 18,170);
	getApplyMaxNumOfFloors().setBounds(viewX + viewWidth + (704-500), viewY+viewHeight+1, 42, 21);
	getPleaseWaitLabel().setBounds(viewX + viewWidth + (620-500), viewY + viewHeight - 22, 150, 21);
	getReadMovingObjects().setBounds(viewX + viewWidth + (550-500), viewY + viewHeight+140 ,84, 21);
}

/**
 * Computes the dataset.
 * Order of initialization: <ol>
 * <li>Time (only first call)
 * <li>ObjectClasses (only first call)
 * <li>ExternalObjectClasses (only first call)
 * <li>ExternalObjectGenerator
 * <li>ExternalObjects (only first call)
 * <li>Reporter
 * <li>WeightManagerForDataGenerator
 * <li>ReRoute
 * <li>ObjectGenerator
 * <li>MovingObjects
 * </ol>
 */
public synchronized void compute () {
	getComputeButton().setEnabled(false);
	getReadMovingObjects().setEnabled(false);
	Utility.setMaxDwellingTime(Integer.parseInt(getMaxDwellingTimeText().getText())); //Sets the maximum dwellingtime to whatever the user has inputted
	Utility.setMaxTranfers(Integer.parseInt(getMaxNumOfTransfersText().getText())); //Sets the maximum number of transfers
	showStatus("initialize generation...");

	// access to the network
	Network net = getNetwork();
	Nodes nodes = net.getNodes();
	Edges edges = net.getEdges();

	// initialization of the necessary classes
	if (time == null) {
		time = new Time (properties,getValueOfTextField(getMaxTimeTextField(),MIN_MAXTIME,MAX_MAXTIME,false));
		dataspace = new DataSpace (drawableObjects);
		objClasses = createObjectClasses (properties,time,dataspace,getValueOfTextField(getNumObjClassesTextField(),1,MAX_OBJCLASSES,false),
			getValueOfTextField(getReportProbTextField(),0,1000,false),getValueOfTextField(getMsdTextField(),1,1000,true));
		extObjClasses = createExternalObjectClasses (properties,time,dataspace,getValueOfTextField(getNumExtObjClassesTextField(), 1, MAX_EXTOBJCLASSES, false));
		if (properties.getProperty(Reporter.VIZ)!=null) {
			for (int c=0; c<objClasses.getNumber(); c++)
				for (int i=0; i<=time.getMaxTime(); i++)
					DrawablePresentation.newDrawablePresentation ("Point"+c+"-"+i,false,objClasses.getColor(c),Color.red,DrawableSymbol.CIRCLE,8);
			for (int c=0; c<extObjClasses.getNumber(); c++)
				for (int i=0; i<=time.getMaxTime(); i++)
					DrawablePresentation.newDrawablePresentation ("Rectangle"+c+"-"+i,false,extObjClasses.getColor(c),Color.red);
		}
	}
	time.reset();
	edgeClasses.announce (time,dataspace,getValueOfTextField(getMsdTextField(),1,1000,true));
	ExternalObjectGenerator extObjGen = createExternalObjectGenerator (properties,time,dataspace,extObjClasses,
		getValueOfTextField(getExtObjPerTimeTextField(),0,MAX_EXTOBJPERTIME,true),getValueOfTextField(getExtObjBeginTextField(),0,MAX_EXTOBJBEGIN,true));
	boolean extObjectsExist = extObjGen.externalObjectsExist();
	if (extObjectsExist && (extObjects == null))
		extObjects = new ExternalObjects (properties,time,extObjClasses);
	reporter = createReporter (properties,drawableObjects);
	deleteButton.setEnabled(true);
	WeightManagerForDataGenerator wm = null;
	if (extObjectsExist)
		wm = new WeightManagerForDataGenerator (edgeClasses,objClasses,extObjects);
	else
		wm = new WeightManagerForDataGenerator (edgeClasses,objClasses,null);
	edges.setWeightManager (wm);
	ReRoute reroute = createReRoute(properties, time, dataspace);
	ObjectGenerator objGen = createObjectGenerator(properties, time, dataspace, nodes, objClasses, getValueOfTextField(getObjPerTimeTextField(), 0, MAX_OBJPERTIME, true), getValueOfTextField(getObjBeginTextField(), 0, MAX_OBJBEGIN, true));
	MovingObjects movingObjects = new MovingObjects (wm,net,objGen,reporter,reroute);
	listOfObjects = new ArrayList<>(); //Stores all moving objects
	// the time starts
	showStatus("generate data, please wait...");
	util.Timer.reset(1);
	util.Timer.reset(2);
	util.Timer.start(1);
	actTime = time.getCurrTime();
	// traverse the time
	while (!time.isMaximumTimeExceeded()) {
		// move and report all external objects, remove the dead objects
		if (extObjectsExist)
			extObjects.moveAndResizeAndRemoveObjects(actTime,extObjGen,reporter);
		// move and report all moving objects, remove the objects reaching the destination
		movingObjects.move(actTime);
		// generate new external objects
		int numOfNewExtObjects = extObjGen.numberOfNewObjects(actTime);
		for (int i=0; i<numOfNewExtObjects; i++) {
			ExternalObject extObj = extObjGen.computeExternalObject(actTime);
			extObj.addToContainer (extObjects);
			extObj.reportNewObject (reporter);
		}
		// generate new moving objects
		int numOfNewObjects = objGen.numberOfNewObjects(actTime);
		for (int i=0; i<numOfNewObjects; i++) {
			// for each new moving object, determine its properties and create it, ...
			int id = objGen.computeId(actTime);
			int objClass = objGen.computeObjectClass(actTime);
			Node start = objGen.computeStartingNode(actTime,objClass);
			Node dest = objGen.computeDestinationNode(actTime,start,objGen.computeLengthOfRoute(actTime,objClass),objClass);
			MovingObject obj = new MovingObject (id,objClass,start,dest,actTime, objGen);
			obj.addToContainer (movingObjects);
			listOfObjects.add(obj);
			// and compute the (first) route
			while (! obj.computeRoute()) {
				obj.setStart(objGen.computeStartingNode(actTime, objClass));
				obj.setDestination(objGen.computeDestinationNode(actTime, start, objGen.computeLengthOfRoute(actTime, objClass), objClass));
			}
			obj.reportNewObject (reporter);
		}
		// show object if there is enough time
		if (waitingPeriod > 0) {
			if (!Time.isFirstTimeStamp(actTime)) {
				for (int c=0; c<objClasses.getNumber(); c++)
					DrawablePresentation.get("Point"+c+"-"+(this.actTime-1)).setVisibility(false);
				for (int c=0; c<extObjClasses.getNumber(); c++)
					DrawablePresentation.get("Rectangle"+c+"-"+(this.actTime-1)).setVisibility(false);
			}
			for (int c=0; c<objClasses.getNumber(); c++)
				DrawablePresentation.get("Point"+c+"-"+this.actTime).setVisibility(true);
			for (int c=0; c<extObjClasses.getNumber(); c++)
				DrawablePresentation.get("Rectangle"+c+"-"+this.actTime).setVisibility(true);
			update(getGraphics());
		}
		// to the next time stamp
		time.increaseCurrTime();
		actTime = time.getCurrTime();
		if (actTime % 25 == 0)
			System.gc();
		reportProgress (actTime);
		// wait
		if (waitingPeriod > 0)
			try {wait(waitingPeriod);} catch (Exception e){System.err.println("wait: "+e);}
	}
	writeMovingObjectOutput(listOfObjects);
	util.Timer.stop(1);
	// report and remove all still existing objects
	showStatus("remove remaining objects and report statistics...");
	movingObjects.removeObjects();
	if (extObjectsExist)
		extObjects.removeObjects();
	// report statistics
	long totalTime = util.Timer.get(1);
	long routingTime = util.Timer.get(2);
	int numOfRoutes = movingObjects.getTotalNumOfObjects()+reroute.getNumberOfRoutesByEvent()+reroute.getNumberOfRoutesByComparison();
	int dx = dataspace.getMaxX()-dataspace.getMinX();
	int dy = dataspace.getMaxY()-dataspace.getMinY();
	reporter.reportInt("data space width: ",dx);
	reporter.reportInt("data space height: ",dy);
	reporter.reportInt("number of nodes: ",nodes.numOfNodes());
	reporter.reportInt("number of edges: ",edges.numOfEdges());
	reporter.reportInt("maximum time: ",time.getMaxTime());
	reporter.reportInt("# moving objects: ",movingObjects.getTotalNumOfObjects());
	reporter.reportInt("# points: ",reporter.getNumberOfReportedPoints());
	reporter.reportInt("# traversed nodes: ",movingObjects.getTotalNumberOfTraversedNodes());
	reporter.reportDouble("# nodes/obj: ",((double)movingObjects.getTotalNumberOfTraversedNodes())/movingObjects.getTotalNumOfObjects());
	reporter.reportDouble("# traversed degree: ",movingObjects.getTotalDegreeOfTraversedNodes());
	double nodeDegree = (double)(movingObjects.getTotalDegreeOfTraversedNodes()-movingObjects.getTotalNumberOfTraversedNodes())/movingObjects.getTotalNumberOfTraversedNodes();
	reporter.reportDouble("# node degree: ",nodeDegree);
	reporter.reportInt("# all routes: ",numOfRoutes);
	reporter.reportInt("# routes by event: ",reroute.getNumberOfRoutesByEvent());
	reporter.reportInt("# routes by comparison: ",reroute.getNumberOfRoutesByComparison());
	reporter.reportInt("total time in ms: ",totalTime);
	reporter.reportDouble("total time/obj: ",((double)totalTime/movingObjects.getTotalNumOfObjects()));
	reporter.reportDouble("total time/point: ",((double)totalTime/reporter.getNumberOfReportedPoints()));
	reporter.reportDouble("routing time in ms: ",routingTime);
	reporter.reportDouble("insert time: ",BorderHeap.insertTimer.get());
	reporter.reportDouble("fetch time:  ",BorderHeap.fetchTimer.get());
	reporter.reportDouble("change time: ",BorderHeap.changeTimer.get());
	reporter.reportDouble("routing time/obj: ",((double)routingTime/movingObjects.getTotalNumOfObjects()));
	reporter.reportDouble("routing time/point: ",((double)routingTime/reporter.getNumberOfReportedPoints()));
	reporter.reportDouble("routing time/node: ",((double)routingTime/movingObjects.getTotalNumberOfTraversedNodes()));
	reporter.reportDouble("routing time/node/nodedegr: ",((double)routingTime/movingObjects.getTotalNumberOfTraversedNodes()/nodeDegree));
	reporter.reportDouble("routing time/routing: ",((double)routingTime/numOfRoutes));
	if (extObjectsExist) {
		reporter.reportInt("# computed decreases: ",extObjects.getNumOfComputedDecreases());
		reporter.reportInt("# real decreases: ",extObjects.getNumOfRealDecreases());
		reporter.reportInt("time for external objects in ms: ",extObjects.getUsedTime());
	}
	long totalDistance = 0;
	for (Enumeration e = edges.elements(); e.hasMoreElements();) {
		Edge edge = (Edge)e.nextElement();
		totalDistance += edge.getLength();
	}
	int avDistance = (int)(totalDistance/edges.numOfEdges());
	reporter.reportInt("average edge length: ",avDistance);
	reporter.reportInt("average route length: ",objGen.getAverageRouteLength());
	reporter.close();

	setTime (0);
	setTimeScrollbar (0);
	this.addTimeButton.setEnabled(true);
	repaint();
	showStatus("ready...");
}

/**
 * Calls the constructor of EdgeClasses.
 * Must be implemented by a subclass of DataGenerator.
 * @return an object of the class EdgeClasses
 * @param properties the properties of the generator
 */
public abstract EdgeClasses createEdgeClasses(Properties properties);

/**
 * Calls the constructor of ExternalObjectClasses.
 * Must be implemented by a subclass of DataGenerator.
 * @return an object of ExternalObjectClasses
 * @param properties properties of the generator
 * @param time the time object
 * @param ds the data space
 * @param numOfClasses number of external object classes
 */
public abstract ExternalObjectClasses createExternalObjectClasses (Properties properties, Time time, DataSpace ds, int numOfClasses);

/**
 * Calls the constructor of ExternalObjectGenerator.
 * Must be implemented by a subclass of DataGenerator.
 * @return an external object generator
 * @param properties properties of the generator
 * @param time the time object
 * @param dataspace the dataspace
 * @param classes the classes of external objects
 * @param numOfExtObjPerTime number of external objects per time
 * @param numAtBeginning number of external objects at the beginning
 */
public abstract ExternalObjectGenerator createExternalObjectGenerator(Properties properties, Time time, DataSpace dataspace, ExternalObjectClasses classes, int numOfExtObjPerTime, int numAtBeginning);

/**
 * Calls the constructor of ObjectClasses.
 * Must be implemented by a subclass of DataGenerator.
 * @return an object of ObjectClasses
 * @param properties properties of the generator
 * @param time the time object
 * @param ds the data space
 * @param numOfClasses number of object classes
 * @param reportProb report probability (0-1000)
 * @param maxSpeedDivisor maximum speed divisor
 */
public abstract ObjectClasses createObjectClasses (Properties properties, Time time, DataSpace ds, int numOfClasses, int reportProb, int maxSpeedDivisor);

/**
 * Calls the constructor of ObjectGenerator.
 * Must be implemented by a subclass of DataGenerator.
 * @return an object generator
 * @param properties properties of the generator
 * @param time the time object
 * @param ds the dataspace
 * @param nodes the nodes of the network
 * @param objClasses description of the object classes
 * @param numOfObjPerTime indicator for the number of objects per time
 * @param numOfObjAtBeginning indicator for the number of objects at the beginning
 */
public abstract ObjectGenerator createObjectGenerator(Properties properties, Time time, DataSpace ds, Nodes nodes, ObjectClasses objClasses, int numOfObjPerTime, int numOfObjAtBeginning);

/**
 * Calls the constructor of Reporter.
 * Must be implemented by a subclass of DataGenerator.
 * @return the reporter
 * @param properties properties of the generator
 * @param objects container of drawable objects
 */
public abstract Reporter createReporter(Properties properties, DrawableObjects objects);

/**
 * Calls the constructor of ReRoute.
 * Must be implemented by a subclass of DataGenerator.
 * @return an object of ReRoute
 * @param properties properties of the generator
 * @param time the time object
 * @param ds the data space
 */
public abstract ReRoute createReRoute (Properties properties, Time time, DataSpace ds);

/**
 * Deletes the generated moving and external objects.
 */
protected void deleteObjects() {
	if (reporter != null)
		reporter.removeReportedObjects();
	repaint();
}

	/** Todo Remove this NEW text
	 * Button for applying changes to max num of floors
	 */
	protected Button getApplyMaxNumOfFloors(){
		if (applyMaxNumOfFloors == null){
			applyMaxNumOfFloors = new Button();
			applyMaxNumOfFloors.setName("ApplyMaxNumOfFloors");
			applyMaxNumOfFloors.setFont(new Font("Dialog", 0, 12));
			applyMaxNumOfFloors.setLabel("Apply");
			applyMaxNumOfFloors.setEnabled(true);
		}
		return applyMaxNumOfFloors;
	}

	protected Button getReadMovingObjects(){
		if (readMovingObjects== null){
			readMovingObjects = new Button();
			readMovingObjects.setName("ReadMovingObjects");
			readMovingObjects.setFont(new Font("Dialog", 0, 12));
			readMovingObjects.setLabel("Convert Data");
			readMovingObjects.setEnabled(true);
		}
		return readMovingObjects;
	}

	/** NEW LABEL
	 * Label saying "please wait" while waiting for buildings to be generated
	 */
	protected Label getPleaseWaitLabel(){
		if (pleaseWaitLabel == null){
			pleaseWaitLabel = new Label();
			pleaseWaitLabel.setName("PleaseWaitLabel");
			pleaseWaitLabel.setFont(new Font("sansserif", 0, 11));
			pleaseWaitLabel.setText("Please wait");
			pleaseWaitLabel.setVisible(false);
		};
		return pleaseWaitLabel;
	}

	/** NEW TEXTFIELD
	 * TextField for number of max floors
	 */
	protected TextField getMaxNumOfFloorsTextField(){
		if (maxNumOfFloorsText == null){
			maxNumOfFloorsText = new TextField(Integer.toString(Utility.getMaxNumOfFloors()));
			maxNumOfFloorsText.setName("maxNumOfFloorsTextField");
			maxNumOfFloorsText.setFont(new Font("Dialog", 0, 11));
		}
		return maxNumOfFloorsText;
	}
	/** NEW TEXTFIELD
	 * TextField for maximum dwelling time
	 */
	protected TextField getMaxDwellingTimeText(){
		if (maxDwellingTimeText == null){
			maxDwellingTimeText = new TextField("10");
			maxDwellingTimeText.setName("maxDwellingTimeText");
			maxDwellingTimeText.setFont(new Font("Dialog", 0, 11));
		}
		return maxDwellingTimeText;
	}
	/** NEW TEXTFIELD
	 * TextField for maximum number of transfers
	 */
	protected TextField getMaxNumOfTransfersText(){
		if (maxNumOfTransfersText == null){
			maxNumOfTransfersText = new TextField("5");
			maxNumOfTransfersText.setName("maxNumOfTransfersText");
			maxNumOfTransfersText.setFont(new Font("Dialog", 0, 11));
		}
		return maxNumOfTransfersText;
	}
	/** NEW CHECKBOX
	 * CheckBox for showing buildings
	 */
	protected Checkbox getShowBuildingsCheckBox(){
		if (showBuildingsCheckBox == null){
			showBuildingsCheckBox = new Checkbox("Show buildings");
			showBuildingsCheckBox.setName("ShowBuildingsCheckBox");
			showBuildingsCheckBox.setFont(new Font("Dialog", 0, 11));
			showBuildingsCheckBox.setState(true);
		}
		return showBuildingsCheckBox;
	}

	/** NEW CHECKBOX
	 * Creates indoor edges checkbox
	 * @return indoor edges checkbox
	 */
	protected Checkbox getIndoorEdgesCheckBox(){
		if (indoorEdgesCheckBox == null){
			indoorEdgesCheckBox = new Checkbox("Show indoor edges");
			indoorEdgesCheckBox.setName("IndoorEdgesCheckBox");
			indoorEdgesCheckBox.setFont(new Font("Dialog", 0, 11));
		}
		return indoorEdgesCheckBox;
	}

	/**
	 * Creates show dwelling edges checkbox
	 * @return dwelling edges checkbox
	 */
	protected Checkbox getShowDwellingEdges(){
		if (showDwellingEdges == null){
			showDwellingEdges = new Checkbox("Show dwelling Edges");
			showDwellingEdges.setName("showDwellingEdges");
			showDwellingEdges.setFont(new Font("Dialog", 0, 11));
		}
		return showDwellingEdges;
	}
	/**
	 * Label for maximum number of floors
	 */
	protected Label getMaxNumberOfFloorsLabel(){
		if (maxNumberOfFloorsLabel == null){
			maxNumberOfFloorsLabel = new Label();
			maxNumberOfFloorsLabel.setName("MaxNumberOfFloorsLabel");
			maxNumberOfFloorsLabel.setFont(new Font("sansserif", 0, 11));
			maxNumberOfFloorsLabel.setText("Max number of floors:");
		};
		return maxNumberOfFloorsLabel;
	}

	/**
	 * Label for max dwelling time
	 * @return
	 */
	protected Label getMaxDwellingTimeLabel(){
		if (maxDwellingTimeLabel == null){
			maxDwellingTimeLabel = new Label();
			maxDwellingTimeLabel.setName("maxDwellingTimeLabel");
			maxDwellingTimeLabel.setFont(new Font("sansserif", 0, 11));
			maxDwellingTimeLabel.setText("Max dwelling time:");
		};
		return maxDwellingTimeLabel;
	}

	/**
	 * Label for max number of transfers
	 * @return
	 */
	protected Label getMaxNumOfTransfersLabel(){
		if (maxNumOfTransfersLabel == null){
			maxNumOfTransfersLabel = new Label();
			maxNumOfTransfersLabel.setName("maxNumOfTransfersLabel");
			maxNumOfTransfersLabel.setFont(new Font("sansserif",0,11));
			maxNumOfTransfersLabel.setText("Max number of transfers:");
		};
		return maxNumOfTransfersLabel;
	}

	/**
	 * Scrollbar for showing floors
	 * @return
	 */
	protected Scrollbar getFloorScrollBar(){
		if (floorScrollBar == null){
			floorScrollBar = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, 1);
			floorScrollBar.setName("FloorScrollBar");
		}
		return floorScrollBar;
	}

/**
 * Creates / Returns the add time button.
 * @return add time button
 */
protected Button getAddTimeButton() {
	if (addTimeButton == null) {
		addTimeButton = new Button();
		addTimeButton.setName("AddTimeButton");
		addTimeButton.setFont(new Font("Dialog", 0, 12));
		addTimeButton.setLabel("Time +");
		addTimeButton.setEnabled(false);
	};
	return addTimeButton;
}

/**
 * Creates / Returns the compute button.
 * @return compute button
 */
protected Button getComputeButton() {
	if (computeButton == null) {
		computeButton = new Button();
		computeButton.setName("ComputeButton");
		computeButton.setFont(new Font("Dialog", 0, 12));
		computeButton.setLabel("Compute");
		computeButton.setEnabled(false);
	};
	return computeButton;
}

/**
 * Creates / Returns the delete button.
 * @return delete button
 */
protected Button getDeleteButton() {
	if (deleteButton == null) {
		deleteButton = new Button();
		deleteButton.setName("DeleteObjectsButton");
		deleteButton.setFont(new Font("Dialog", 0, 12));
		deleteButton.setLabel("Delete Obj.");
		deleteButton.setEnabled(false);
	};
	return deleteButton;
}

/**
 * Creates / Returns the external objects at the beginning text field.
 * @return text field
 */
protected TextField getExtObjBeginTextField () {
	if (extobjBeginText == null) {
		extobjBeginText = new TextField("0");
		extobjBeginText.setName("ExtObjBeginTextField");
		extobjBeginText.setFont(new Font("Dialog", 0, 11));
	};
	return extobjBeginText;
}

/**
 * Creates / Returns the external objects per time text field.
 * @return text field
 */
protected TextField getExtObjPerTimeTextField () {
	if (extobjPerTimeText == null) {
		extobjPerTimeText = new TextField("0");
		extobjPerTimeText.setName("ExtObjPerTimeTextField");
		extobjPerTimeText.setFont(new Font("Dialog", 0, 11));
	};
	return extobjPerTimeText;
}

/**
 * Returns an info text for a drawable object.
 * @return info text
 * @param obj drawable object
 */
protected String getInfoText (DrawableObject obj) {
	return null;
}

/**
 * Creates / returns the maximum time label.
 * @return maximum time label
 */
protected Label getMaxTimeLabel() {
	if (maxTimeLabel == null) {
		maxTimeLabel = new Label();
		maxTimeLabel.setName("MaxTimeLabel");
		maxTimeLabel.setFont(new Font("sansserif", 0, 12));
		maxTimeLabel.setText("maximum time (" + MIN_MAXTIME + "-" + MAX_MAXTIME + "):");
	};
	return maxTimeLabel;
}

/**
 * Creates / Returns the maximum time text field.
 * @return maximum time text field
 */
protected TextField getMaxTimeTextField () {
	if (maxTimeText == null) {
		maxTimeText = new TextField("20");
		maxTimeText.setName("MaxTimeTextField");
		maxTimeText.setFont(new Font("Dialog", 0, 11));
	};
	return maxTimeText;
}

/**
 * Creates / returns the maximum speed divisor label.
 * @return label
 */
protected Label getMsdLabel() {
	if (msdLabel == null) {
		msdLabel = new Label();
		msdLabel.setName("MsdLabel");
		msdLabel.setFont(new Font("sansserif", 0, 12));
		msdLabel.setText("max.speed div. (10=fast,50=middle,250=slow):");
	};
	return msdLabel;
}

/**
 * Creates / Returns the maximum speed divisor text field.
 * @return text field
 */
protected TextField getMsdTextField () {
	if (msdText == null) {
		msdText = new TextField("50");
		msdText.setName("ReportProbTextField");
		msdText.setFont(new Font("Dialog", 0, 11));
	};
	return msdText;
}

/**
 * Creates / Returns the number of object classes text field.
 * @return number of object classes text field
 */
protected TextField getNumExtObjClassesTextField () {
	if (numExtObjClassesText == null) {
		numExtObjClassesText = new TextField("3");
		numExtObjClassesText.setName("NumExtObjClassesTextField");
		numExtObjClassesText.setFont(new Font("Dialog", 0, 11));
	};
	return numExtObjClassesText;
}

/**
 * Creates / returns the number of object classes label.
 * @return number of object classes label
 */
protected Label getNumObjClassesLabel() {
	if (numObjClassesLabel == null) {
		numObjClassesLabel = new Label();
		numObjClassesLabel.setName("NumObjClassesLabel");
		numObjClassesLabel.setFont(new Font("sansserif", 0, 12));
		numObjClassesLabel.setText("classes (M:1-" + MAX_OBJCLASSES + "/E:1-" + MAX_EXTOBJCLASSES + "):");
	};
	return numObjClassesLabel;
}

/**
 * Creates / Returns the number of object classes text field.
 * @return number of object classes text field
 */
protected TextField getNumObjClassesTextField () {
	if (numObjClassesText == null) {
		numObjClassesText = new TextField("6");
		numObjClassesText.setName("NumObjClassesTextField");
		numObjClassesText.setFont(new Font("Dialog", 0, 11));
	};
	return numObjClassesText;
}

/**
 * Creates / returns the external objects per time label.
 * @return label
 */
protected Label getObjBeginLabel() {
	if (objBeginLabel == null) {
		objBeginLabel = new Label();
		objBeginLabel.setName("ExtObjPerTimeLabel");
		objBeginLabel.setFont(new Font("sansserif", 0, 12));
		objBeginLabel.setText("obj./begin (M:-" + MAX_OBJBEGIN + " E:-" + MAX_EXTOBJBEGIN + "):");
	};
	return objBeginLabel;
}

/**
 * Creates / Returns the moving objects at the beginning text field.
 * @return text field
 */
protected TextField getObjBeginTextField () {
	if (objBeginText == null) {
		objBeginText = new TextField("5");
		objBeginText.setName("ObjBeginTextField");
		objBeginText.setFont(new Font("Dialog", 0, 11));
	};
	return objBeginText;
}

/**
 * Creates / returns the objects per time label.
 * @return objects per time label
 */
protected Label getObjPerTimeLabel() {
	if (objPerTimeLabel == null) {
		objPerTimeLabel = new Label();
		objPerTimeLabel.setName("ObjPerTimeLabel");
		objPerTimeLabel.setFont(new Font("sansserif", 0, 12));
		objPerTimeLabel.setText("obj./time (M:-" + MAX_OBJPERTIME + "/E:-" + MAX_EXTOBJPERTIME + "):");
	};
	return objPerTimeLabel;
}

/**
 * Creates / Returns the objects per time text field.
 * @return objects per time text field
 */
protected TextField getObjPerTimeTextField () {
	if (objPerTimeText == null) {
		objPerTimeText = new TextField("5");
		objPerTimeText.setName("ObjPerTimeTextField");
		objPerTimeText.setFont(new Font("Dialog", 0, 11));
	};
	return objPerTimeText;
}

/**
 * Returns an integer property.
 * @return the integer value
 * @param key name of the key
 * @param defaultValue the default value
 */
protected int getProperty (String key, int defaultValue) {
	return getProperty (properties,key,defaultValue);
}

/**
 * Returns an integer property.
 * @return the integer value
 * @param properties the properties
 * @param key name of the key
 * @param defaultValue the default value
 */
public static int getProperty (Properties properties, String key, int defaultValue) {
	try {
		return new Integer(properties.getProperty(key)).intValue();
	}
	catch (Exception ex) {
		return defaultValue;
	}
}

/**
 * Creates / returns the report probabilty label.
 * @return label
 */
protected Label getReportProbLabel() {
	if (reportProbLabel == null) {
		reportProbLabel = new Label();
		reportProbLabel.setName("ReportProbLabel");
		reportProbLabel.setFont(new Font("sansserif", 0, 12));
		reportProbLabel.setText("report probability (0-1000):");
	};
	return reportProbLabel;
}

/**
 * Creates / Returns the report probabilty text field.
 * @return text field
 */
protected TextField getReportProbTextField () {
	if (reportProbText == null) {
		reportProbText = new TextField("1000");
		reportProbText.setName("ReportProbTextField");
		reportProbText.setFont(new Font("Dialog", 0, 11));
	};
	return reportProbText;
}

/**
 * Creates / Returns the time scrollbar.
 * @return East-Button
 */
protected Scrollbar getTimeScrollbar() {
	if (timeScrollbar == null) {
		timeScrollbar = new Scrollbar(Scrollbar.HORIZONTAL, 0, 32, 0, viewWidth-32);
		timeScrollbar.setName("TimeScrollbar");
	};
	return timeScrollbar;
}

/**
 * Computes the value of the text field and adapt it.
 * @return integer value of text field
 * @param tf the text field
 * @param min minimum allowed value
 * @param max maximum allowed value
 * @param enabledAfter should the text filed be enabled after computing the value?
 */
protected int getValueOfTextField (TextField tf, int min, int max, boolean enabledAfter) {
	int intValue = new Integer(tf.getText()).intValue();
	if (intValue < min)
		intValue = min;
	else if (intValue > max)
		intValue = max;
	tf.setText(String.valueOf(intValue));
	tf.setEditable(enabledAfter);
	tf.setEnabled(enabledAfter);
	return intValue;
}

/**
 * Initializes the data generator.
 * Initializes the class EdgeClasses.
 */
public void init() {
	String p = getParameter("propertyfile");
	if (p != null)
		propFilename = p;
	// read properties
	try {
		URL url = computeURL(propFilename);
		InputStream in = url.openStream();
		properties.load(in);
	}
	catch (MalformedURLException mex) {
		System.err.println("DataGenerator.init: "+mex);
		System.exit(PROPERTY_FILE_ERROR);
	}
	catch (IOException ioex) {
		System.err.println("DataGenerator.init: "+ioex);
		System.exit(PROPERTY_FILE_ERROR);
	}
	// init drawable objects	
	edgeClasses = createEdgeClasses (properties);
	if (drawableObjects == null)
		drawableObjects = new DrawableObjectsWithSearchTree(numOfLayers,new MemoryRTree());
	super.init();
}

/**
 * Inits the presentation of the network.
 */
protected void initDrawablePresentation () {
	super.initDrawablePresentation();
	int num = edgeClasses.getNumber();
	// Minimum scales for depicting edges and nodes
	int[] nodeMinScale = new int[num+1];
	for (int i=0; i<num+1; i++)
		nodeMinScale[i] = 0;
	int[] edgeMinScale = new int[num];
	for (int i=0; i<num; i++)
		edgeMinScale[i] = edgeClasses.getMinScale(i);
	// Colors of edges and nodes
	Color[] nodeColor = new Color[num+1];
	for (int i=0; i<num+1; i++)
		nodeColor[i] = Color.gray;
	Color[] edgeColor = new Color[num];
	for (int i=0; i<num; i++)
		edgeColor[i] = edgeClasses.getColor(i);
	// Setting
	net.getEdges().setNumOfClasses (num);
	net.getEdges().initPresentation (edgeColor,Color.red);
	net.getEdges().setMinScaleArray (edgeMinScale);
	net.getNodes().setNumOfClasses (num+1);
	net.getNodes().initPresentation (nodeColor,Color.red);
	net.getNodes().setMinScaleArray (nodeMinScale);
}

/**
 * Evaluates the properties of the property file.
 */
protected void interpretParameters () {
	// evaluate generator properties
	MIN_MAXTIME = getProperty("MIN_MAXTIME",MIN_MAXTIME);
	MAX_MAXTIME = getProperty("MAX_MAXTIME",MAX_MAXTIME);
	MAX_OBJCLASSES = getProperty("MAX_OBJCLASSES",MAX_OBJCLASSES);
	MAX_OBJPERTIME = getProperty("MAX_OBJPERTIME",MAX_OBJPERTIME);
	MAX_OBJBEGIN = getProperty("MAX_OBJBEGIN",MAX_OBJBEGIN);
	MAX_EXTOBJCLASSES = getProperty("MAX_EXTOBJCLASSES",MAX_EXTOBJCLASSES);
	MAX_EXTOBJPERTIME = getProperty("MAX_EXTOBJPERTIME",MAX_EXTOBJPERTIME);
	MAX_EXTOBJBEGIN = getProperty("MAX_EXTOBJBEGIN",MAX_EXTOBJBEGIN);

	waitingPeriod = getProperty("waitingPeriod",waitingPeriod);
	// evaluate showmap parameters
	baseScaleFactor = getProperty("baseScaleFactor",1);
	minScale = getProperty("minScale",minScale);
	maxScale = getProperty("maxScale",maxScale);
	scale = getProperty("scale",maxScale);
	viewWidth = getProperty("viewWidth",viewWidth);
	viewHeight = getProperty("viewHeight",viewHeight);
	mapWidth = getProperty("mapWidth",scale*viewWidth);
	mapHeight = getProperty("mapHeight",scale*viewHeight);
	viewX = getProperty("viewX",viewX);
	viewY = getProperty("viewY",viewY);
	String p = properties.getProperty("color");
	if (p != null)
		backgroundColor = ColorDefiner.getColor(p);
	p = properties.getProperty("mapColor");
	if (p != null)
		mapColor = ColorDefiner.getColor(p);
	p = properties.getProperty("language");
	if ((p != null) && p.equals("D"))
		language = GERMAN;
}

/**
 * Makes the file name to an absolute file name
 * @param  fileName  the name of the file
 */
protected static String makeAbsolute (String fileName) {
	try {
		return new File(fileName).getAbsolutePath();
	} catch (Exception ex) {
		return fileName;
	}
}

/**
 * Reports the progress of the generation.
 * @param time actual time
 */
protected void reportProgress (int time) {
	setTimeScrollbar (time);
}

/**
 * Sets the status of the applet. If the status is COMPLETE, the compute button
 * will be enabled. If the maximum time text field has been changed, the computation
 * will automatically be started.
 * @param state the new state
 */
protected void setState (int state) {
	super.setState(state);
	if (state != COMPLETE)
		return;
	Rectangle r = drawableObjects.getDataspace();
	//System.out.println("complete: "+r);
	this.movePos(r.x+r.width/2,r.y+r.height,scale);
	repaint();
	// Automatic computation
	if (getValueOfTextField(getMaxTimeTextField(),MIN_MAXTIME,MAX_MAXTIME,true) != 20)
		compute();
	// Compute button
	getComputeButton().setEnabled(true);
	getFloorScrollBar().setMaximum(Utility.getMaxNumOfFloors());
	getFloorScrollBar().setValue(getFloorScrollBar().getMaximum());
	getMaxNumOfFloorsTextField().setText(Integer.toString(Utility.getMaxNumOfFloors()));
}

/**
 * Sets the actual time for displaying purposes.
 * @param actTime int
 */
protected void setTime (int actTime) {
	drawableObjects.removeAllObjectsOfLayer(Drawable.OBJECTLAYER);
	// reset old state
	if (this.actTime != 0) {
		//for (int c=0; c<objClasses.getNumber(); c++)
		//	DrawablePresentation.get("Point"+c+"-"+this.actTime).setVisibility(false);
		for (int c=0; c<extObjClasses.getNumber(); c++)
			DrawablePresentation.get("Rectangle"+c+"-"+this.actTime).setVisibility(false);
	}
	else
		for (int t=0; t<=time.getMaxTime(); t++) {
		//	for (int c=0; c<objClasses.getNumber(); c++)
		//		DrawablePresentation.get("Point"+c+"-"+t).setVisibility(false);
			for (int c=0; c<extObjClasses.getNumber(); c++)
				DrawablePresentation.get("Rectangle"+c+"-"+t).setVisibility(false);
		}
	// set new state
	if (actTime != 0) {
		for (MovingObject mo : listOfObjects){
			//for (String s : mo.getRouteList()){

			if (mo.getRouteList().containsKey(Integer.toString(actTime))){
				String s = mo.getRouteList().get(Integer.toString(actTime));

					if (s.contains("Building template:")){
						String[] tmp = s.split(":");
						Scanner sc = new Scanner(tmp[5]);
						int floor = sc.nextInt();
						sc = new Scanner(tmp[6]);
						int time = sc.nextInt();
						sc = new Scanner(tmp[7]);
						sc.useLocale(Locale.US);
						int x = (int)sc.nextDouble();
						sc = new Scanner(tmp[8]);
						sc.useLocale(Locale.US);
						int y = (int)sc.nextDouble();

						if (floor == Utility.getActFloorVisual() && time == actTime){
							DrawableSymbol symbol = new DrawableSymbol (x,y,"Point"+mo.getObjectClass()+"-"+time);
							symbol.setLayer(Drawable.OBJECTLAYER);
							symbol.getPresentation().setVisibility(true);
							drawableObjects.addDrawable(symbol);
						}
					}
					else if (!s.contains("Building template:")) {
						String[] tmp = s.split(":");
						int j = 0;
						if (s.contains("Floor: ")){
							j++;
						}

						Scanner sc = new Scanner(tmp[j+1]);
						sc.useLocale(Locale.US);
						int time = sc.nextInt();
						sc = new Scanner(tmp[j+2]);
						sc.useLocale(Locale.US);
						int x = (int)sc.nextDouble();
						sc = new Scanner(tmp[j+3]);
						sc.useLocale(Locale.US);
						int y = (int)sc.nextDouble();
						if (time == actTime){
							DrawableSymbol symbol = new DrawableSymbol (x,y,"Point"+mo.getObjectClass()+"-"+time);
							symbol.setLayer(Drawable.OBJECTLAYER);
							symbol.getPresentation().setVisibility(true);
							drawableObjects.addDrawable(symbol);
						}
					}
			}
			//}
		}

		/*for (int c=0; c<objClasses.getNumber(); c++) {
			DrawablePresentation.get("Point" + c + "-" + actTime).setVisibility(true);
		}*/

		/*
		for (int c=0; c<objClasses.getNumber(); c++) {
			if (DrawablePresentation.get("Point" + c + "-" + actTime).inside) {
				if (DrawablePresentation.get("Point" + c + "-" + actTime).floor == Utility.getActFloorVisual()) {
					DrawablePresentation.get("Point" + c + "-" + actTime).setVisibility(true);
				}
			} else {
				DrawablePresentation.get("Point" + c + "-" + actTime).setVisibility(true);
			}
		}*/


		for (int c=0; c<extObjClasses.getNumber(); c++)
			DrawablePresentation.get("Rectangle"+c+"-"+actTime).setVisibility(true);
	}
	else {
		Utility.numberOfReportedPositions = 0;
		for (int t = 0; t <= time.getMaxTime(); t++) {
			/*for (int c=0; c<objClasses.getNumber(); c++)
				DrawablePresentation.get("Point"+c+"-"+t).setVisibility(true);*/
			for (int c = 0; c < extObjClasses.getNumber(); c++)
				DrawablePresentation.get("Rectangle" + c + "-" + t).setVisibility(true);
		}
		for (MovingObject mo : listOfObjects){
			//for (String s : mo.getRouteList()){

			for (Map.Entry<String,String> ss : mo.getRouteList().entrySet()){
				Utility.numberOfReportedPositions++;
				String s = ss.getValue();

				if (s.contains("Building template:")){
					String[] tmp = s.split(":");
					Scanner sc = new Scanner(tmp[5]);
					int floor = sc.nextInt();
					sc = new Scanner(tmp[6]);
					int time = sc.nextInt();
					sc = new Scanner(tmp[7]);
					sc.useLocale(Locale.US);
					int x = (int)sc.nextDouble();
					sc = new Scanner(tmp[8]);
					sc.useLocale(Locale.US);
					int y = (int)sc.nextDouble();

					DrawableSymbol symbol = new DrawableSymbol (x,y,"Point"+mo.getObjectClass()+"-"+time);
					symbol.setLayer(Drawable.OBJECTLAYER);
					symbol.getPresentation().setVisibility(true);
					drawableObjects.addDrawable(symbol);
				}
				else {
					String[] tmp = s.split(":");
					int j = 0;
					if (s.contains("Floor: ")){
						j++;
					}

					Scanner sc = new Scanner(tmp[j+1]);
					sc.useLocale(Locale.US);
					int time = sc.nextInt();
					sc = new Scanner(tmp[j+2]);
					sc.useLocale(Locale.US);
					int x = (int)sc.nextDouble();
					sc = new Scanner(tmp[j+3]);
					sc.useLocale(Locale.US);
					int y = (int)sc.nextDouble();
					DrawableSymbol symbol = new DrawableSymbol (x,y,"Point"+mo.getObjectClass()+"-"+time);
					symbol.setLayer(Drawable.OBJECTLAYER);
					symbol.getPresentation().setVisibility(true);
					drawableObjects.addDrawable(symbol);
				}
			}
			//}
		}
/*		for (int c=0; c<objClasses.getNumber(); c++) {
			DrawablePresentation.get("Point" + c + "-" + actTime).setVisibility(true);
		}*/
	}
	this.actTime = actTime;
	time.setCurrTime(actTime);
	getNameLabel().setText("Time: " + String.valueOf(actTime));
	repaint();
}

/**
 * Sets the position of the time scrollbar and sets the time text field.
 * @param t time
 */
protected void setTimeScrollbar (int t) {
	int value = t*(timeScrollbar.getMaximum()-timeScrollbar.getVisibleAmount())/time.getMaxTime();
	timeScrollbar.setValue(value);
	getNameLabel().setText("Time: " + String.valueOf(t));
}

/**
 * Shows the status.
 * @param  text  text to be displayed
 */
public void showStatus (String text) {
	super.showStatus(text);
	if (frame != null)
		frame.setTitle("Network Generator: "+text);
}

/**
 * Interprets the properties "urlne" (base name of unzipped network files) or "urlnez" (base name of zipped network files)
 * and starts the loading thread.
 * This method must be overwritten by a superclass, which reads the network from elsewhere.
 */
protected void startLoadingThread () {
	// determine filenames
	URL url[] = {null,null,null};
	String filename = properties.getProperty("urlne");
	if (filename != null) {
		url[1] = computeURL (filename+".node");
		url[2] = computeURL (filename+".edge");
	}
	else {
		filename = properties.getProperty("urlnez");
		if (filename == null) {
			System.err.println("no network file determined");
			System.exit(NETWORKFILE_ERROR);
		}
		url[1] = computeURL (filename+".node.zip");
		url[2] = computeURL (filename+".edge.zip");
	}
	// start thread
	new LoadDrawables (this,url,0).start();
}

/**
 * Sets the viewpoint to the value predefined by the parameters.
 */
public void setViewToPrefinedValue() {
	viewMapX = getProperty("posx",mapWidth/2/scale);
	viewMapY = getProperty("posy",mapHeight/2/scale);
}

	/**
	 * Writes the output for indoortopology for all moving objects
	 */
	public void writeMovingObjectOutput(ArrayList<MovingObject> objList){

		Element trajectories = new Element ("trajectories");

		for (MovingObject m : objList){

			Element trajectory = new Element("trajectory");
			Element objid = new Element("objid");
			objid.appendChild(Integer.toString(m.getId()));
			trajectory.appendChild(objid);
			Element objclass = new Element("objclass");
			objclass.appendChild(Integer.toString(m.getObjectClass()));
			trajectory.appendChild(objclass);

			for (Map.Entry<String,String> ss : m.getRouteList().entrySet()){
					String s = ss.getValue();
				//Element flag = new Element("flag");

				if (s.contains("Building template:")){
					//flag.appendChild("true");

					Element iloc = new Element("iloc");
					Element itime = new Element("time");
					Element ipos = new Element("pos");
					Element buildingID = new Element("buildingID");
					Element floorID = new Element("floorID");
					Element roomID = new Element("roomID");

					//oloc.appendChild(otime);
					//oloc.appendChild(opos);

					iloc.appendChild(itime);
					iloc.appendChild(ipos);
					iloc.appendChild(buildingID);
					iloc.appendChild(floorID);
					iloc.appendChild(roomID);

					String[] tmp = s.split(":");
					Scanner sc = new Scanner(tmp[1]);
					buildingID.appendChild(Integer.toString(sc.nextInt()));
					sc = new Scanner(tmp[4]);
					roomID.appendChild(Integer.toString(sc.nextInt()));
					sc = new Scanner(tmp[5]);
					floorID.appendChild(Integer.toString(sc.nextInt()));
					sc = new Scanner(tmp[6]);
					itime.appendChild(Integer.toString(sc.nextInt()));
					sc = new Scanner(tmp[7]);
					sc.useLocale(Locale.US);
					String tmpPos = Double.toString(sc.nextDouble());
					sc = new Scanner(tmp[8]);
					sc.useLocale(Locale.US);
					tmpPos += " " + Double.toString(sc.nextDouble());
					ipos.appendChild(tmpPos);

					//trajectory.appendChild(flag);
					//trajectory.appendChild(oloc);
					trajectory.appendChild(iloc);
				}
				else {
					Element oloc = new Element("oloc");
					Element otime = new Element("time");
					Element opos = new Element("pos");

					//flag.appendChild("false");

					oloc.appendChild(otime);
					oloc.appendChild(opos);

					//iloc.appendChild(itime);
					//iloc.appendChild(buildingID);
					//iloc.appendChild(floorID);
					//iloc.appendChild(roomID);

					String[] tmp = s.split(":");
					int j = 0;
					if (s.contains("Floor: ")){
						j++;
					}

					Scanner sc = new Scanner(tmp[j+1]);
					sc.useLocale(Locale.US);
					otime.appendChild(Integer.toString(sc.nextInt()));
					sc = new Scanner(tmp[j+2]);
					sc.useLocale(Locale.US);
					String tmpPos = Double.toString(sc.nextDouble());
					sc = new Scanner(tmp[j+3]);
					sc.useLocale(Locale.US);
					tmpPos += " " + Double.toString(sc.nextDouble());
					opos.appendChild(tmpPos);

					//trajectory.appendChild(flag);
					trajectory.appendChild(oloc);
					//trajectory.appendChild(iloc);
					//trajectories.appendChild(trajectory);
				}
			}
			trajectories.appendChild(trajectory);
		}

		Document doc = new Document(trajectories);

		try{
			FileOutputStream fileoutputstream = new FileOutputStream("./output/output.xml");

			Serializer serializer = new Serializer(fileoutputstream, "ISO-8859-1");
			serializer.setIndent(4);
			serializer.setMaxLength(128);
			serializer.write(doc);
		}
		catch (IOException ex){
			System.err.println(ex);
		}
	}

	public void readMovingObjectsOutput(){
		try{
			listOfObjects.clear();
			getReadMovingObjects().setEnabled(false);
			getComputeButton().setEnabled(false);
			getAddTimeButton().setEnabled(true);
			reporter = createReporter(properties, drawableObjects);
			deleteButton.setEnabled(true);

			int maxTime = 0;

			Builder parser = new Builder();
			File f = new File("./output/output.xml");
			Document doc = parser.build(f);
			Element trajectories = doc.getRootElement();
			//listChildren(trajectories, 0);

			//System.out.println(trajectories.getChild(1).getValue());
			//System.out.println(trajectories.getChild(0));
			int count = 0;
			for (int i = 0; i < trajectories.getChildCount(); i++) {

				if (trajectories.getChild(i) instanceof Element){
					int objID = Integer.parseInt(trajectories.getChild(i).getChild(1).getValue());
					int objClass = Integer.parseInt(trajectories.getChild(i).getChild(3).getValue());
					nu.xom.Node trajectory = trajectories.getChild(i);

					MovingObject mo = new MovingObject(objID, objClass);

					for (int j = 5; j < trajectory.getChildCount(); j = j+2) {
						nu.xom.Node position = trajectory.getChild(j);

						int time = Integer.parseInt(position.getChild(1).getValue());
						String location = position.getChild(3).getValue();

						if (time > maxTime){
							maxTime = time;
						}

						Scanner sc = new Scanner(location);
						sc.useLocale(Locale.US);
						int x = (int)sc.nextDouble();
						int y = (int)sc.nextDouble();

						if (position.getChildCount() == 11){
							int floor = Integer.parseInt(position.getChild(7).getValue());
							//reporter.visualizeIndoorMovingObject(x, y, objClass, time, floor);
							Map<String, String>tmpRouteList = mo.getRouteList();
							tmpRouteList.put(Integer.toString(time), "Building template: 0 ButtomX: 0 ButtomY: 0 Room: 0 Floor: " + floor + " Time: " + time + " Last X: " + x + " Last Y:" + y);
							mo.setRouteList(tmpRouteList);
						}
						else {
							//reporter.visualizeMovingObject(x,y,objClass,time);
							Map<String, String>tmpRouteList = mo.getRouteList();
							tmpRouteList.put(Integer.toString(time), "*" + " Time: " + time + " Last X: " + x + " Last Y:" + y);
							mo.setRouteList(tmpRouteList);
						}
					}
					listOfObjects.add(mo);
				}
			}
			time = new Time(properties, maxTime);
			dataspace = new DataSpace(drawableObjects);
			objClasses = createObjectClasses (properties,time,dataspace,getValueOfTextField(getNumObjClassesTextField(),1,MAX_OBJCLASSES,false),
					getValueOfTextField(getReportProbTextField(),0,1000,false),getValueOfTextField(getMsdTextField(),1,1000,true));
			extObjClasses = createExternalObjectClasses (properties,time,dataspace,getValueOfTextField(getNumExtObjClassesTextField(),1,MAX_EXTOBJCLASSES,false));
			if (properties.getProperty(Reporter.VIZ)!=null) {
				for (int c = 0; c < objClasses.getNumber(); c++)
					for (int i = 0; i <= time.getMaxTime(); i++)
						DrawablePresentation.newDrawablePresentation("Point" + c + "-" + i, false, objClasses.getColor(c), Color.red, DrawableSymbol.CIRCLE, 8);
				for (int c = 0; c < extObjClasses.getNumber(); c++)
					for (int i = 0; i <= time.getMaxTime(); i++)
						DrawablePresentation.newDrawablePresentation("Rectangle" + c + "-" + i, false, extObjClasses.getColor(c), Color.red);
			}
			drawableObjects.removeAllObjectsOfLayer(Drawable.OBJECTLAYER);
			setTime(actTime);
			/*for (int i = 0; i < trajectories.getChildCount(); i++) {

				if (trajectories.getChild(i) instanceof Element){
					String objID = trajectories.getChild(i).getChild(1).getValue();
					int objClass = Integer.parseInt(trajectories.getChild(i).getChild(3).getValue());
					nu.xom.Node trajectory = trajectories.getChild(i);

					for (int j = 5; j < trajectory.getChildCount(); j = j+2) {
						nu.xom.Node position = trajectory.getChild(j);

						int time = Integer.parseInt(position.getChild(1).getValue());
						String location = position.getChild(3).getValue();

						if (time > maxTime){
							maxTime = time;
						}

						Scanner sc = new Scanner(location);
						sc.useLocale(Locale.US);
						int x = (int)sc.nextDouble();
						int y = (int)sc.nextDouble();

						if (position.getChildCount() == 11){
							int floor = Integer.parseInt(position.getChild(7).getValue());
							reporter.visualizeIndoorMovingObject(x,y,objClass,time,floor);
						}
						else {
							reporter.visualizeMovingObject(x,y,objClass,time);
						}
					}
				}
			}*/
			getMaxTimeTextField().setEnabled(false);
			getMaxTimeTextField().setText(Integer.toString(maxTime));
		}
		catch (ParsingException ex){
			System.err.println("ParsingException. Something went wrong reading the output file");
		}
		catch (IOException ex){
			System.err.println("IOException. Something went wrong reading the output file");
		}
		repaint();
	}

	private void bfs(Node s, int mark) {
		ArrayList<Node> q = new ArrayList<>();

		s.setMark(mark);
		q.add(s);

		while (!q.isEmpty()) {
			Node v = q.get(0);
			q.remove(v);
			for (Node w : v.getNeighborNodes()) {
				if (w.getMark() != mark) {
					w.setMark(mark);
					q.add(w);
				}
			}
		}
	}
}