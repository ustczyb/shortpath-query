package edu.ustc.cs.alg;

import edu.ustc.cs.alg.alg.SILC;
import edu.ustc.cs.alg.model.edge.Edge;
import edu.ustc.cs.alg.model.edge.WeightEdge;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by zyb on 2017/3/8.
 */
public class SILCTest {
    WeightedGraph<Integer, Edge> graph;
    SILC<Integer,Edge> silc;

    @Before
    public void before() throws Exception {
        List<Integer> order = new ArrayList<>();
        graph = new SimpleWeightedGraph<Integer, Edge>(WeightEdge.class);
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
        silc = new SILC<>(graph);
        silc.init();
    }

    @Test
    public void testGetPath(){
        System.out.println(silc.getPathVertex(4,2));
    }

}
