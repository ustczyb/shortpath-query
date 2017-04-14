package edu.ustc.cs.alg.util;


import edu.ustc.cs.alg.model.dto.EdgeAdapter;
import edu.ustc.cs.alg.model.dto.GraphBundle;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import routing.*;
import util.Utility;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;


/**
 * Created by zyb on 2017/4/7.
 */
public class ReadBinaryFileUtil {

    public static GraphBundle File2Graph(String path) throws IOException {
        DataInputStream nodeIn = new DataInputStream(new FileInputStream(path + ".node"));
        Edges edges = new Edges();
        Nodes nodes = new Nodes(edges);
        Node node = null;
        int count = 0;
        //读取顶点
        while((node = nodes.read(nodeIn)) != null){
            count ++;
        }
        System.out.println(count);
        //读取边
        DataInputStream edgeIn = new DataInputStream(new FileInputStream(path + ".edge"));
        Edge actEdge = null;
        int line = 1;
        boolean eof = false;
        while (!eof) {
            try {
                actEdge = edges.read(edgeIn,nodes);
                if (actEdge != null) {
                    count++;
                }
                else
                    System.err.println("Read error for edge on line "+line);
                line++;
            }
            catch (IOException ioe) {
                eof = true;
            }
        }
        AbstractBaseGraph<Node, EdgeAdapter> graph = new DefaultDirectedWeightedGraph<Node, EdgeAdapter>(EdgeAdapter.class);
        Enumeration nodeEnum = nodes.elements();
        Enumeration edgeEnum = edges.elements();
        while(nodeEnum.hasMoreElements()){
            graph.addVertex((Node) nodeEnum.nextElement());
        }
        while (edgeEnum.hasMoreElements()){
            Edge edge = (Edge) edgeEnum.nextElement();
            EdgeAdapter adapter = new EdgeAdapter(edge);
            graph.addEdge(adapter.getSource(),adapter.getTarget(),adapter);
            graph.addEdge(adapter.getTarget(),adapter.getSource(),adapter.reverse());
        }
        GraphBundle graphBundle = new GraphBundle();
        graphBundle.setGraph(graph);
        graphBundle.setEdges(edges);
        graphBundle.setNodes(nodes);
        return graphBundle;
    }

    public static void main(String[] args) throws IOException {

        DataInputStream nodeIn = new DataInputStream(new FileInputStream("F:\\code\\Code\\UrbanGen\\dataset\\network.node"));
        Edges edges = new Edges();
        Nodes nodes = new Nodes(edges);
        Node node = null;
        int count = 0;
        while((node = nodes.read(nodeIn)) != null){
            count ++;
            if(node.getFlag() != 0){
                System.out.println(node);
            }
        }
        System.out.println(count);
        count = 0;
        DataInputStream edgeIn = new DataInputStream(new FileInputStream("F:\\code\\Code\\UrbanGen\\dataset\\network.edge"));
        Edge actEdge = null;
        int line = 1;
        boolean eof = false;
        while (!eof) {
            try {
                actEdge = edges.read(edgeIn,nodes);
                if (actEdge != null) {
                    if(actEdge.getFlag() > 0){
                        System.out.println(actEdge);
                    }
                    count++;
                }
                else
                    System.err.println("Read error for edge on line "+line);
                line++;
            }
            catch (IOException ioe) {
                eof = true;
            }
        }
        System.out.println(count);
    }
}
