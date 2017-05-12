package edu.ustc.cs.alg.alg;

import edu.ustc.cs.alg.model.edge.Edge;
import edu.ustc.cs.alg.model.path.Path;
import edu.ustc.cs.alg.model.path.ShortestPath;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.AbstractBaseGraph;

import java.util.*;

/**
 * Created by zyb on 2017/3/10.
 * TNR based on CH
 */
public class ChTnr<V,E extends Edge> implements ShortestPathStrategy<V,E> {

    private Graph<V,Edge> graph;
    private List<V> order;
    private List<V> transNodes;
    private Hashtable<V,Integer> vertexToInteger;
    private int numOfVertex;

    private CH ch;

    private Hashtable<V, Hashtable<V, Path>> forwardAccessNodeTable;
    private Hashtable<V, Hashtable<V, Path>> backwardAccessNodeTable;
    private Hashtable<V, Hashtable<V, Path>> accessNodeDistanceTable;

    public ChTnr(AbstractBaseGraph<V,Edge> graph){
        ch = new CH(graph);
        ch.init();
        this.graph = ch.getGraph();
        this.order = ch.getOrder();
        numOfVertex = graph.vertexSet().size();
        transNodes = order.subList(numOfVertex - (int) Math.sqrt(numOfVertex), numOfVertex);
    }

    public ChTnr(AbstractBaseGraph<V,Edge> graph,List<V> order){
        this.graph = graph;
        this.order = order;
        numOfVertex = graph.vertexSet().size();
        transNodes = order.subList(numOfVertex - (int) Math.sqrt(numOfVertex), numOfVertex);
    }

    public List<V> getOrder(){
        return order;
    }

    public List<V> getTransNodes(){
        return transNodes;
    }

    public void init(){

        accessNodeDistanceTable = new Hashtable<V, Hashtable<V, Path>>(transNodes.size());
        forwardAccessNodeTable = new Hashtable<V, Hashtable<V, Path>>(numOfVertex);
        backwardAccessNodeTable = new Hashtable<V, Hashtable<V, Path>>(numOfVertex);

        //1.存储中转节点距离
        System.out.println("saving transNode shortPath table...");
        for(V v : transNodes){
            Hashtable<V, Path> hashtable = new Hashtable<V, Path>();
            for(V w : transNodes){
                if(w != v){
                    hashtable.put(w, ch.getPath(v, w));
                }
            }
            accessNodeDistanceTable.put(v, hashtable);
        }
        //2.存储每个非中转节点的前向中转节点
        System.out.println("saving forwardAccessNode shortPath table...");
        for(V v : graph.vertexSet()){
            Set<V> forwardAccessNodes = findForwardAccessNode(v);
            Hashtable<V, Path> hashtable = new Hashtable<V, Path>();
            for(V w : forwardAccessNodes){
                hashtable.put(w,ch.getPath(v,w));
            }
            forwardAccessNodeTable.put(v, hashtable);
        }
        //3.存储每个非中转节点的后向中转节点
        System.out.println("saving backwardAccessNode shortPath table...");
        for(V v : graph.vertexSet()){
            Set<V> backwardAccessNodes = findBackwardAccessNode(v);
            Hashtable<V, Path> hashtable = new Hashtable<V, Path>();
            for(V w : backwardAccessNodes){
                hashtable.put(w,ch.getPath(w,v));
            }
            backwardAccessNodeTable.put(v, hashtable);
        }
    }

    /*
    一条有向边u->v为forwardEdge当且仅当order(u) < order(v)
     */
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

    /*
    从v发出的所有forwardEdge
     */
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

    /*
    指向v的所有非forwardEdge
     */
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

    /*
        BFS寻找前向Access结点
     */
    public Set<V> findForwardAccessNode(V v){
        Set<V> set = new HashSet<V>();
        Set<V> visitedNodes = new HashSet<V>();
        if(transNodes.contains(v)){
            set.add(v);
            return set;
        }
        Queue<V> queue = new LinkedList<V>();
        queue.add(v);
        while(!queue.isEmpty()){
            V vertex = queue.poll();
            visitedNodes.add(vertex);
            for(Edge<V> edge : getUpperEdgeList(vertex)){
                V w = edge.getTarget();
                if(transNodes.contains(w)){
                    set.add(w);
                } else {
                    if(!visitedNodes.contains(w)){
                        queue.add(w);
                    }
                }
            }
        }

        return set;
    }
    /*
            BFS寻找后向Access结点
     */
    public Set<V> findBackwardAccessNode(V v){
        Set<V> set = new HashSet<V>();
        Set<V> visitedNodes = new HashSet<V>();
        if(transNodes.contains(v)){
            set.add(v);
            return set;
        }
        Queue<V> queue = new LinkedList<V>();
        queue.add(v);
        while(!queue.isEmpty()){
            V vertex = queue.poll();
            visitedNodes.add(vertex);
            for(Edge<V> edge : getLowerEdgeList(vertex)){
                V w = edge.getSource();
                if(transNodes.contains(w)){
                    set.add(w);
                } else {
                    if(!visitedNodes.contains(w)){
                        queue.add(w);
                    }
                }
            }
        }
        return set;
    }

    @Override
    public ShortestPath getPath(V source, V sink) {
        return null;
    }

    @Override
    public List<V> getPathVertex(V source, V sink) {
        List<V> result = new ArrayList<V>();
        Hashtable<V, Path> sourceTable = forwardAccessNodeTable.get(source);
        Hashtable<V, Path> sinkTable = backwardAccessNodeTable.get(sink);
        if(isIntersect(sourceTable.keySet(),sinkTable.keySet())){
            //source和sink的最短路径可能不经过中转节点，使用ch算法计算最短路
            result = ch.getPathVertex(source, sink);
        } else {
            //使用dist(source,sink) = min(dist(source, T1) + dist(T1, T2) + dist(T2, sink))计算最短路径
            //TODO 使用path来标识路径而不是使用double
            Path shortestPath = new ShortestPath(null,null,0.0);
            for(V t1 : sourceTable.keySet()){
                for(V t2 : sinkTable.keySet()){
                    Path sourceToT1 = sourceTable.get(t1);
                    Path t1ToT2 = accessNodeDistanceTable.get(t1).get(t2);
                    Path t2ToSink = sinkTable.get(t2);
                    double distance = sourceToT1.getWeight() + t1ToT2.getWeight() + t2ToSink.getWeight();
                    if(shortestPath.getWeight() > distance){
                        try {
                            shortestPath = sourceToT1.append(t1ToT2).append(t2ToSink);
                            result = shortestPath.getVertexList();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public double getPathWeight(V source, V sink) {
        return 0;
    }

    private <O> boolean isIntersect(Set<O> set1, Set<O> set2){
        for(O o : set2){
            if(set1.contains(o)){
                return true;
            }
        }
        return false;
    }

}
