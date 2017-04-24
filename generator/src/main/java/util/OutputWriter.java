package util;
/**
 * Created by Ulf on 09/04/15.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import routing.*;

public class OutputWriter
{
    public static void WriteEdgesToFile(ArrayList<Edge> content, String filename) {
        try {
            File file = new File(filename);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (Edge i: content)
            {
                bw.write(i.getNode1().getsId() + " " + i.getNode2().getsId() + "\n");
            }
            bw.close();


            System.out.println("Done with output");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void WriteEdgesToEdgeFile (ArrayList<Edge> edges, String filename) {
        try {

            File file = new File(filename);
            if (!file.exists()){
                file.createNewFile();
            }

            DataOutputStream out = new DataOutputStream(new FileOutputStream(filename));

            for (Edge e : edges){
                out.writeLong(e.getNode1().getID());
                out.writeLong(e.getNode2().getID());
                byte l = (byte)e.getName().length();
                out.writeByte(l);
                if (l > 0) {
                    out.write(e.getName().getBytes());
                }
                out.writeLong(e.getID());
                out.writeInt(e.getEdgeClass());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void WriteNodesToNodesFile (ArrayList<Node> nodes, String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()){
                file.createNewFile();
            }
            DataOutputStream out = new DataOutputStream(new FileOutputStream(filename));

            for (Node n : nodes){
                byte l = (byte)n.getName().length();
                out.writeByte(l);
                if (l > 0){
                    out.write(n.getName().getBytes());
                }
                out.writeLong(n.getID());
                out.writeInt(n.getX());
                out.writeInt(n.getY());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //TODO: Ask Ulf what the meaning of this function is
    private static void WriteNodesToFile(ArrayList<Node> nodes, String filename){
        //private static void WriteNodesToFile(HashMap<Long,Node> nodes, String filename){
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            DataOutputStream out = new DataOutputStream(new FileOutputStream(filename));
            for(Node node : nodes)
            {
                String name = "*";
                byte nameLength = (byte)name.length();
                out.write(nameLength);
                if ( nameLength > 0)
                {
                    out.write(name.getBytes());
                }
                out.writeLong(node.getID());
                out.writeInt(node.getX());
                out.writeInt(node.getY());
            }

        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This function splits the input dataset into smaller files so they can be run with the C++ face finding algorithm
     *Note: This function should be run as the last thing before the C++ program
     */
    public static void WriteSplitFiles(ArrayList<Edge> content) {

        //Sorts the edges based on X value of first node
        Collections.sort(content, new Edge.FirstNodeXComperator() );

        int splits = 0;

        WriteEdgesToFile(content, "boost-facefinding/allEdges.txt");

        File allEdges = new File("boost-facefinding/allEdges.txt");

        if (allEdges.exists()){
            double bytes = allEdges.length();
            double kilobytes = bytes / 1024;
            //The finding face algorithm does not work with edge files over 14 kilobytes
            splits = (int)Math.ceil(kilobytes / 14);
            System.out.println("splits:" + splits);
        }

        ArrayList<ArrayList<Edge>> edges = new ArrayList<>();

        int splitCount = 0; //Counter for knowing how many splits have been done
        int splitLength = content.size() / splits; //Number of edges for each split
        System.out.println("splitLength: " + splitLength);
        for (int i = 0; i < splits ; i++) {
            edges.add(new ArrayList<>());
            if (splitLength * (splitCount+1) < content.size()) {
                for (int j = splitLength * splitCount; j < splitLength * (splitCount+1); j++) {
                    edges.get(edges.size()-1).add(content.get(j));
                }
            }
            else {
                for (int j = splitLength * splitCount; j < content.size(); j++) {
                    edges.get(edges.size()-1).add(content.get(j));
                }
            }
            splitCount++;
        }

        ArrayList<Node> nodes = new ArrayList<>();
        try {
            DataOutputStream edgeout = new DataOutputStream(new FileOutputStream("./dataset/network.edge"));
            DataOutputStream nodeout = new DataOutputStream(new FileOutputStream("./dataset/network.node"));

            splitCount = 0;
            for (ArrayList<Edge> ae : edges){
                WriteEdgesToFile(ae , "./Edges" + splitCount);
                for (Edge e : ae){
                    e.write(edgeout);
                    if (!nodes.contains(e.getNode1())) {
                        nodes.add(e.getNode1());
                        e.getNode1().write(nodeout);
                    }
                    if (!nodes.contains(e.getNode2())) {
                        nodes.add(e.getNode2());
                        e.getNode2().write(nodeout);
                    }
                }
                splitCount++;
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
    private static HashMap<Long,Node> getListNodes(ArrayList<Edge> edges)
    {
        HashMap<Long,Node> nodeList = new HashMap<>();
        for (Edge edge : edges)
        {
            if (nodeList.get(edge.getNode1().getID()) == null){
                nodeList.put(edge.getNode1().getID(),edge.getNode1());
            }
            if (nodeList.get(edge.getNode2().getID()) == null){
                nodeList.put(edge.getNode2().getID(),edge.getNode2());
            }
        }
        return nodeList;
    }
}
