package src;

import drawables.DrawableObjects;
import generator2.*;
import routing.*;
import util.InputReader;
import util.MaximumRectangle;
import util.OutputWriter;
import util.Utility;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Default controller applet for the computation of network-based spatiotemporal datasets.
 * generator2.PositionReporter is used. 
 * It is also possible to run this class as Java application.
 *
 * @version	2.01	03.09.2003	call of PositionGenerator, makeAbsolute
 * @version	2.00	07.07.2001	separated from the class "DataGenerator"
 * @author FH Oldenburg
 */
public class MainsCombined extends DataGenerator {

	/**
	 * Calls the constructor of EdgeClasses.
	 * @return new created object of the class EdgeClasses
	 * @param properties the properties of the generator
	 */
	public EdgeClasses createEdgeClasses (Properties properties) {
		return new EdgeClasses (properties);
	}

	/**
	 * Calls the constructor of ExternalObjectClasses.
	 * @return an object of ExternalObjectClasses
	 * @param properties properties of the generator
	 * @param time the time object
	 * @param ds the data space
	 * @param numOfClasses number of external object classes
	 */
	public ExternalObjectClasses createExternalObjectClasses (Properties properties, Time time, DataSpace ds, int numOfClasses) {
		return new ExternalObjectClasses (properties,time,ds,numOfClasses);
	}

	/**
	 * Calls the constructor of ExternalObjectGenerator.
	 * @return an external object generator
	 * @param properties properties of the generator
	 * @param time the time object
	 * @param dataspace the dataspace
	 * @param classes the classes of external objects
	 * @param numOfExtObjPerTime number of external objects per time
	 * @param numAtBeginning number of external objects at the beginning
	 */
	public ExternalObjectGenerator createExternalObjectGenerator(Properties properties, Time time, DataSpace dataspace, ExternalObjectClasses classes, int numOfExtObjPerTime, int numAtBeginning) {
		return new ExternalObjectGenerator (properties, time,dataspace, classes, numOfExtObjPerTime,numAtBeginning);
	}

	/**
	 * Calls the constructor of ObjectClasses.
	 * @return an object of ObjectClasses
	 * @param properties properties of the generator
	 * @param time the time object
	 * @param ds the data space
	 * @param numOfClasses number of object classes
	 * @param reportProb report probability (0-1000)
	 * @param maxSpeedDivisor maximum speed divisor
	 */
	public ObjectClasses createObjectClasses(Properties properties, Time time, DataSpace ds, int numOfClasses, int reportProb, int maxSpeedDivisor) {
		return new ObjectClasses (properties, time, ds, numOfClasses, reportProb, maxSpeedDivisor);
	}

	/**
	 * Calls the constructor of ObjectGenerator.
	 * @return an object generator
	 * @param properties properties of the generator
	 * @param time the time object
	 * @param ds the dataspace
	 * @param nodes the nodes of the network
	 * @param objClasses description of the object classes
	 * @param numOfObjPerTime indicator for the number of moving objects per time
	 * @param numOfObjAtBeginning indicator for the number of moving objects at the beginning
	 */
	public ObjectGenerator createObjectGenerator (Properties properties, Time time, DataSpace ds, Nodes nodes, ObjectClasses objClasses, int numOfObjPerTime, int numOfObjAtBeginning) {
		return new ObjectGenerator (properties, time,ds, nodes, objClasses,numOfObjPerTime,numOfObjAtBeginning);
	}

	/**
	 * Calls the constructor of PositionReporter.
	 * @return  the reporter
	 * @param  properties  properties of the generator
	 * @param  objects  container of drawable objects
	 */
	public Reporter createReporter (Properties properties, DrawableObjects objects) {
		return new PositionReporter (properties,objects);
	}

	/**
	 * Calls the constructor of ReRoute.
	 * @return an object of ReRoute
	 * @param properties properties of the generator
	 * @param time the time object
	 * @param ds the data space
	 */
	public ReRoute createReRoute (Properties properties, Time time, DataSpace ds) {
		return new ReRoute (properties,time,ds);
	}

	/**
	 * Generates the indoor topology again
	 */
	public static void generateNewIndoorTopology(boolean rotation){
		BrinkhoffReader.readDataset("./dataset/Originalnetwork");

		Utility.setAllNodes(Utility.removeSingleEdgeNodes(Utility.getAllNodes()));
		Utility.assignsIDsForAllNodes(); //Setting the sIDs
		//Writes the input files for the C++ program
		OutputWriter.WriteSplitFiles(Utility.getAllEdges());

		//Runs the C++ facefinding algorithm
		ProcessBuilder pb = new ProcessBuilder();
		pb.command(".\\boost-facefinding\\facefinding.exe"); //Windows
//		pb.command("./boost-facefinding/facefinding.out"); //Linux
		try {
			Process p = pb.start();
			p.waitFor();
		}
		catch (IOException e){
			e.printStackTrace();

		}catch (InterruptedException q){
			q.printStackTrace();
		}

		System.out.println("Reading faces and removing faces...");
		Utility.setAllFaces(InputReader.ReadFile(Utility.getAllNodes(), "./faces.txt"));

		ArrayList<Face> toBeRemoved = new ArrayList<>();
		ArrayList<Face> allFaces = Utility.getAllFaces();

		int[][] faceMatrix;

		System.out.println("Finding maximum rectangle and placing buildings...");
		for (Face f : Utility.getAllFaces()){

			//Removes all the faces with too large FacePerimeter
			if (f.getFacePerimeter() < Utility.maxFacePerimeter){
//				faceMatrix = f.ToMatrix(f.getNodes());
				if (!f.GenerateRotatedBuildings()){
//				if (faceMatrix == null){
					toBeRemoved.add(f);
				}
				else {
					MaximumRectangle.FindMaximumRectangle(f.ToMatrix(f.getNodes()),f);
				}
			}
			else {
				toBeRemoved.add(f);
			}
		}

		for (Face f : toBeRemoved){
			allFaces.remove(f);
		}

		Utility.setAllFaces(Utility.removeWrongFaces(allFaces));
//		Utility.setAllFaces(Utility.removeWrongFaces(Utility.getAllFaces()));

		System.out.println("Splitting edges...");
		for (Face f : allFaces){
			f.SplitEdge();
		}

		System.out.println("Writing edges and nodes to file...");
		String edgeFile = "./dataset/network.edge";
		String nodeFile = "./dataset/network.node";
		//Writes the new nodes and edges to a file using their own write functions
		try {
			File efile = new File(edgeFile);
			if (!efile.exists()){
				efile.createNewFile();
			}

			File nfile = new File(nodeFile);
			if (!nfile.exists()){
				nfile.createNewFile();
			}

			DataOutputStream edgeout = new DataOutputStream(new FileOutputStream("./dataset/network.edge"));
			for (Edge e : Utility.getAllEdges()){
				e.write(edgeout);
			}
			DataOutputStream nodeout = new DataOutputStream(new FileOutputStream("./dataset/network.node"));
			for (Node n : Utility.getAllNodes()){
				n.write(nodeout);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
//	/**
//	 * Generates indoor topology if faces have already been previously found
//	 */
//	public static void generateIndoorTopology(){
//		BrinkhoffReader.readDataset("./dataset/Originalnetwork");
//
//		Utility.setAllNodes(Utility.removeSingleEdgeNodes(Utility.getAllNodes()));
//
//		System.out.println("Reading faces and removing faces...");
//		Utility.setAllFaces(InputReader.ReadFile(Utility.getAllNodes(), "./faces.txt"));
//
//		System.out.println("Finding maximum rectangle and placing buildings...");
//		for (Face f : Utility.getAllFaces()){
//			MaximumRectangle.FindMaximumRectangle(f.ToMatrix(),f);
//		}
//
//		Utility.setAllFaces(Utility.removeWrongFaces(Utility.getAllFaces())); //TODO: No reason to send all faces when it is now a global variable
//
//		System.out.println("Splitting edges...");
//		for (Face f : Utility.getAllFaces()){
//			f.SplitEdge();
//		}
//
//		System.out.println("Writing edges and nodes to file...");
//		String edgeFile = "./dataset/network.edge";
//		String nodeFile = "./dataset/network.node";
//		//Writes the new nodes and edges to a file using their own write functions
//		try {
//			File efile = new File(edgeFile);
//			if (!efile.exists()){
//				efile.createNewFile();
//			}
//
//			File nfile = new File(nodeFile);
//			if (!nfile.exists()){
//				nfile.createNewFile();
//			}
//
//			DataOutputStream edgeout = new DataOutputStream(new FileOutputStream("./dataset/network.edge"));
//			for (Edge e : Utility.getAllEdges()){
//				e.write(edgeout);
//			}
//			DataOutputStream nodeout = new DataOutputStream(new FileOutputStream("./dataset/network.node"));
//			for (Node n : Utility.getAllNodes()){
//				n.write(nodeout);
//			}
//
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//	}

	/**
	 * Runs the C++ program that finds the faces of the graph
	 */
	private static void FindFaces(){
		//Writes the input files for the C++ program
		OutputWriter.WriteSplitFiles(Utility.getAllEdges());
		//Runs the C++ facefinding algorithm
		ProcessBuilder pb = new ProcessBuilder();
		pb.command(".\\boost-facefinding\\facefinding.exe"); //Windows
//		pb.command("./boost-facefinding/facefinding.out"); //Linux
		try {
			Process p = pb.start();
			p.waitFor();
		}
		catch (IOException e){
			e.printStackTrace();

		}catch (InterruptedException q){
			q.printStackTrace();
		}
	}

	//TODO Test function:
//	public static void RotateFacesTest(){
//		BrinkhoffReader.readDataset("./dataset/Originalnetwork");
//
//		Utility.setAllNodes(Utility.removeSingleEdgeNodes(Utility.getAllNodes()));
//		Utility.assignsIDsForAllNodes(); //Setting the sIDs
//
//		FindFaces();
//
//		System.out.println("Reading faces and removing faces...");
//		Utility.setAllFaces(InputReader.ReadFile(Utility.getAllNodes(), "./faces.txt"));
//
//		ArrayList<Face> allFaces = Utility.getAllFaces();
//
//		double avgX = 0;
//		double avgY = 0;
//
//		for (Node n : Utility.getAllNodes()){
//			avgX += n.x;
//			avgY +=n.y;
//		}
//
//		avgX /= Utility.getAllNodes().size();
//		avgY /= Utility.getAllNodes().size();
//
//		System.out.println("avgX = " + avgX + " avgY = " + avgY);
//		Utility.center = new Point((int) avgX,(int) avgY);
////		for (Node n : Utility.getAllNodes()){
////			System.out.println("BEFORE: (" + n.x + "," + n.y + ")");
////			Utility.NodeRotation(n,Utility.center,Math.toRadians(45));
//////			Utility.NodeRotation(n,Utility.getAllNodes().get(5),Math.toRadians(90));
////			System.out.println("AFTER: (" + n.x + "," + n.y + ")");
////		}
//
//		ArrayList<Face> toBeRemoved = new ArrayList<>();
//
//		System.out.println("Finding maximum rectangle and placing buildings...");
//		for (Face f : Utility.getAllFaces()){
//
//			//Removes all the faces with too large FacePerimeter
//			if (f.getFacePerimeter() < Utility.maxFacePerimeter){
//				if (!f.GenerateRotatedBuildings()){
//					toBeRemoved.add(f);
//				}
//			}
//			else {
//				toBeRemoved.add(f);
//			}
//		}
//
//		for (Face f : toBeRemoved){
//			allFaces.remove(f);
//			f.DestroyFace();
//		}
//
//		System.out.println("Writing edges and nodes to file...");
//		String edgeFile = "./dataset/network.edge";
//		String nodeFile = "./dataset/network.node";
//		//Writes the new nodes and edges to a file using their own write functions
//		try {
//			File efile = new File(edgeFile);
//			if (!efile.exists()){
//				efile.createNewFile();
//			}
//
//			File nfile = new File(nodeFile);
//			if (!nfile.exists()){
//				nfile.createNewFile();
//			}
//
//			DataOutputStream edgeout = new DataOutputStream(new FileOutputStream("./dataset/network.edge"));
//			for (Edge e : Utility.getAllEdges()){
//				e.write(edgeout);
//			}
//			DataOutputStream nodeout = new DataOutputStream(new FileOutputStream("./dataset/network.node"));
//			for (Node n : Utility.getAllNodes()){
//				n.write(nodeout);
//			}
//
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//		System.out.println("Done");
//	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args args[0] = name of the property file
	 */
	public static void main(String[] args) {
		generateNewIndoorTopology(false);

		if ((args.length > 0) && (args[0] != null))
			propFilename = makeAbsolute(args[0]);
		main ("generator2.DefaultDataGenerator");
	}
	
}
