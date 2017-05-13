package edu.ustc.cs.alg.alg;

import edu.ustc.cs.alg.model.edge.Edge;
import edu.ustc.cs.alg.model.path.ShortestPath;
import edu.ustc.cs.alg.util.FibonacciMap;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.*;

/**
 * Created by helloworld on 2017/5/11.
 */
public class Dijkstra<V, E extends Edge> implements ShortestPathStrategy<V, Edge>{

    DefaultDirectedWeightedGraph<V, Edge> graph;
    HashMap<V, Integer> nodeTable;
    int numOfVex;
    Double[] distTo;
    Edge[] edgeTo;
    V[] vexTo;
    FibonacciMap<V> fibonacciMap;

    public Dijkstra(DefaultDirectedWeightedGraph<V,Edge> graph){
        nodeTable = new HashMap<V, Integer>();
        this.graph = graph;
        Set<V> set = graph.vertexSet();
        numOfVex = set.size();
        int index = 0;
        for(V v : set){
            nodeTable.put(v,index++);
        }
        distTo = new Double[numOfVex];
        edgeTo = new Edge[numOfVex];
        vexTo = (V[]) new Object[numOfVex];
    }

    public int index(V v){
        return nodeTable.get(v);
    }

    @Override
    public ShortestPath getPath(V source, V sink) {

        Arrays.fill(distTo,Double.MAX_VALUE);
        distTo[index(source)] = 0.0;
        Arrays.fill(edgeTo,null);
        vexTo[index(source)] = source;
        fibonacciMap = new FibonacciMap<V>();

        fibonacciMap.put(source,0.0);
        while (!fibonacciMap.isEmpty()){
            V v = fibonacciMap.removeMin();
            if(v.equals(sink) ){
                ArrayList<Edge> edgeList = new ArrayList<>();

                while(!vexTo[index(v)].equals(v)){
                    edgeList.add(edgeTo[index(v)]);
                    v = vexTo[index(v)];
                }
                Collections.reverse(edgeList);
                ShortestPath shortestPath = new ShortestPath(edgeList);
                return shortestPath;
            } else{
                relaxNode(v);
            }
        }


        return null;
    }

    private void relaxNode(V v){
        for(Edge<V> edge : graph.edgesOf(v)){
            if(edge.getSource().equals(v) ){
                relax(edge);
            }
        }
    }

    private void relax(Edge<V> edge){
        V s = edge.getSource();
        V t = edge.getTarget();
        double temp = distTo[index(s)] + edge.getLength();
        int indexOfT = index(t);
        if(distTo[indexOfT] > temp){
            distTo[indexOfT] = temp;
            edgeTo[indexOfT] = edge;
            vexTo[indexOfT] = s;
            fibonacciMap.put(t,temp);
        }
    }

    @Override
    public List getPathVertex(V source, V sink) {
        return getPath(source, sink).getVertexList();
    }

    @Override
    public double getPathWeight(V source, V sink) {
        return getPath(source, sink).getWeight();
    }
}
