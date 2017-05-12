package edu.ustc.cs.alg;

import edu.ustc.cs.alg.alg.CH;
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
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.AbstractBaseGraph;
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
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("dataset/test/roadNetwork.object"));
        graph = (Graph<Node, Edge>) ois.readObject();
        flat = Flat.builder().x1(0l).x2(22000l).y1(0l).y2(31000l).build();
        long startTime = System.currentTimeMillis();
        tnr = new TNR(graph,flat,1);
        tnr.init();
        long endTime = System.currentTimeMillis();
        System.out.println("init time : " + (endTime - startTime));
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
        long startTime = System.currentTimeMillis();
        ShortestPath sp = tnr.getPath(sourceId, targetId);
        long endTime = System.currentTimeMillis();
        System.out.println("query time : " + (endTime - startTime));
        System.out.println(sp.getVertexList());
        CH ch = new CH((AbstractBaseGraph) graph);
        startTime = System.currentTimeMillis();
        ch.init();
        endTime = System.currentTimeMillis();
        System.out.println("ch init time : " + (endTime - startTime));
        startTime = System.currentTimeMillis();
        ShortestPath sp2 = ch.getPath(tnr.getNode(sourceId), tnr.getNode(targetId));
        endTime = System.currentTimeMillis();
        System.out.println("ch query time : " + (endTime - startTime));
    }

    @Test
    public void testSILC(){
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        long startTime = System.currentTimeMillis();
        for(Node v : graph.vertexSet()){
            dijkstraShortestPath.getPaths(v);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("SILC init time" + (endTime - startTime));
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
