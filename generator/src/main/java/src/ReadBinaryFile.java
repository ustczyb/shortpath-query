package src;

import routing.Edge;
import routing.Edges;
import routing.Node;
import routing.Nodes;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by zyb on 2017/4/6.
 */
public class ReadBinaryFile {

    public static void main(String[] args) throws IOException {
        DataInputStream nodeIn = new DataInputStream(new FileInputStream("dataset/network.node"));
        Edges edges = new Edges();
        Nodes nodes = new Nodes(edges);
        Node node = null;
        int count = 0;
        while((node = nodes.read(nodeIn)) != null){
            count ++;
            if(node.getFloor() > 0){
                System.out.println(node);
            }
        }
        System.out.println(count);
        count = 0;
//        DataInputStream edgeIn = new DataInputStream(new FileInputStream("dataset/network.edge"));
//        Edge actEdge = null;
//        for(int i = 0; i < 10; i++){
//            actEdge = edges.read(edgeIn,nodes);
//            System.out.println(actEdge);
//        }

//        int line = 1;
//        boolean eof = false;
//        while (!eof) {
//            try {
//                actEdge = edges.read(edgeIn,nodes);
//                if (actEdge != null) {
//                    count++;
//                }
//                else
//                    System.err.println("Read error for edge on line "+line);
//                line++;
//            }
//            catch (IOException ioe) {
//                eof = true;
//            }
//        }
//        System.out.println(count);
    }
}
