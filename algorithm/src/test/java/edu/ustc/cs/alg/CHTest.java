package edu.ustc.cs.alg;

import edu.ustc.cs.alg.alg.CH;
import edu.ustc.cs.alg.model.edge.Edge;
import edu.ustc.cs.alg.model.edge.WeightEdge;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.io.File;
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
    long starttime;

    @Before
    public void before() throws Exception {
        List<Integer> order = new ArrayList<>();
        graph = new DefaultDirectedWeightedGraph<Integer, Edge>(WeightEdge.class);
        File input = new File("F:\\java\\algs4-data\\algs4-data\\mediumEWD.txt");
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

        ch = new CH<Integer,Edge>((AbstractBaseGraph<Integer, Edge>) graph);
        long startTime = System.currentTimeMillis();
        ch.init();
        long endTime = System.currentTimeMillis();
        System.out.println("init time : " + (endTime - startTime));
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
//                    List<Integer> list = ch.getPathVertex(v,w);
//                    System.out.println(list);
//                    System.out.println(dijkstraShortestPath.getPathVertex(v,w).getVertexList());
//                }
//            }
//        }
        long startTime = System.currentTimeMillis();
        System.out.println(dijkstraShortestPath.getPath(4,2).getVertexList());
        long endTime = System.currentTimeMillis();
        System.out.println("Dijkstra Run Time : " + (endTime - startTime));

        startTime = System.currentTimeMillis();
        System.out.println(ch.getPathVertex(4, 2));
        endTime = System.currentTimeMillis();
        System.out.println("CH Run Time : " + (endTime - startTime));

    }

} 
