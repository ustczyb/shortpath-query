package edu.ustc.cs;

import edu.ustc.cs.alg.CH;
import edu.ustc.cs.model.edge.Edge;
import edu.ustc.cs.model.edge.WeightEdge;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
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
        File input = new File("F:\\java\\algs4-data\\algs4-data\\tinyEWD.txt");
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
        ch.init();
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
        for(Integer v : graph.vertexSet()){
            for(Integer w : graph.vertexSet()){
                if(!v.equals(w)){
                    List<Integer> list = ch.getPath(v,w);
                    System.out.println(list);
                    System.out.println(dijkstraShortestPath.getPath(v,w).getVertexList());
                }
            }
        }

//        System.out.println(ch.getPath(0, 5));
    }

} 
