package edu.ustc.cs.alg;


import edu.ustc.cs.model.edge.Edge;
import edu.ustc.cs.model.edge.ShortCut;
import edu.ustc.cs.model.edge.WeightEdge;
import edu.ustc.cs.util.GraphUtil;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.ListSingleSourcePathsImpl;

import java.util.*;

/**
 * Created by zyb on 2017/2/22.
 */
public class CH<V, E extends Edge> implements ShortestPathStrategy<V,E> {

    private List<V> order;
    private Graph<V, Edge> graph;
    private DijkstraShortestPath<V, Edge> dijkstra;

    public CH(List<V> order, Graph<V, Edge> graph) {
        this.order = order;
        this.graph = graph;
        dijkstra = new DijkstraShortestPath<V, Edge>(graph);
    }

    public List<V> generateOrder(Graph<V,E> graph){
        List<V> order = new ArrayList<V>();
        //TODO generate the order
        return order;
    }

    public void init(){
        HashSet<V> hashSet = new HashSet<V>();
        for(V v : order){
            hashSet.add(v);
            Set<Edge> edges = graph.edgesOf(v);
            List<V> list = new ArrayList<V>();          //层次低于v的相邻顶点
            for(Edge e : edges){
                if(!hashSet.contains(e.getAnotherVertex(v))){
                    list.add((V) e.getAnotherVertex(v));
                }
            }
            // TODO 可优化 只计算那些有到达v的有向边的点即可，无需遍历所有顶点
            for(int i = 0; i < list.size(); i ++){
                for(int j = i+1; j < list.size(); j++){
                    V vi = list.get(i);
                    V vj = list.get(j);
                    GraphPath graphPath = dijkstra.getPath(vi,vj);
                    List<V> path = graphPath.getVertexList();
                    Double length = graphPath.getWeight();
                    if(path.contains(v)){

                        List<Edge> edgeList = graphPath.getEdgeList();
                        ShortCut<V> shortCut = new ShortCut<V>();
                        shortCut.setSource(vi);
                        shortCut.setTarget(vj);
                        //可能会出现shortcut中嵌套shortcut的情况，即shortcut返回的path是不可行的
                        List<V> vList = new ArrayList<V>();
                        for(Edge<V> e : edgeList){
                            if(e instanceof ShortCut){
                                vList.addAll(((ShortCut) e).getPath());
                                vList.remove(vList.size()-1);
                            } else {
                                vList.add(e.getSource());
                            }
                        }
                        vList.add(vj);
                        shortCut.setPath(vList);
                        shortCut.setLength(length);

                        graph.addEdge(vi,vj,shortCut);
                    }
                }
            }
        }
    }

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
            set = ((DirectedGraph) graph).outgoingEdgesOf(v);
        } else{
            set = graph.edgesOf(v);
        }
        return set;
    }

    private Set<Edge> getInCommingEdges(V v){
        Set<Edge> set = null;
        if(graph instanceof DirectedGraph){
            set = ((DirectedGraph) graph).incomingEdgesOf(v);
        } else{
            set = graph.edgesOf(v);
        }
        return set;
    }

    private List<Edge> getUpperEdgeList(V v){
        List<Edge> list = new ArrayList<Edge>();
        Set<Edge> set = getOutGoingEdges(v);
        for(Edge e : set){
            if(isForwardEdge(e)){
                list.add(e);
            }
        }
        return list;
    }

    private List<Edge> getLowerEdgeList(V v){
        List<Edge> list = new ArrayList<Edge>();
        Set<Edge> set = getInCommingEdges(v);
        for(Edge e : set){
            if(!isForwardEdge(e)){
                list.add(e);
            }
        }
        return list;
    }

    public BiDijkstra getInstense(){
        return new BiDijkstra();
    }

    @Override
    public List<V> getPath(V source, V sink){
        return getInstense().getPathVertexs(source, sink);
    }


    public List<Edge> getPathEdges(V source, V sink){
        return getInstense().getPathEdges(source, sink);
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
        //TODO 可优化 手写双向dijstra算法可以减少图的复制操作
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

    private class BiDijkstra{

        private Hashtable<V,Integer> vertexToInteger;
        private int numOfVertex;

        private double[] fordDistTo;
        private Edge[] fordEdgeTo;
        private V[] fordVexTo;

        private double[] backDistTo;
        private Edge[] backEdgeTo;
        private V[] backVexTo;

        public BiDijkstra(){
            vertexToInteger = GraphUtil.getVexIndex(graph);
            numOfVertex = vertexToInteger.size();
        }

        private Integer indexOfVertex(V v){
            return vertexToInteger.get(v);
        }

        public List<V> getPathVertexs(V source, V sink){
            List<V> list = new ArrayList<V>();
            List<Edge> edges = getPathEdges(source, sink);
            for(Edge<V> e : edges){
                if(e instanceof ShortCut){
                    list.addAll(((ShortCut) e).getPath());
                    list.remove(list.size() - 1);
                } else {
                    list.add(e.getSource());
                }
            }
            list.add(sink);
            return list;
        }

        public List<Edge> getPathEdges(V source, V sink){

            fordDistTo = new double[numOfVertex];
            fordVexTo = (V[]) new Object[numOfVertex];
            fordEdgeTo = new Edge[numOfVertex];
            for(int i = 0; i < numOfVertex; i++)
                fordDistTo[i] = Double.POSITIVE_INFINITY;
            fordDistTo[indexOfVertex(source)] = 0.0;
            fordVexTo[indexOfVertex(source)] = source;

            backDistTo = new double[numOfVertex];
            backVexTo = (V[]) new Object[numOfVertex];
            backEdgeTo = new Edge[numOfVertex];
            for(int i = 0; i < numOfVertex; i++)
                backDistTo[i] = Double.POSITIVE_INFINITY;
            backDistTo[indexOfVertex(sink)] = 0.0;
            backVexTo[indexOfVertex(sink)] = sink;

            List<Edge> result = new ArrayList<Edge>();
            List<V> sourceList = new ArrayList<V>();        //从source搜索过的节点
            List<V> sinkList = new ArrayList<V>();

            PriorityQueue<Edge<V>> sourceQueue = new PriorityQueue<>();
            PriorityQueue<Edge<V>> sinkQueue = new PriorityQueue<>();
            sourceQueue.add(new WeightEdge(source, source,0.0));
            sinkQueue.add(new WeightEdge(sink, sink, 0.0));

            while(!sourceQueue.isEmpty() || !sinkQueue.isEmpty()){
                //source搜索
                if(!sourceQueue.isEmpty()){
                    V sourceV = sourceQueue.poll().getTarget();
                    if(sinkList.contains(sourceV)){
                        V v = sourceV;
                        int index = indexOfVertex(sourceV);
                        while(!v.equals(fordVexTo[index])){
                            v = fordVexTo[index];
                            result.add(fordEdgeTo[index]);
                            index = indexOfVertex(v);
                        }
                        Collections.reverse(result);
                        v = sourceV;
                        index = indexOfVertex(v);
                        while(!v.equals(backVexTo[indexOfVertex(v)])){
                            v = backVexTo[indexOfVertex(v)];
                            result.add(backEdgeTo[index]);
                            index = indexOfVertex(v);
                        }
                        return result;
                    }
                    sourceList.add(sourceV);
                    relax(sourceV, sourceQueue, true);
                }

                //sink搜索
                if(!sinkQueue.isEmpty()){
                    V sinkV = sinkQueue.poll().getSource();
                    if(sourceList.contains(sinkV)){
                        V v = sinkV;
                        int index = indexOfVertex(sinkV);
                        while(!v.equals(fordVexTo[index])){
                            v = fordVexTo[index];
                            result.add(fordEdgeTo[index]);
                            index = indexOfVertex(v);
                        }
                        Collections.reverse(result);
                        v = sinkV;
                        index = indexOfVertex(v);
                        while(!v.equals(backVexTo[indexOfVertex(v)])){
                            v = backVexTo[indexOfVertex(v)];
                            result.add(backEdgeTo[index]);
                            index = indexOfVertex(v);
                        }
                        return result;
                    }
                    sinkList.add(sinkV);
                    relax(sinkV, sinkQueue, false);
                }

            }
            return result;
        }

        private void relax(V v, PriorityQueue<Edge<V>> pq, boolean isForward){
            int indexOfV = indexOfVertex(v);
            List<Edge> list = null;
            if(isForward){
                list = getUpperEdgeList(v);
                for(Edge<V> e : list){
                    V w = e.getTarget();
                    int indexOfW = indexOfVertex(w);
                    if(fordDistTo[indexOfW] > fordDistTo[indexOfV] + e.getLength()) {
                        fordDistTo[indexOfW] = fordDistTo[indexOfV] + e.getLength();
                        fordEdgeTo[indexOfW] = e;
                        fordVexTo[indexOfW] = v;
                        pq.add(e);
                    }
                }
            } else {
                list = getLowerEdgeList(v);
                for(Edge<V> e : list){
                    V w = e.getSource();
                    int indexOfW = indexOfVertex(w);
                    if(backDistTo[indexOfW] > backDistTo[indexOfV] + e.getLength()) {
                        backDistTo[indexOfW] = backDistTo[indexOfV] + e.getLength();
                        backEdgeTo[indexOfW] = e;
                        backVexTo[indexOfW] = v;
                        pq.add(e);
                    }
                }
            }
        }
    }

}
