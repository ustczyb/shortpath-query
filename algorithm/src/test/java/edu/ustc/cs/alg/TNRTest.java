package edu.ustc.cs.alg;

import edu.ustc.cs.alg.alg.TNR;
import edu.ustc.cs.alg.model.coordinate.Flat;
import edu.ustc.cs.alg.model.coordinate.Node;
import edu.ustc.cs.alg.model.edge.Edge;
import edu.ustc.cs.alg.model.graph.SpatialNetwork;
import edu.ustc.cs.alg.model.path.ShortestPath;
import edu.ustc.cs.alg.model.vertex.VertexAdapter;
import edu.ustc.cs.alg.util.ReadBinaryFileUtil;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

/**
 * Created by zyb on 2017/4/30.
 */
public class TNRTest {

    SpatialNetwork spatialNetwork;
    Graph<Node, Edge> graph;
    TNR tnr;
    Flat flat;

//    @Before
//    public void init() throws FileNotFoundException {
//        spatialNetwork = ReadBinaryFileUtil.file2graph("F:\\code\\Code\\UrbanGen\\dataset\\");
//        graph = spatialNetwork.getGraph();
//    }

    @Test
    public void serilize() throws IOException {
        spatialNetwork = ReadBinaryFileUtil.file2graph("F:\\code\\Code\\UrbanGen\\dataset\\");
        graph = spatialNetwork.getGraph();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("F:\\test\\roadNetwork.object"));
        oos.writeObject(graph);
        oos.close();
    }

    @Before
    public void readFromFile() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("F:\\\\test\\\\roadNetwork.object"));
        graph = (Graph<Node, Edge>) ois.readObject();
        flat = Flat.builder().x1(0l).x2(22000l).y1(0l).y2(31000l).build();
        tnr = new TNR(graph,flat,10);
        tnr.init();
    }

    @Test
    public void testInit(){
        System.out.println(tnr.getRoadNetwork().vertexSet().size());
        System.out.println(tnr.getRoadNetwork().edgeSet().size());
        System.out.println(tnr.containsEdge(7442l,3908l));
    }

    @Test
    public void testSP(){
        long sourceId = 7442l;
        long targetId = 3908l;
        ShortestPath sp = tnr.getPath(sourceId, targetId);
        System.out.println(sp.getVertexList());
    }

    @Test
    public void calculateFlat(){
        long maxX = 0l,maxY = 0l;
        for(Node node : graph.vertexSet()){
            if(node.getX() > maxX){
                maxX = node.getX();
            }
            if(node.getY() > maxY){
                maxY = node.getY();
            }
        }
        System.out.println(maxX + "  " + maxY);
    }

}
