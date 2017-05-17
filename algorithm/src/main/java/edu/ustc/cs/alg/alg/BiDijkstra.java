package edu.ustc.cs.alg.alg;

import edu.ustc.cs.alg.model.edge.Edge;
import edu.ustc.cs.alg.model.path.ShortestPath;
import edu.ustc.cs.alg.util.FibonacciMap;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.*;

/**
 * Created by zyb on 2017/5/13.
 * 双向Dijkstra算法
 */
public class BiDijkstra<V, E extends Edge> implements ShortestPathStrategy<V, Edge> {

    protected DefaultDirectedWeightedGraph<V, Edge> graph;
    protected HashMap<V, Integer> nodeTable;
    protected int numOfVex;

    protected Double[] fordDistTo;
    protected Edge[] fordEdgeTo;
    protected V[] fordVexTo;
    protected FibonacciMap<V> fordFibonacciMap;
    protected HashSet<V> fordSearchSet;

    protected Double[] backDistTo;
    protected Edge[] backEdgeTo;
    protected V[] backVexTo;
    protected FibonacciMap<V> backFibonacciMap;
    protected HashSet<V> backSearchSet;

    public BiDijkstra(DefaultDirectedWeightedGraph<V,Edge> graph){


        nodeTable = new HashMap<V, Integer>();
        this.graph = graph;
        Set<V> set = graph.vertexSet();
        numOfVex = set.size();
        int index = 0;
        for(V v : set){
            nodeTable.put(v,index++);
        }

        fordDistTo = new Double[numOfVex];
        fordEdgeTo = new Edge[numOfVex];
        fordVexTo = (V[]) new Object[numOfVex];

        backDistTo = new Double[numOfVex];
        backEdgeTo = new Edge[numOfVex];
        backVexTo = (V[]) new Object[numOfVex];
    }

    public int index(V v){
        return nodeTable.get(v);
    }

    @Override
    public ShortestPath getPath(V source, V sink) {

        Arrays.fill(fordDistTo,Double.MAX_VALUE);
        fordDistTo[index(source)] = 0.0;
        Arrays.fill(fordEdgeTo,null);
        fordVexTo[index(source)] = source;
        fordFibonacciMap = new FibonacciMap<V>();
        fordSearchSet = new HashSet<V>();

        Arrays.fill(backDistTo,Double.MAX_VALUE);
        backDistTo[index(sink)] = 0.0;
        Arrays.fill(backEdgeTo,null);
        backVexTo[index(sink)] = sink;
        backFibonacciMap = new FibonacciMap<V>();
        backSearchSet = new HashSet<V>();

        fordFibonacciMap.put(source,0.0);
        backFibonacciMap.put(sink,0.0);

        while(!fordFibonacciMap.isEmpty() || !backFibonacciMap.isEmpty()){
            if(!fordFibonacciMap.isEmpty()){
                V v = fordFibonacciMap.removeMin();
                if(backSearchSet.contains(v)){  //前向搜索和反向搜索相遇

                    ShortestPath shortestPath = generatePath(v);

                    //寻找其他路径
                    while(!fordFibonacciMap.isEmpty()){
                        V fordV = fordFibonacciMap.removeMin();
                        if(backSearchSet.contains(fordV)){
                            Set<Edge> inCommingEdgeSet = getInCommingEdge(fordV);
                            for(Edge<V> edge : inCommingEdgeSet){
                                V fordNode = edge.getSource();
                                if(fordSearchSet.contains(fordNode)){
                                    double weight = fordDistTo[index(fordNode)] + edge.getLength() + backDistTo[index(fordV)];
                                    if(weight < shortestPath.getWeight()){
                                        shortestPath = generatePath(edge);
                                    }
                                }
                            }
                        }
                    }
                    return shortestPath;
                } else{
                    fordSearchSet.add(v);
                    relaxNode(v, true);
                }
            }
            if(!backFibonacciMap.isEmpty()){
                V v = backFibonacciMap.removeMin();
                if(fordSearchSet.contains(v)){  //反向搜索与前向搜索相遇
                    ShortestPath shortestPath = generatePath(v);
                    //寻找其他路径
                    while(!backFibonacciMap.isEmpty()){
                        V backV = backFibonacciMap.removeMin();
                        if(fordSearchSet.contains(backV)){
                            Set<Edge> outGoingEdgeSet = getOutGoingEdge(backV);
                            for(Edge<V> edge : outGoingEdgeSet){
                                V backNode = edge.getTarget();
                                if(backSearchSet.contains(backNode)){
                                    double weight = backDistTo[index(backNode)] + edge.getLength() + fordDistTo[index(backV)];
                                    if(weight < shortestPath.getWeight()){
                                        shortestPath = generatePath(edge);
                                    }
                                }
                            }
                        }
                    }
                    return shortestPath;
                } else{
                    backSearchSet.add(v);
                    relaxNode(v, false);
                }
            }
        }


        return null;
    }

    protected ShortestPath generatePath(Edge<V> edge){
        V eTemp = edge.getSource();
        V fTemp = edge.getTarget();
        List<Edge> edgeList = new ArrayList<>();

        int eIndex = index(eTemp);
        int fIndex = index(fTemp);
        while(!eTemp.equals(fordVexTo[eIndex])){
            eTemp = fordVexTo[eIndex];
            edgeList.add(fordEdgeTo[eIndex]);
            eIndex = index(eTemp);
        }
        Collections.reverse(edgeList);
        edgeList.add(edge);
        while(!fTemp.equals(backVexTo[fIndex])){
            fTemp = backVexTo[fIndex];
            edgeList.add(backEdgeTo[fIndex]);
            fIndex = index(fTemp);
        }
        ShortestPath shortestPath = new ShortestPath(edgeList);
        return shortestPath;
    }

    protected ShortestPath generatePath(V v){
        V temp = v;
        //先记录下这条路径
        ArrayList<Edge> edgeList = new ArrayList<>();
        int index = index(v);
        while(!v.equals(fordVexTo[index])){
            v = fordVexTo[index];
            edgeList.add(fordEdgeTo[index]);
            index = index(v);
        }
        Collections.reverse(edgeList);
        v = temp;
        index = index(v);
        while(!v.equals(backVexTo[index])){
            v = backVexTo[index];
            edgeList.add(backEdgeTo[index]);
            index = index(v);
        }
        ShortestPath shortestPath = new ShortestPath(edgeList);
        return shortestPath;
    }

    protected void relaxNode(V v, boolean flag){
        if(flag){
            Set<Edge> edgeSet = getOutGoingEdge(v);
            for(Edge edge : edgeSet){
                relax(edge, flag);
            }
        } else{
            Set<Edge> edgeSet = getInCommingEdge(v);
            for(Edge edge : edgeSet){
                relax(edge, flag);
            }
        }
    }

    protected void relax(Edge<V> edge, boolean flag){
        V s = edge.getSource();
        V t = edge.getTarget();
        if(flag){
            double temp = fordDistTo[index(s)] + edge.getLength();
            int indexOfT = index(t);
            if(fordDistTo[indexOfT] > temp){
                fordDistTo[indexOfT] = temp;
                fordEdgeTo[indexOfT] = edge;
                fordVexTo[indexOfT] = s;
                fordFibonacciMap.put(t,temp);
            }
        } else{
            double temp = backDistTo[index(t)] + edge.getLength();
            int indexOfS = index(s);
            if(backDistTo[indexOfS] > temp){
                backDistTo[indexOfS] = temp;
                backEdgeTo[indexOfS] = edge;
                backVexTo[indexOfS] = t;
                backFibonacciMap.put(s,temp);
            }
        }
    }

    protected Set<Edge> getInCommingEdge(V v){
        Set<Edge> set = new HashSet<>();
        for(Edge edge : graph.edgesOf(v)){
            if(edge.getTarget().equals(v)){
                set.add(edge);
            }
        }
        return set;
    }

    protected Set<Edge> getOutGoingEdge(V v){
        Set<Edge> set = new HashSet<>();
        for(Edge edge : graph.edgesOf(v)){
            if(edge.getSource().equals(v)){
                set.add(edge);
            }
        }
        return set;
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
