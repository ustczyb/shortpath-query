package edu.ustc.cs.alg.alg;


import edu.ustc.cs.alg.model.edge.ShortCut;
import edu.ustc.cs.alg.model.vertex.VertexAdapter;
import edu.ustc.cs.alg.util.FibonacciMap;
import edu.ustc.cs.alg.model.edge.Edge;
import edu.ustc.cs.alg.model.edge.WeightEdge;
import edu.ustc.cs.alg.model.path.ShortestPath;
import edu.ustc.cs.alg.util.GraphUtil;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.ListSingleSourcePathsImpl;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.io.*;
import java.util.*;

/**
 * Created by zyb on 2017/2/22.
 */
public class CH<V, E extends Edge> implements ShortestPathStrategy<V,E>,Serializable {

    //顶点的顺序
    private List<V> order;
    //由于CH算法要在图上添加shortcut从而改变原图的结构，因此我们把原图进行clone一份用于进行CH的预处理，虽然只是浅拷贝但是不会对原图产生影响
    private Graph<V, Edge> graph;

    private Graph<V,Edge> originalGraph;

    //将预处理完成的图和顶点顺序序列化
    public void writeObject(String dirName) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dirName + "\\order.obj"));
        oos.writeObject((ArrayList<V>)order);
        oos.close();
        oos = new ObjectOutputStream(new FileOutputStream(dirName + "\\graph.obj"));
        oos.writeObject((DefaultDirectedWeightedGraph<V,Edge>)graph);
        oos.close();
    }
    //根据本地文件进行反序列化
    public static CH readObject(String dirName) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dirName + "\\order.obj"));
        List order = (List) ois.readObject();
        ois.close();
        ois = new ObjectInputStream((new FileInputStream(dirName + "\\graph.obj")));
        DefaultDirectedWeightedGraph graph = (DefaultDirectedWeightedGraph) ois.readObject();
        CH result = new CH(order,graph);
        return result;
    }

    public CH(List<V> order, Graph<V, Edge> graph) {
        this.order = order;
        this.graph = graph;
    }
    //使用默认的顶点排序方式进行预处理
    public CH(AbstractBaseGraph<V, Edge> graph){
        this.originalGraph = graph;
        this.graph = (Graph<V, Edge>) graph.clone();
        this.order = new ArrayList<V>(graph.vertexSet().size());
        init();
    }

    //根据给出的顶点顺序进行预处理
    public CH(AbstractBaseGraph<V, Edge> graph,List<V> order){
        this.originalGraph = graph;
        this.graph = (Graph<V, Edge>) graph.clone();
        this.order = new ArrayList<V>(graph.vertexSet().size());
        init(order);
    }

    public Graph getGraph(){
        return graph;
    }

    public List<V> getOrder(){
        return order;
    }

    //默认排序方式收缩顶点
    private void contract(V v){
        Set<Edge> inCommingEdges = getInCommingEdges(v);
        Set<Edge> outGoningEdges = getOutGoingEdges(v);
        for(Edge inCommingEdge : inCommingEdges){
            V u = (V) inCommingEdge.getSource();
            if(order.contains(u)){
                continue;
            }
            for(Edge outGoningEdge : outGoningEdges){
                V w = (V) outGoningEdge.getTarget();
                if(order.contains(w) || w.equals(u)){
                    continue;
                }
                Edge u2w = graph.getEdge(u,w);
                double weight = inCommingEdge.getLength() + outGoningEdge.getLength();
                if(u2w == null || u2w.getLength() > weight){
                    if(u2w != null){
                        graph.removeEdge(u2w);
                    }
                    //add shortcut
                    ShortCut<V> shortCut = new ShortCut<V>();
                    shortCut.setSource(u);
                    shortCut.setTarget(w);
                    shortCut.setLength(weight);
                    List<V> list = new ArrayList<V>();
                    list.addAll(inCommingEdge.getVertexs());
                    list.remove(list.size() - 1);
                    list.addAll(outGoningEdge.getVertexs());
                    shortCut.setPath(list);
                    graph.addEdge(u,w,shortCut);

                } else {
                    continue;
                }
            }
        }
        order.add(v);
    }

    /*
    计算顶点v的edge difference
     */
    private int calculateEdgeDifference(V v){
        int count = 0;
        Set<Edge> inCommingEdges = getInCommingEdges(v);
        Set<Edge> outGoningEdges = getOutGoingEdges(v);
        for(Edge outGoingEdge : outGoningEdges){
            V w = (V) outGoingEdge.getTarget();
            if(order.contains(w)){
                continue;
            }
            count --;
        }
        for(Edge inCommingEdge : inCommingEdges){
            V u = (V) inCommingEdge.getSource();
            if(order.contains(u)){
                continue;
            }
            count --;
            for(Edge outGoingEdge : outGoningEdges){
                V w = (V) outGoingEdge.getTarget();
                if(order.contains(w)){
                    continue;
                }
                Edge u2w = graph.getEdge(u,w);
                if(u2w == null){
                    count ++;
                } else {
                    continue;
                }
            }
        }
        return count;
    }

    private void init(List<V> order){
        this.order = order;
        for(V v : order){
            contractByOrder(v);
        }
    }

    private void contractByOrder(V v){
        Set<Edge> inCommingEdges = getInCommingEdges(v);
        Set<Edge> outGoningEdges = getOutGoingEdges(v);
        int orderOfV = order.indexOf(v);
        for(Edge inCommingEdge : inCommingEdges){
            V u = (V) inCommingEdge.getSource();
            int orderOfU = order.indexOf(u);
            if(orderOfU < orderOfV){
                continue;
            }
            for(Edge outGoningEdge : outGoningEdges){
                V w = (V) outGoningEdge.getTarget();
                int orderOfW = order.indexOf(w);
                if(orderOfW < orderOfV || w.equals(u)){
                    continue;
                }
                Edge u2w = graph.getEdge(u,w);
                double weight = inCommingEdge.getLength() + outGoningEdge.getLength();
                if(u2w == null || u2w.getLength() > weight){
                    if(u2w != null){
                        graph.removeEdge(u2w);
                    }
                    //add shortcut
                    ShortCut<V> shortCut = new ShortCut<V>();
                    shortCut.setSource(u);
                    shortCut.setTarget(w);
                    shortCut.setLength(weight);
                    List<V> list = new ArrayList<V>();
                    list.addAll(inCommingEdge.getVertexs());
                    list.remove(list.size() - 1);
                    list.addAll(outGoningEdge.getVertexs());
                    shortCut.setPath(list);
                    graph.addEdge(u,w,shortCut);

                } else {
                    continue;
                }
            }
        }
    }

    public void init(){
        System.out.println("init method running ...");
        FibonacciMap<V> sortMap = new FibonacciMap<V>();
        for(V v : graph.vertexSet()){
            sortMap.put(v,calculateEdgeDifference(v));
        }
        while (!sortMap.isEmpty()){
            V nextOrderedVertex = sortMap.removeMin();
            contract(nextOrderedVertex);

            //更新刚刚被收缩点邻居的edge difference
            for(Edge edge : graph.edgesOf(nextOrderedVertex)){
                V adjVertex = (V) edge.getTarget();
                if(!order.contains(adjVertex)){
                    sortMap.put(adjVertex,calculateEdgeDifference(adjVertex));
                }
            }
        }
        System.out.println("init method over");
        System.out.println("shortcut num : " + (graph.edgeSet().size() - originalGraph.edgeSet().size()));
    }
    //TODO CH算法的双向Dijkstra计算incomming upper边集在搜索结束时要分开

    private boolean isForwardEdge(Edge<V> edge){
        if(order.indexOf(edge.getSource()) < order.indexOf(edge.getTarget())){
            return true;
        } else{
            return false;
        }
    }

    private Set<Edge> getOutGoingEdges(V v){
        Set<Edge> set = null;
        if(graph instanceof DirectedGraph){
            set = new HashSet<>();
            Set<Edge> allEdges = graph.edgesOf(v);
            for(Edge edge : allEdges){
                if(edge.getSource().equals(v)){
                    set.add(edge);
                }
            }
        } else{
            set = graph.edgesOf(v);
        }
        return set;
    }

    private Set<Edge> getInCommingEdges(V v){
        Set<Edge> set = null;
        if(graph instanceof DirectedGraph){
            set = new HashSet<>();
            Set<Edge> allEdges = graph.edgesOf(v);
            for(Edge edge : allEdges){
                if(edge.getTarget().equals(v)){
                    set.add(edge);
                }
            }
        } else{
            set = graph.edgesOf(v);
        }
        return set;
    }

    private Set<Edge> getUpperEdgeList(V v){
        Set<Edge> result = new HashSet<>();
        Set<Edge> set = getOutGoingEdges(v);
        for(Edge e : set){
            if(isForwardEdge(e)){
                result.add(e);
            }
        }
        return result;
    }

    private Set<Edge> getLowerEdgeList(V v){
        Set<Edge> list = new HashSet<>();
        Set<Edge> set = getInCommingEdges(v);
        for(Edge e : set){
            if(!isForwardEdge(e)){
                list.add(e);
            }
        }
        return list;
    }

    private BiDijkstra getInstense(){
        return new BiDijkstra((DefaultDirectedWeightedGraph) graph);
    }

    public ShortestPath getPath(V source, V sink){
        return  getInstense().getPath(source, sink);
    }

    public List<V> getPathVertex(V source, V sink){
        return getInstense().getPathVertex(source, sink);
    }


    public List<Edge> getPathEdges(V source, V sink){
        return getInstense().getPath(source, sink).getEdgeList();
    }

    public GraphPath<V, Edge> getGraphPath(V source, V sink) {
        Set<V> vexs = graph.vertexSet();
        int min = Math.min(order.indexOf(source),order.indexOf(sink));
        List<V> list = new ArrayList<V>();
        for(V v : vexs){
            if(order.indexOf(v) < min){
                list.add(v);
            }
        }
        graph.removeAllVertices(list);
        BidirectionalDijkstraShortestPath<V,Edge> bidirectionalDijkstraShortestPath = new BidirectionalDijkstraShortestPath<V, Edge>(graph);
        return bidirectionalDijkstraShortestPath.getPath(source,sink);
    }

    @Override
    public double getPathWeight(V source, V sink) {
        GraphPath<V, Edge> p = getGraphPath(source, sink);
        if (p == null) {
            return Double.POSITIVE_INFINITY;
        } else {
            return p.getWeight();
        }
    }

    public ShortestPathAlgorithm.SingleSourcePaths<V, Edge> getPaths(V source) {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException("graph must contain the source vertex");
        }

        Map<V, GraphPath<V, Edge>> paths = new HashMap<>();
        for (V v : graph.vertexSet()) {
            paths.put(v, getGraphPath(source, v));
        }
        return new ListSingleSourcePathsImpl<V,Edge>(graph, source, paths);
    }

    //CH查询时使用的双向Dijkstra算法
    private class BiDijkstra extends edu.ustc.cs.alg.alg.BiDijkstra<V, Edge>{

        public BiDijkstra(DefaultDirectedWeightedGraph graph) {
            super(graph);
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
                                Set<Edge> inCommingEdgeSet = getInCommingEdges(fordV);
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
                        while(!backFibonacciMap.isEmpty()){
                            V backV = backFibonacciMap.removeMin();
                            if(fordSearchSet.contains(backV)){
                                Set<Edge> outGoingEdgeSet = getOutGoingEdges(backV);
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
                                Set<Edge> outGoingEdgeSet = getOutGoingEdges(backV);
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
                        while(!fordFibonacciMap.isEmpty()){
                            V fordV = fordFibonacciMap.removeMin();
                            if(backSearchSet.contains(fordV)){
                                Set<Edge> inCommingEdgeSet = getInCommingEdges(fordV);
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
                        backSearchSet.add(v);
                        relaxNode(v, false);
                    }
                }
            }
            return null;
        }

        @Override
        protected Set<Edge> getInCommingEdge(V o) {
            return getLowerEdgeList(o);
        }

        @Override
        protected Set<Edge> getOutGoingEdge(V o) {
            return getUpperEdgeList(o);
        }
    }

}
