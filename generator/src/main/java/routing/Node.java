package routing;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import drawables.*;
import util.*;

public class Node extends Drawable {
    /**
     * x-coordinate.
     */
    public int x = 0;
    /**
     * y-coordinate.
     */
    public int y = 0;
    /**
     * The identifier.
     */
    private long id = 0;
    /**
     * A shorter identifier used exclusively with the face finding method, if the original IDs are incompatable with the
     * face finding method
     */
    private int sId = 0;
    /**
     * The face the node belongs to
     */
    private int face = 0;
    /**
     * The flag of the node, 0 is outdoor, 1 is indoor, 2 is underground,
     * TODO: These might not be important. 3 is potential door or staircase going down and 4 is potential staircase going up,
     * 5 is for visualization of the maximum rectangle and rooms. 6 is for room nodes. 7 is left-most door node. 8 is right-most door node
    */
    private short flag = 0;
    /**
     * The floor of the node, is used for nodes inside buildings
     */
    private short floor = 0;
    /**
     * Class of the node.
     */
    private short nodeClass = 0;
    /**
     * Number of edges.
     */
    private byte numOfEdges = 0;
    /**
     * Index of the current edge.
     */
    private byte actEdge = 0;
    /**
     * Name of the node (may be null).
     */
    private String name = null;
    //Room number if the node is part of a room
    private int roomNumber = 0;

    /**
     * The current mark.
     */
    private int mark = 0;
    /**
     * The outgoing edges of the node.
     */
    private Edge[] edge = new Edge[3];
    /**
     * The container, the node belongs to.
     */
    private Nodes nodes = null;
    /**
     * distances of the path 1 and 2
     */
    private double distanceOfWay[] = {0.0,0.0};
    /**
     * marks of edges using path 1 and 2
     */
    private byte wayEdge[] = {-1,-1};
    /**
     * Node used for pathfinding with AStar algorithm
     */
    //TODO SE LIGE HER
    public boolean isRotated = false;
    public double angle = 0;
    /**
     * Positions in a heap depending of the path (1 and 2)
     */
    protected short heapPos[] = {0,0};

    public void setFloor(short floor) {
        this.floor = floor;
    }

    public short getFloor() {
        return floor;
    }

    public short getFlag() {
        return flag;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public Node(int x, int y, long id, short flag) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.flag = flag;
    }

    public Node(int x, int y, long id, short flag, short floor) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.flag = flag;
        this.floor = floor;
    }

    /**
     * Constructor.
     * @param  id  id of the node
     * @param  x  x-coordinate
     * @param  y  y-coordinate
     */
    public Node (long id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        layer = POINTLAYER;
        pres = DrawablePresentation.get("Node0");
    }

    /**
     * Constructor.
     * @param  id  id of the node
     * @param  x  x-coordinate
     * @param  y  y-coordinate
     * @param  name  name
     * @param  nodes  container
     */
    public Node (long id, int x, int y, String name, Nodes nodes) {
        this.id = id;
        this.x = x;
        this.y = y;
        layer = POINTLAYER;
        pres = DrawablePresentation.get("Node"+(nodes.getNumOfClasses()-1));
        minScale = nodes.minScale[nodes.getNumOfClasses()-1];
        maxScale = 0;
        this.name = name;
        this.nodes = nodes;
        nodeClass = (short)(nodes.getNumOfClasses()-1);
    }

    /**
     * Constructor.
     * @param  id  id of the node
     * @param  x  x-coordinate
     * @param  y  y-coordinate
     * @param  nodes  container
     */
    public Node (long id, int x, int y, Nodes nodes) {
        this (id,x,y,null,nodes);
    }

    /**
     * Passt die Knotenklasse an die neue Kantenklasse an.
     * @param newEdgeClass neue Kantenklasse
     */
    protected void adaptClass (int newEdgeClass) {
        // Fall 1: Kante von wichtigerer Klasse => Knotenklasse anpassen
        if (newEdgeClass+1 < nodeClass) {
            nodeClass = (short)(newEdgeClass+1);
            setMinScale (nodes.minScale[nodeClass]);
            setPresentation (DrawablePresentation.get("Node"+nodeClass));
		/*if (getNext() != null) {
			getNext().setMinScale (nodes.minTextScale[nodeClass]);
			getNext().setPresentation (DrawablePresentation.get("NodeText"+nodeClass));
		}*/
        }
        // Fall 2: Kante von gleicher Klasse und viele anliegende Kanten => ggf. Knotenklasse anpassen
        else if ((newEdgeClass+1 == nodeClass) && (numOfEdges > 2)) {
            int n = 0;
            for (int i=0; i<numOfEdges; i++)
                if (edge[i].getEdgeClass()+1 == nodeClass)
                    n++;
            if (n > 2) {
                nodeClass--;
                setMinScale (nodes.minScale[nodeClass]);
                setPresentation (DrawablePresentation.get("Node"+nodeClass));
			/*if (getNext() != null) {
				getNext().setMinScale (nodes.minTextScale[nodeClass]);
				getNext().setPresentation (DrawablePresentation.get("NodeText"+nodeClass));
			}*/
            }
        }
    }

    /**
     * Adds a new edge to a nodes list of edges.
     * Note: Might have to look more like the original, might not be necessary though
     */
    public void addEdge (Edge newEdge) {
        if (numOfEdges >= edge.length) {
            Edge[] newEdges = new Edge[2*edge.length];
            for (int i=0; i<edge.length; i++)
                newEdges[i] = edge[i];
            edge = newEdges;
        }
        edge[numOfEdges++] = newEdge;
    }

    /**
     * L�scht die angegebene Markierung vom Knoten.
     * @param value Wert der Markierung
     */
    public void clearMark (int value) {
        if (isMarked (value))
            mark = mark-value;
    }
    /**
     * L�scht alle Wege, die vom Knoten ausgehen.
     */
    public void clearWays () {
        wayEdge[0] = -1;
        wayEdge[1] = -1;
    }

    public void debugPrint (int way) {
        System.out.print(name+ " - "+ getDistanceOfWay(way));
    }
/**
 * Berechnet den Abstand des Knotens zu dem angegebenen Knoten.
 * @return Abstand
 * @param node Vergleichs-Knoten
 */

    /**
     * Draws the node if it is visible according to the given scale.
     * @param  g  graphic context
     * @param  scale  current scale
     * @param  mode  draw mode
     * @param  pvalue  value
     */
    protected void drawProtected (Graphics g, int scale, int mode, int pvalue) {
        DrawablePresentation ap = pres.get(scale,mode,pvalue);
        int size = ap.getSize();
        try {
            size = size*obj.getDataValue(0);
        }
        catch (Exception ex) {
        }
        // Symbol-F�llung zeichnen
        if (selected)
            g.setColor (ap.getSelectionFillColor());
        else
            g.setColor (ap.getFillColor());
        g.fillOval (x/scale-size/2,y/scale-size/2,size,size);
        // Symbol-Umri� zeichnen
        if (selected)
            g.setColor (ap.getSelectionColor());
        else
            g.setColor (ap.getColor());
        g.drawOval (x/scale-size/2,y/scale-size/2,size,size);
    }

    /**
     * Vergleich von zwei Knoten auf Gleichheit �ber die ID.
     * @return Knoten gleich?
     * @param node zu vergleichender Knoten
     */
    public boolean equals (Object node) {
        if (node == null)
            return false;
        return (id == ((Node)node).id);
    }
    /**
     * Gibt die Distanz bei dem Knoten bez�glich des angegebenen Weges zur�ck.
     * @return Distanz
     */
    public double getDistanceOfWay (int way) {
        return distanceOfWay[way-1];
    }

    /**
     * Returns the first edge of the node
     */
    public Edge getFirstEdge () {
        if (numOfEdges == 0)
            return null;
        actEdge = 0;
        return edge[actEdge++];
    }

    public Node() {}

    public Edge getNextEdge () {
        if (actEdge >= numOfEdges)
            return null;
        return edge[actEdge++];
    }

    public Edge[] getEdge() { return edge; }

    @Override
    public String toString() {
        return "Node{" +
                "x=" + x +
                ", y=" + y +
                ", id=" + id +
                ", flag=" + flag +
                ", floor=" + floor +
                ", roomNumber=" + roomNumber +
                '}';
    }

    public boolean compareCoordinates(Point p){
        if (p.x == x && p.y == y){
            return true;
        }
        else
            return false;
    }

    public double distanceTo (Node node) {
        return computeDistanceTo (node.getX(),node.getY());
    }

    public double computeDistanceTo (int x, int y) {
        return computeDistance (this.x,this.y, x,y);
    }

    //TODO SHOULD EXTEND DRAWABLE INSTEAD OF THIS
    public static double computeDistance (int x1, int y1, int x2, int y2) {
        long xDist = Math.abs(x1-x2);
        long yDist = Math.abs(y1-y2);
        return Math.sqrt(xDist*xDist + yDist*yDist);
    }


    public Point toPoint (){
        return new Point(this.x, this.y);
    }
    /**
     * Returns the minimum bounding rectangle of the primitive.
     * @return  the MBR
     */
    public Rectangle getMBR () {
        return new Rectangle (x,y,0,0);
    }
    /**
     * Returns the name of the node.
     * @return Name
     */
    public String getName () {
        if (name != null)
            return name;
        else
            return "";
    }
    /**
     * Gibt Knotenklasse zur�ck.
     * @return Knotenklasse
     */
    public int getNodeClass () {
        return nodeClass;
    }
    /**
     * Returns the container 'Nodes'.
     * @return the container
     */
    public Nodes getNodeContainer () {
        return nodes;
    }

    /**
     * Gibt die Kante zur�ck, �ber die der angefragte Weg verl�uft.
     * @return  Kante, �ber die der Weg verl�uft
     * @param  way  Index des Weges
     */
    public Edge getWayEdge (int way) {
        if (wayEdge[way-1] < 0)
            return null;
        else
            return edge[wayEdge[way-1]];
    }
    /**
     * Gibt Hashcode f�r den Knoten zur�ck.
     * @return Hashcode
     */
    public int hashCode () {
        return (int) id;
    }


    /**
     * F�rbt den Knoten in der Hervorhebungsfarbe ein.
     * @param presName Darstellungsname
     */
    public void highlight (String presName) {
        setPresentation(DrawablePresentation.get(presName));
    }

    /**
     * Testet, ob das Symbol durch den �bergebenen Punkt ausgew�hlt wird.
     * @return ausgew�hlt?
     * @param px x-Koordinate in Basis-Koordinaten
     * @param py y-Koordinate in Basis-Koordinaten
     * @param scale akt. Ma�stab
     */
    public boolean interacts (int px, int py, int scale) {
        int actMode = (container==null? DrawableObjects.STDMODE:container.getMode());
        int pvalue = (obj==null? DrawablePresentation.NOVALUE:obj.getPresValue());
        DrawablePresentation ap = pres.get(scale,actMode,pvalue);
        int size = Math.abs(ap.getSize()*pvalue) / 10;
        if (size < 4)
            size = 4;
        return (px >= x-size/2*scale) && (px <= x+size/2*scale) &&
                (py >= y-size/2*scale-1) && (py <= y+size/2*scale);
    }

    /**
     * Gibt zur�ck, ob der Knoten mit dem angegebenen Wert markiert ist.
     * @return markiert?
     * @param value Markierungswert
     */
    public boolean isMarked (int value) {
        if (mark > nodes.nullMark)
            return ((mark-nodes.nullMark) & value) > 0;
        else
            return false;
    }
    /**
     * Markiert den Knoten mit dem angegebenen Wert.
     * @param value Markierunsgwert
     */
    public void mark (int value) {
        if (mark > nodes.nullMark)
            mark = nodes.nullMark + ((mark-nodes.nullMark) | value);
        else
            mark = nodes.nullMark + value;
        if (nodes.maxMark < mark)
            nodes.maxMark = mark;
    }
    /**
     * Moves the node to a new position.
     * @param x new x-coordinate
     * @param y new y-coordinate
     */
    public void moveTo (int x, int y) {
        this.x = x;
        this.y = y;
        for (int i=0; i<numOfEdges; i++)
            edge[i].announceMove(this);
    }

    /**
     * Not implemented.
     */
    public EntryReadable read (EntryInput r) {
        throw new UnsupportedOperationException("Node.read is not implemented!");
    }

    /**
     * Entfernt Kante vom Knoten.
     * Die Knotenklassen werden z.Zt. nicht angepasst.
     * @param oldEdge zu entfernende Kante
     */
    public void removeEdge (Edge oldEdge) {
        // Kante suchen
        int edgeIndex = 0;
        while ((edgeIndex < numOfEdges) && (edge[edgeIndex] != oldEdge))
            edgeIndex++;
        if (edgeIndex == numOfEdges)
            return;
        // und entfernen
        edge[edgeIndex] = edge[numOfEdges-1];
        numOfEdges--;
        // Die Knotenklassen werden z.Zt. nicht angepasst !!!
    }
    /**
     * Replaces the actual node by the parameter node.
     * @param node replacing node
     */
    public void replaceBy (Node node) {
        for (int i=numOfEdges-1; i>=0; i--)
            edge[i].replaceNode (this,node);
        if (numOfEdges != 0)
            System.err.println("Node.replaceBy: numOfEdges != 0");
    }

    /**
     * Setzt die Distanz bez�glich des angegebenen Weges.
     * @param way Index des Weges
     * @param distance Distanz
     */
    public void setDistanceOfWay (int way, double distance) {
        distanceOfWay[way-1] = distance;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public int getNumOfEdges () {
        return numOfEdges;
    }
    public int getFace (){
        return face;
    }
    public long getID(){ return id;}

    public int getsId() { return sId; }

    public void setsId(int sId) { this.sId = sId; }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Sets the ID of the node.
     * @param newID the new ID
     */
    public void setID (long newID) {
        id = newID;
    }
    /**
     * Sets the name of the node.
     * @param name new name
     */
    public void setName (String name) {
        this.name = name;
	/*if (next != null)
		((DrawableText)next).setText(name);
	else
		next = new DrawableText (x,y, name, "NodeText"+nodeClass, DrawableText.LEFT,DrawableText.CENTER, nodes.minTextScale[nodeClass], 0);*/
    }
    /**
     * F�rbt den Knoten in der Standardfarbe ein.
     */
    public void setStandardAppearance () {
        setPresentation(DrawablePresentation.get("Node"+nodeClass));
    }

    /**
     * Merkt sich die angegebene Kante als weiteren Verlauf des angegebenen Weges.
     * @param way Index des Wegs
     * @param e Kante, �ber der der Weg verl�uft
     */
    public void setWay (int way, Edge e) {
        // ggf. alte Wegkanten zur�cksetzen
        if (!isMarked(1))
            wayEdge[0] = -1;
        if (!isMarked(2))
            wayEdge[1] = -1;
        // Kante merken
        for (int i=0; i < numOfEdges; i++)
            if (edge[i] == e)
                wayEdge[way-1] = (byte)i;
    }
    /**
     * Schreibt den Knoten in den DataOutput.
     * @return  erfolgreich?
     * @param  out  DataOutput
     */
    public boolean write (DataOutput out) {
        try {
            byte l = (byte)getName().length();
            out.writeByte(l);
            if (l > 0)
                out.write(getName().getBytes());
            out.writeLong(id);
            out.writeInt(getX());
            out.writeInt(getY());
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    public boolean writeNode (BufferedWriter writer) {
        try {
            byte l = (byte)getName().length();
            writer.write(String.valueOf(l));
            if (l > 0){
                writer.write(String.valueOf(true));
                writer.write(name);
            } else {
                writer.write(String.valueOf(false));
            }
            writer.write(String.valueOf(id) + " ");
            writer.write(String.valueOf(x) + " ");
            writer.write(String.valueOf(y) + " ");
            writer.write(String.valueOf(flag) + " ");
            writer.write(String.valueOf(floor) + " ");
            writer.write(String.valueOf(roomNumber) + " ");
            writer.newLine();
            writer.flush();
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    public void writeSIDToStringSimple(PrintWriter out){
        try{
            out.println(sId + " "  + x + " " + y + " " + name);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Schreibt den Knoten in den EntryWriter.
     * @param out EntryWriter
     */
    public void write (EntryWriter out) {
        out.print(id); out.print('\t'); out.print(getX()); out.print('\t');
        out.print(getY()); out.println('\t'+getName());
    }

    /**
     * Not implemented.
     */
    protected void writeProtected (EntryWriter out, int type) {
        throw new UnsupportedOperationException("Node.writeProtected is not implemented!");
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public ArrayList<Edge> getConnectedEdges(){
        ArrayList<Edge> res = new ArrayList<>();

        Edge edge = getFirstEdge();

        res.add(edge);

        while ((edge = getNextEdge()) != null){
            res.add(edge);
        }
        return res;
    }

    public ArrayList<Node> getNeighborNodes(){
        ArrayList<Node> res = new ArrayList<>();

        Edge edge = getFirstEdge();

        if (edge.getNode1() != this){
            res.add(edge.getNode1());
        }
        if (edge.getNode2() != this){
            res.add(edge.getNode2());
        }

        while ((edge = getNextEdge()) != null){
            if (edge.getNode1() != this && !res.contains(edge.getNode1())){
                res.add(edge.getNode1());
            }
            if (edge.getNode2() != this && !res.contains(edge.getNode2())){
                res.add(edge.getNode2());
            }
        }
        return res;
    }
}
