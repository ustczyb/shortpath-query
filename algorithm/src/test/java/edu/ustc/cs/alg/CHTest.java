package edu.ustc.cs.alg;

import edu.ustc.cs.alg.alg.BiDijkstra;
import edu.ustc.cs.alg.alg.CH;
import edu.ustc.cs.alg.alg.Dijkstra;
import edu.ustc.cs.alg.model.edge.Edge;
import edu.ustc.cs.alg.model.edge.WeightEdge;
import edu.ustc.cs.alg.model.path.ShortestPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** 
* CH Tester. 
* 
* @author <Authors name> 
* @since <pre>���� 1, 2017</pre> 
* @version 1.0 
*/ 
public class CHTest {

    WeightedGraph<Integer, Edge> graph;
    CH<Integer,Edge> ch;
    Dijkstra<Integer,Edge> dijkstra;
    BiDijkstra<Integer, Edge> biDijkstra;
    long starttime;

    @Before
    public void before() throws Exception {
        List<Integer> order = new ArrayList<>();
        graph = new DefaultDirectedWeightedGraph<Integer, Edge>(WeightEdge.class);
        File input = new File("dataset/mediumEWD.txt");
        Scanner in = new Scanner(input);
        int num = in.nextInt();
        for(int i = 0; i < num; i++){           //添加顶点
            graph.addVertex(i);
            order.add(i);
        }
        in.nextInt();
        while (in.hasNext()){  //录入边
            int v = in.nextInt();
            int w = in.nextInt();
            double weight = in.nextDouble();
            WeightEdge<Integer> edge = new WeightEdge<Integer>(v,w,weight);
            graph.addEdge(v,w,edge);
        }
        in.close();

   //    dijkstra = new Dijkstra((DefaultDirectedWeightedGraph) graph);
        biDijkstra = new BiDijkstra<>((DefaultDirectedWeightedGraph<Integer, Edge>) graph);
        ch = new CH<Integer,Edge>((AbstractBaseGraph<Integer, Edge>) graph);
        long startTime = System.currentTimeMillis();
        ch.init();
        long endTime = System.currentTimeMillis();
        System.out.println("init time : " + (endTime - startTime));
  //      ch.writeObject("F:\\test");
    }

 //   @Before
    public void testSerialize() throws IOException, ClassNotFoundException {
        ch = CH.readObject("F:\\test");
    }

    @After
    public void after() throws Exception {
    }

    /**
    *
    * Method: init()
    *
    */

    @Test
    public void testCanculateEdgeDifference() {
        for(int i = 0; i < 8; i++){
            int result = ch.calculateEdgeDifference(i);
            System.out.println(result);
        }

    }

    @Test
    public void testInit() throws Exception {
        System.out.println(ch.getOrder());
        System.out.println(ch.getGraph());
    }

    /**
    *
    * Method: queryShortPath(V v1, V v2)
    *
    */
    @Test
    public void testQueryShortPath() throws Exception {

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
//        for(Integer v : graph.vertexSet()){
//            for(Integer w : graph.vertexSet()){
//                if(!v.equals(w)){
//                    List<Integer> list = biDijkstra.getPathVertex(v,w);
//                    System.out.println(list);
//                    System.out.println(dijkstraShortestPath.getPath(v,w).getVertexList());
//                }
//            }
//        }
        long startTime = System.currentTimeMillis();
        System.out.println(biDijkstra.getPath(40,22).getVertexList());
        long endTime = System.currentTimeMillis();
  //      System.out.println("Dijkstra Run Time : " + (endTime - startTime));

        startTime = System.currentTimeMillis();
        ShortestPath shortestPath = ch.getPath(40,22);
        System.out.println(shortestPath.getVertexList() + " " + shortestPath.getWeight());
        endTime = System.currentTimeMillis();
        System.out.println("CH Run Time : " + (endTime - startTime));

    }

} 
