package edu.ustc.cs;

import edu.ustc.cs.demo.CH;
import edu.ustc.cs.model.edge.Edge;
import edu.ustc.cs.model.edge.WeightEdge;
import edu.ustc.cs.model.graph.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
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

    UndirectedGraph<Integer, Edge> graph;
    CH<Integer,Edge> ch;

@Before
public void before() throws Exception {
    List<Integer> order = new ArrayList<>();
    graph = new SimpleGraph<>(WeightEdge.class);
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
    ch = new CH<>(order,graph);
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
public void testInit() throws Exception {
    ch.init();
    System.out.println(graph);
}

/** 
* 
* Method: queryShortPath(V v1, V v2) 
* 
*/ 
@Test
public void testQueryShortPath() throws Exception { 
//TODO: Test goes here... 
} 


} 
