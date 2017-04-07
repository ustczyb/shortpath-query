package src;
/**
 * Created by Ulf on 07/05/15.
 */
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import routing.*;
import util.Utility;

import javax.xml.crypto.Data;

public class BrinkhoffReader {
    public static ArrayList<Node> readNodes(String filename) {
        ArrayList<Node> nodeList = new ArrayList<>();
        try {
            byte[] input;
            FileInputStream fis = new FileInputStream(filename);
            int c;

            while ((c = fis.read()) != -1) {
                String s;
                long id = 0;
                int x = 0;
                int y = 0;


                if (c != 0) {
                    input = new byte[c];
                    for (int i = 0; i < c; i++) {
                        input[i] = (byte) fis.read();
                    }
                    s = new String(input);
                }
                byte[] lba = new byte[8];
                for (int i = 0; i < 8; i++) {
                    lba[i] = (byte) fis.read();
                }
                ByteBuffer lbb = ByteBuffer.wrap(lba);
                id = lbb.getLong();

                byte[] xba = new byte[4];
                for (int i = 0; i < 4; i++) {
                    xba[i] = (byte) fis.read();
                }
                ByteBuffer xbb = ByteBuffer.wrap(xba);
                x = xbb.getInt();

                byte[] yba = new byte[4];
                for (int i = 0; i < 4; i++) {
                    yba[i] = (byte) fis.read();
                }
                ByteBuffer ybb = ByteBuffer.wrap(yba);
                y = ybb.getInt();

                Node node = new Node(x,y,id,(short)0);
                //System.out.println(node.getID() + " " + node.x + " " + node.y);
                nodeList.add(node);
            }
        }
        catch (IOException e)
        {
            System.out.println("Input doesn't match format " + e);
        }
        return nodeList;
    }
    public static ArrayList<Edge> readEdges(String filename, ArrayList<Node> nodes){
        ArrayList<Edge> edgeList = new ArrayList<>();
        try{
            int c;
            FileInputStream fis = new FileInputStream(filename);
            while ((c = fis.read()) != -1)
            {
                long nodeId1 = 0;
                long nodeId2 = 0;
                String name = "";
                long id = 0;
                int edgeClass = 0;

                byte[] lba1 = new byte[8];
                lba1[0] = (byte)c;
                for (int i = 1; i < 8; i++) {
                    lba1[i] = (byte) fis.read();
                }
                ByteBuffer lbb1 = ByteBuffer.wrap(lba1);
                nodeId1 = lbb1.getLong();

                byte[] lba2 = new byte[8];
                lba1[0] = (byte)c;
                for (int i = 0; i < 8; i++) {
                    lba2[i] = (byte) fis.read();
                }
                ByteBuffer lbb2 = ByteBuffer.wrap(lba2);
                nodeId2 = lbb2.getLong();

                byte[] input;

                byte len = (byte)fis.read();
                if (len != 0) {
                    input = new byte[len];
                    for (int i = 0; i < len; i++) {
                        input[i] = (byte) fis.read();
                    }
                    name = new String(input, "UTF8");
                }

                byte[] eba = new byte[8];
                lba1[0] = (byte)c;
                for (int i = 0; i < 8; i++) {
                    eba[i] = (byte) fis.read();
                }
                ByteBuffer ebb = ByteBuffer.wrap(eba);
                id = ebb.getLong();

                byte[] cba = new byte[4];
                for (int i = 0; i < 4; i++) {
                    cba[i] = (byte) fis.read();
                }
                ByteBuffer cbb = ByteBuffer.wrap(cba);
                edgeClass = cbb.getInt();

                Edge edge = new Edge(id,GetNodeFromID(nodes,nodeId1), GetNodeFromID(nodes,nodeId2),name,(short)0,(short)0, 0);
                edgeList.add(edge);
            }
        }
        catch (IOException e)
        {
            System.out.println("Input doesn't match format " + e);
        }
        return edgeList;
    }
    public static Node GetNodeFromID(ArrayList<Node> nodes, long id)
    {
        for (Node node: nodes)
        {
            if(node.getID() == id)
                return node;
        }
        return null;
    }

    public static void readDataset(String filename){
        try {
            DataInputStream nodeIn = new DataInputStream(new FileInputStream(filename+".node"));
            DataInputStream edgeIn = new DataInputStream(new FileInputStream(filename+".edge"));

            ArrayList<Edge> readEdges = new ArrayList<>();
            ArrayList<Node> readNodes = new ArrayList<>();

            //Original Brinkhoff containers used for reading files
            Edges edges = new Edges();
            Nodes nodes = new Nodes(edges);

            System.out.println("read nodes ...");

            Node actNode = null;
            while ((actNode = nodes.read(nodeIn)) != null) {
                    readNodes.add(actNode);
            }

            System.out.println("read edges ...");

            //DrawableObjectType edgeType = DrawableObjectType.getObjectType("Edge");
            Edge actEdge = null;
            int line = 1;
            boolean eof = false;
            while (!eof) {
                try {
                    actEdge = edges.read(edgeIn,nodes);
                    if (actEdge != null) {
                        readEdges.add(actEdge);
                    }
                    else
                        System.err.println("Read error for edge on line "+line);
                    line++;
                }
                catch (IOException ioe) {
                    eof = true;
                }
            }

            Utility.setAllEdges(readEdges);
            Utility.initializeAllNodes(readNodes);
        }
        catch (IOException e){
            System.err.println("Error reading data!");

        }

    }
}
