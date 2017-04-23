package routing;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

import drawables.*;
import util.*;

public class Edge extends Drawable implements Comparable  {

    /**
     * The identifier.
     */
    private long id = 0;
    /**
     * The length of the edge.
     */
    private double length = 0;
    /**
     * Starting node.
     */
    protected Node node1 = null;
    /**
     * End node.
     */
    protected Node node2 = null;
    /**
     * Optional name of the edge.
     */
    private String name = null;
    /**
     * The room that the edge belongs to, is null if it does not belong to any room
     */
    private Room room = null;
    /**
     * The faces the edge belongs to
     */
    private ArrayList<Face> faces = new ArrayList<>();
    /**
     * The flag of the node, 0 is outdoor 1 is indoor and 2 is underground, 5 is for visualizing Rooms, 6 is for room edges
     */
    private short flag = 0;
    /**
     * Usage of the edge.
     */
    private short usage = 0;
    /**
     * The floor of the edge if it is inside a building
     */
    private short floor = 0;
    /**
     * The capacity of the edge, this variable is only used for the inside topology part
     */
    private int capacity = 0;
    /**
     * Current mark.
     */
    private int mark = 0;
    /**
     * The class of the edge.
     */
    private short edgeClass = 0;
    /**
     * Link to the container.
     */
    private Edges edges = null;

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Edge() {
    }

    public short getFloor() {
        return floor;
    }

    public void setFloor(short floor) {
        this.floor = floor;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    /**
     * Constructor.
     * @param  id  ID
     */
    protected Edge (long id) {
        this.id = id;
        pres = DrawablePresentation.get("default");
    }

    public Edge(long id, Node node1, Node node2, String name, short flag, short usage, int mark) {
        this.id = id;
        pres = DrawablePresentation.get("default");
        this.length = node1.distanceTo(node2);
        this.node1 = node1;
        this.node2 = node2;
        this.name = name;
        this.flag = flag;
        this.usage = usage;
        this.mark = mark;

        node1.addEdge(this);
        node2.addEdge(this);
        if (name.contains("dwelling")){
            layer = DWELLINGLAYER;
        }
         if (name.contains("inside") && !name.contains("Stair")){
            layer = INSIDELAYER;
        }
         if (name.contains("walls")){
            layer = BUILDINGLAYER;
        }
        if (name.contains("Stair")){
            pres.setVisibility(false);
            //layer = BUILDINGLAYER;
        }
    }

    public Node getNode1 () {
        return node1;
    }
    /**
     * @return End node
     */
    public Node getNode2 () { return node2; }
    public long getID(){ return id; }

    public short getFlag() { return flag; }

    /**
     * Gibt einen Knoten der Kante zur�ck.
     * @return der Knoten
     */
    public Node getOneNode () {
        return node1;
    }

    /**
     * Returns the name of the edge.
     * @return the name
     */
    public String getName () {
        if (name != null)
            return name;
        else
            return "";
    }

    /**
     * Gibt den Knoten der Kante zur�ck, der nicht dem �bergebenden Knoten entspricht.
     * @return gegen�berliegender Knoten
     * @param oneNode Vergleichsknoten
     */
    public Node getOppositeNode (Node oneNode) {
        if (node1.equals(oneNode))
            return node2;
        else
            return node1;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "id=" + id +
                ", length=" + length +
                ", node1=" + node1 +
                ", node2=" + node2 +
                ", name='" + name + '\'' +
                ", flag=" + flag +
                ", floor=" + floor +
                '}';
    }

    /**
     * Constructor.
     * @param  id  ID
     * @param  edgeClass  class of the edge
     * @param  node1  first node
     * @param  node2  second node
     * @param  name  name of the node (may be null)
     */
    public Edge (long id, int edgeClass, Node node1, Node node2, String name) {
        this.id = id;
        pres = DrawablePresentation.get("default");
        this.edgeClass = (short)edgeClass;
        this.node1 = node1;
        this.node2 = node2;
        this.name = name;
        this.length = node1.distanceTo(node2);

        if (name.contains("dwelling")){
            layer = DWELLINGLAYER;
        }
        if (name.contains("inside")  && !name.contains("Stair")){
            layer = INSIDELAYER;
        }
        if (name.contains("walls")){
            layer = BUILDINGLAYER;
        }
        if (name.contains("Stair")){
            pres.setVisibility(false);
        }
    }

    /**
     * Returns the length of the edge
     * @return Length
     */
    public double getLength() {
        return length;
    }

    public static class FirstNodeXComperator implements Comparator<Edge>
    {
        public  int compare(Edge e1, Edge e2){
            return (e1.getNode1().getX() - e2.getNode1().getX());

        }
    }


    /**
     * Constructor.
     * @param  id  ID
     * @param  edgeClass  class of the edge
     * @param  node1  first node
     * @param  node2  second node
     * @param  name  name of the node (may be null)
     * @param  edges  the container of the edges
     */
    public Edge (long id, int edgeClass, Node node1, Node node2, String name, Edges edges) {
        this.id = id;
        pres = DrawablePresentation.get("Edge"+Num.putIntoInterval(edgeClass,0,edges.getNumOfClasses()));
        this.edgeClass = (short)Num.putIntoInterval (edgeClass,0,edges.getNumOfClasses());
        this.node1 = node1;
        this.node2 = node2;
        this.name = name;
        this.edges = edges;
        this.minScale = edges.minScale[Num.putIntoInterval(edgeClass,0,edges.getNumOfClasses())];
        this.maxScale = 0;
        this.length = node1.distanceTo(node2);

        if (name != null) {
            if (name.contains("dwelling")) {
                layer = DWELLINGLAYER;
            }
            if (name.contains("inside") && !name.contains("Stair")) {
                layer = INSIDELAYER;
            }
            if (name.contains("walls")) {
                layer = BUILDINGLAYER;
            }
            if (name.contains("Stair")) {
                pres.setVisibility(false);
                //layer = BUILDINGLAYER;
            }
        }
    }

    /**
     * Announces to the edge that the node has moved to a new position.
     * @param node one node of the edge
     */
    public void announceMove (Node node) {
        this.length = node1.distanceTo(node2);
    }

    /**
     * Clears the mark.
     */
    public void clearMark () {
        mark = edges.nullMark;
    }

    /**
     * Comparing function concerning the edge names.
     * @return  the result of compareTo on the names
     * @param  edge  the other edge to be compared
     */
    public int compareTo (Object edge) {
        if (edge == null)
            return 1;
        return getName().compareTo(((Edge)edge).getName());
    }

    /**
     * Ausgabe der Kante zu Debugging-Zwecken.
     */
    public void debugPrint () {
        System.out.print(node1.getId()+" - "+node2.getId());
    }
    /**
     * Decrements the usage of the edge.
     */
    public void decUsage() {
        if (usage < 1) System.out.println("Edge "+id+": "+usage+"--");
        usage--;
    }

    /**
     * Draws the edge if it is visible according to the given scale.
     * @param  g  graphic context
     * @param  scale  current scale
     * @param  mode  draw mode
     * @param  value  value
     */
    protected void drawProtected (Graphics g, int scale, int mode, int value) {
        // Darstellung vorbereiten
        DrawablePresentation ap = pres.get(scale,mode,value);
        Color color = ap.getColor();
        if (selected)
            color = ap.getSelectionColor();
        // Koordinaten berechnen
        int cx1 = node1.getX()/scale;
        int cy1 = node1.getY()/scale;
        int cx2 = node2.getX()/scale;
        int cy2 = node2.getY()/scale;
        // Zeichnen
        DrawableLine.drawProtected (g,ap,color,color,cx1,cy1,cx2,cy2);
    }

    /**
     * Returns the minimum bounding rectangle of the primitive.
     * @return  the MBR
     */
    public Rectangle getMBR () {
        int minX = Math.min(node1.getX(),node2.getX());
        int minY = Math.min(node1.getY(),node2.getY());
        int maxX = Math.max(node1.getX(),node2.getX());
        if (maxX == minX)
            maxX++;
        int maxY = Math.max(node1.getY(),node2.getY());
        if (maxY == minY)
            maxY++;
        return new Rectangle (minX,minY,maxX-minX,maxY-minY);
    }

    /**
     * Vergleich von zwei Kanten auf Gleichheit �ber die ID.
     * @return Kanten gleich?
     * @param edge zu vergleichende Kante
     */
    public boolean equals (Object edge) {
        if (edge == null)
            return false;
        return (id == ((Edge)edge).id);
    }

    /**
     * Returns the usage of the edge.
     * @return usage
     */
    public int getUsage() {
        return usage;
    }
    /**
     * Gibt das (gemittelte) Gewicht der Kante (in Hin- und R�ckrichtung) mittels
     * des WeightManagers zur�ck.
     * @return Gewicht der Kante
     */
    public double getWeight() {
        return edges.getWeightManager().getWeight (this);
    }
    /**
     * Gibt das Kantengewicht aus Richtung des �bergebenen Knotens mittels des WeightManagers zur�ck.
     * @return Kantengewicht
     * @param from Ausgangsknoten
     */
    public double getWeight (Node from) {
        return edges.getWeightManager().getWeight(this,node1.equals(from));
    }
    /**
     * Gibt das gerichte Kantengewicht mittels des WeightManagers zur�ck.
     * @return Kantengewicht
     * @param forwards Kante vorw�rts gerichtet?
     */
    public double getWeight (boolean forwards) {
        return edges.getWeightManager().getWeight(this,forwards);
    }
    /**
     * Testet, ob die Kante den �bergebenen Knoten als Start- oder Endknoten besitzt.
     * @return besitzt den Knoten?
     * @param node Vergleichsknoten
     */
    public boolean hasAsNode (Node node) {
        return node1.equals(node) || node2.equals(node);
    }
    /**
     * Gibt Hashcode f�r die Kante zur�ck.
     * @return Hashcode
     */
    public int hashCode () {
        return (int) id;
    }
    /**
     * Increments the usage of the edge.
     */
    public void incUsage() {
	/*if (usage > 2) System.out.println("Edge "+id+": "+usage+"++");*/
        usage++;
    }

    /*
     * Testet, ob die Linie durch den �bergebenen Punkt ausgew�hlt wird.
     * @return ausgew�hlt?
     * @param x x-Koordinate des zu testenden Punktes
     * @param y y-Koordinate des zu testenden Punktes
     * @param scale aktuelle Ma�stab
     */
    public boolean interacts (int x, int y, int scale) {
        int minX = Math.min(node1.getX(),node2.getX());
        int minY = Math.min(node1.getY(),node2.getY());
        int maxX = Math.max(node1.getX(),node2.getX());
        int maxY = Math.max(node1.getY(),node2.getY());
        return DrawableLine.interacts (x,y,node1.getX(),node1.getY(),node2.getX(),node2.getY(),minX,minY,maxX,maxY,scale);
    }

    /**
     * Testet, ob die Kante den �bergebenen Vergleichsknoten als Zielknoten besitzt.
     * @return ist Zielknoten?
     * @param pNode2 Vergleichsknoten
     */
    public boolean isDirectedTo (Node pNode2) {
        return node2.equals(pNode2);
    }
    /**
     * Is the edge marked?
     * @return marked?
     */
    public boolean isMarked () {
        return (mark > edges.nullMark);
    }
    /**
     * Testet, ob die Kante den �bergebenen Vergleichsknoten als Anfangsknoten besitzt.
     * @return ist Anfangsknoten?
     * @param pNode1 Vergleichsknoten
     */
    public boolean isStartingFrom (Node pNode1) {
        return node1.equals(pNode1);
    }

    /**
     * Marks the edges.
     */
    public void mark () {
        mark = edges.nullMark+1;
    }

    /**
     * Not implemented.
     */
    public EntryReadable read (EntryInput r) {
        throw new UnsupportedOperationException("Node.read is not implemented!");
    }

    /**
     * Replaces one node of the edge by another node.
     * @param oldNode old node
     * @param newNode new node
     */
    public void replaceNode (Node oldNode, Node newNode) {
        if (node1 == oldNode) {
            node1.removeEdge (this);
            node1 = newNode;
            node1.addEdge (this);
        }
        else if (node2 == oldNode) {
            node2.removeEdge (this);
            node2 = newNode;
            node2.addEdge (this);
        }
        length = node1.distanceTo(node2);
    }

    public ArrayList<Face> getFaces() {
        return faces;
    }

    public void addFaces(Face f) {
        faces.add(f);
    }

    public void setFaces(ArrayList<Face> faces) {
        this.faces = faces;
    }

    /**
     * Setzt die Klasse der Kante neu.
     * @param edgeClass Kantenklasse
     */
    public void setEdgeClass (short edgeClass) {
        if (edgeClass < this.edgeClass) {
            getNode1().adaptClass(edgeClass);
            getNode2().adaptClass(edgeClass);
        }
        this.edgeClass = edgeClass;
        setPresentation(DrawablePresentation.get("Edge"+edgeClass));
    }

    /**
     * Gibt Kantenklasse zur�ck.
     * @return Kantenklasse
     */
    public int getEdgeClass () {
        return edgeClass;
    }
    /**
     * Returns the container 'Edges'.
     * @return the container
     */
    public Edges getEdgeContainer () {
        return edges;
    }

    /**
     * Set a new ID.
     * @param newID the new ID
     */
    protected void setID (long newID) {
        id = newID;
    }
    /**
     * Sets the name of the edge.
     * @param name new name
     */
    public void setName (String name) {
        this.name = name;
    }
    /**
     * F�rbt die Kante gem�� der Standardfarbe ihrer Klasse ein.
     */
    public void setStandardAppearance () {
        setPresentation(DrawablePresentation.get("Edge"+edgeClass));
    }
    /**
     * Setzt die Benutzung der Kante neu.
     * @param usage Benutzung
     */
    public void setUsage (short usage) {
        this.usage = usage;
    }
    /**
     * Schreibt die Kante in den DataOutput.
     * @return erfolgreich?
     * @param out DataOutput
     */
    public boolean write (DataOutput out) {
        try {
            out.writeLong(node1.getID());
            out.writeLong(node2.getID());
            byte l = (byte)getName().length();
            out.writeByte(l);
            if (l > 0)
                out.write(getName().getBytes());
            out.writeLong(id);
            out.writeInt(edgeClass);
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    public boolean writeEdge (BufferedWriter writer) {
        try {
            byte l = (byte)getName().length();
            writer.write(String.valueOf(l));
            if (l > 0){
                writer.write(String.valueOf(true));
                writer.write(name);
            } else {
                writer.write(String.valueOf(false));
            }
            writer.write(String.valueOf(node1.getID()) + " ");
            writer.write(String.valueOf(node2.getID()) + " ");
            writer.write(String.valueOf(id) + " ");
            writer.write(String.valueOf(length) + " ");
            writer.write(String.valueOf(flag) + " ");
            writer.write(String.valueOf(floor) + " ");
            writer.write(String.valueOf(room.getID()) + " ");
            writer.newLine();
            writer.flush();
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    public void writeSIDToStringSimple(PrintWriter out){
        try {
            //out.println(id + " " + node1.getsId() + " " + node2.getsId() + " " + (int)length + " " + edgeClass + " " + name + " ");
            out.println(node1.getsId() + " " + node2.getsId() + " " + (int)length + " " + edgeClass + " " + name + " ");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Schreibt die Kante in den EntryWriter.
     * @param out EntryWriter
     */
    public void write (EntryWriter out) {
        out.print(id); out.print('\t'); out.print(node1.getID()); out.print('\t');
        out.print(node2.getID()); out.print('\t'); out.print(edgeClass);
        out.println('\t'+name);
    }

    /**
     * Not implemented.
     */
    protected void writeProtected (EntryWriter out, int type) {
        throw new UnsupportedOperationException("Edge.writeProtected is not implemented!");
    }

}
