package generator2;

import java.io.*;
import java.util.*;

import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import routing.*;

/**
 * Container class for all current moving objects.
 *
 * @version 2.00	04.09.01	revision for generator v2.0
 * @version 1.20	11.04.01	object generator added
 * @version 1.11	10.10.00	calling reportEnd
 * @version 1.10	24.04.00	travNodes & travDegree added, use of decreaseUsage changed
 * @version 1.01	01.02.00	reporting adjusted
 * @version 1.00	13.01.00	first version
 * @author FH Oldenburg
 */

public class MovingObjects {

	/**
	 * the moving objects
	 */
	private Vector objs = null;
	/**
	 * the weight manager
	 */
	private WeightManagerForDataGenerator wm = null;
	/**
	 * description of the object classes
	 */
	private ObjectClasses objClasses = null;
	/**
	 * description of the network
	 */
	private Network net = null;
	/**
	 * object generator
	 */
	private ObjectGenerator objGen = null;
	/**
	 * reporter
	 */
	private Reporter reporter = null;
	/**
	 * re-routing decider
	 */
	private ReRoute reroute = null;

	/**
	 * number of moving objects
	 */
	private int num = 0;
	/**
	 * number of created moving objects
	 */
	private int totalNum = 0;
	/**
	 * total number of traversed nodes
	 */
	private int travNodes = 0;
	/**
	 * total degree of traversed nodes
	 */
	private int travDegree = 0;

/**
 * MovingObjects constructor.
 * @param wm the weight manager
 * @param net the network
 * @param objGen the object generator
 * @param reporter the reporter
 * @param reroute reroute decider
 */
public MovingObjects (WeightManagerForDataGenerator wm, Network net, ObjectGenerator objGen, Reporter reporter, ReRoute reroute) {
	this.wm = wm;
	this.objClasses = wm.getObjectClasses();
	this.net = net;
	this.objGen = objGen;
	this.reporter = reporter;
	this.reroute = reroute;
	objs = new Vector (10000,10000);
}
/**
 * Adds a moving object to the container.
 * @param obj moving object
 */
protected void add (MovingObject obj) {
	objs.addElement(obj);
	num++;
	totalNum++;
}
/**
 * Returns the network.
 * @return network
 */
public Network getNetwork() {
	return net;
}
/**
 * Returns the description of the object classes.
 * @return object classes
 */
public ObjectClasses getObjectClasses() {
	return objClasses;
}
/**
 * Returns the rerouting decider.
 * @return reroute
 */
public ReRoute getReRoute () {
	return reroute;
}
/**
 * Returns the total degree of traversed nodes.
 * @return degree of traversed nodes
 */
public int getTotalDegreeOfTraversedNodes() {
	return travDegree;
}
/**
 * Returns the total number of traversed nodes.
 * @return number of traversed nodes
 */
public int getTotalNumberOfTraversedNodes() {
	return travNodes;
}
/**
 * Returns the total number of created moving objects.
 * @return int
 */
public int getTotalNumOfObjects () {
	return totalNum;
}
/**
 * Returns the weight manager.
 * @return weight manager
 */
public WeightManagerForDataGenerator getWeightManager () {
	return wm;
}
/**
 * Increments the counter for the degree of traversed nodes by a given value.
 * @param value the increment
 */
public void incTraversedDegreeBy (int value) {
	travDegree += value;
}
/**
 * Increments the counter for the number of traversed nodes by a given value.
 * @param value the increment
 */
public void incTraversedNodesBy (int value) {
	travNodes += value;
}
/**
 * Moves all objects. The positions during the route of the objects, which have reached
 * the destination node, are reported; these objects are removed from the container.
 * @param time the current time stamp
 */
public void move (int time) {
	for (int i=num-1; i>=0; i--) {
		MovingObject obj = (MovingObject)objs.elementAt(i);
		if (obj.move (time,reporter)) {
			obj.reportEnd(reporter);
			remove(i);
		}
	}
}

	/**
	 * Writes the output for indoortopology for all moving objects
	 */
	/*
public void writeMovingIndoorOutput(){

	Element trajectories = new Element ("trajectories");

	for (int i=num-1; i>=0; i--) {
		MovingObject obj = (MovingObject)objs.elementAt(i);

		Element trajectory = new Element("trajectory");

		Element objid = new Element("objid");
		objid.appendChild(Integer.toString(obj.getId()));
		trajectory.appendChild(objid);

		for (String s : obj.getRouteList()){
			Element flag = new Element("flag");

			Element oloc = new Element("oloc");
			Element otime = new Element("time");
			Element opos = new Element("pos");

			Element iloc = new Element("iloc");
			Element itime = new Element("time");
			Element buildingID = new Element("buildingID");
			Element floorID = new Element("floorID");
			Element roomID = new Element("roomID");

			if (s.contains("Building template:")){
				flag.appendChild("true");

				//oloc.appendChild(otime);
				//oloc.appendChild(opos);

				iloc.appendChild(itime);
				//TODO Append actual time here
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

				trajectory.appendChild(flag);
				//trajectory.appendChild(oloc);
				trajectory.appendChild(iloc);
			}
			else {
				flag.appendChild("false");

				oloc.appendChild(otime);
				oloc.appendChild(opos);

				//iloc.appendChild(itime);
				//iloc.appendChild(buildingID);
				//iloc.appendChild(floorID);
				//iloc.appendChild(roomID);

				//System.out.println(s);

				String[] tmp = s.split(":");
				int j = 0;
				if (s.contains("Floor: ")){ //TODO should not be necessairy
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

				trajectory.appendChild(flag);
				trajectory.appendChild(oloc);
				//trajectory.appendChild(iloc);
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
}*/


/**
 * Removes the moving object at a given index.
 * @param index index of the object
 */
public void remove (int index) {
	MovingObject obj = (MovingObject)objs.elementAt(index);
	if ((objGen != null) && (obj!=null))
		objGen.reachDestination(obj);
	objs.setElementAt(objs.elementAt(num-1),index);
	objs.setElementAt(null,num-1);
	num--;
	objs.setSize(num);

}
/**
 * The positions during the route of all objects in the container are reported.
 * Then, all objects are removed from the container.
 */
public void removeObjects() {
	for (int i=0; i<num; i++) {
		MovingObject obj = (MovingObject)objs.elementAt(i);
		obj.decreaseUsage(obj.getActPathEdge());
		if (obj.getActPathEdge().getEdge().getRoom() != null){ //Decrements room usage if edge is part of a room
			obj.getActPathEdge().getEdge().getRoom().decUsage();
		}
		else {
			obj.getActPathEdge().getEdge().decUsage();
		}
		objs.setElementAt(null,i);
	}
	num = 0;
	objs.setSize(num);
}
}