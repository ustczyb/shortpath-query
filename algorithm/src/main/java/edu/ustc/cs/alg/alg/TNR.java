package edu.ustc.cs.alg.alg;

import com.sun.istack.internal.NotNull;
import edu.ustc.cs.alg.model.coordinate.Flat;
import edu.ustc.cs.alg.model.coordinate.Node;
import edu.ustc.cs.alg.model.edge.Edge;
import edu.ustc.cs.alg.model.edge.ShortCut;
import edu.ustc.cs.alg.model.graph.Block;
import edu.ustc.cs.alg.model.graph.SpatialNetwork;
import edu.ustc.cs.alg.model.path.ShortestPath;
import edu.ustc.cs.alg.model.vertex.VertexAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.*;

/**
 * Created by zyb on 2017/4/25.
 * 平面路网的TNR算法
 * 突然想到一个有趣的问题：Node类是图数据结构中的点，那么node中可否有一个域叫做numOfEdges(表示这个点边的数量)呢？
 * 答案显然是否定的，因为这个域是取决于node所在的图的。如果为了让这个域有效而把图也作为node的域对象，那么我认为这个设计是
 * 很糟糕的，因为如果我们需要取graph的子图时必须要新建对象而不能够复用原来的对象。
 */
@Getter
@Setter
public class TNR<V extends Node,E extends Edge> implements ShortestPathStrategy<Node, Edge> {

    protected DefaultDirectedWeightedGraph<Node, Edge> roadNetwork;
    private Hashtable<Long, Node> hashtable;
    protected Block[][] blocks;
    protected CH ch;
    //长
    protected long blockSize1;
    //宽
    protected long blockSize2;
    //n×n的分块数
    protected int n;

    private DijkstraShortestPath dijkstraShortestPath;

    public void init(){
        dijkstraShortestPath = new DijkstraShortestPath(roadNetwork);
        ch = new CH(roadNetwork);
        ch.init();
    }

    public TNR(){
        hashtable = new Hashtable<>();
        roadNetwork = new DefaultDirectedWeightedGraph<Node, Edge>(Edge.class);
    }

    public TNR(SpatialNetwork spatialNetwork){

    }

    /*
    flat即为大图graph的最小包含矩形
     */
    public TNR(Graph<Node, Edge<Node>> graph, Flat flat, int n){
        hashtable = new Hashtable<>();
        roadNetwork = new DefaultDirectedWeightedGraph<Node, Edge>(Edge.class);
        this.n = n;
        blockSize1 = flat.getSize1()/n;
        blockSize2 = flat.getSize2()/n;
        blocks = new Block[n][n];

        for(Node v : graph.vertexSet()){
            VertexAdapter vertexAdapter = (VertexAdapter)v;
            hashtable.put(vertexAdapter.getId(), vertexAdapter);
            int i = (int) (v.getX() / blockSize1);
            int j = (int) (v.getY() / blockSize2);
            Block block = blocks[i][j];
            if(block == null){
                block = new Block();
                Flat flat1 = Flat.builder().x1(i * blockSize1).y1(j * blockSize2).x2((i + 1) * blockSize1).y2((j + 1) * blockSize2).build();
                block.setFlat(flat1);
                blocks[i][j] = block;
            }
            Graph blockGraph = block.getGraph();
            blockGraph.addVertex(v);
            for(Edge edge : graph.edgesOf(v)){
                Node w = (Node) edge.getAnotherVertex(v);
                if(!block.contains(w)){
                    //有通向block外的边，即为中转结点
                    block.getTransNodes().add(v);
                    roadNetwork.addVertex(v);
                    roadNetwork.addVertex(w);
                    roadNetwork.addEdge(v, w, edge);
                } else{
                    blockGraph.addVertex(w);
                    blockGraph.addEdge(v, w, edge);
                }
            }
        }
        //在roadNetwork中添加一些边，保证其连通性
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                Block block = blocks[i][j];
                if(block != null){
                    block.init();
                    HashSet<Node> set = block.getTransNodes();
                    for(Node v : set){
                        for(Node w : set){
                            if(v != w){
                                if(!graph.containsEdge(v,w)){
                                    GraphPath graphPath = block.getGraphPath(v,w);
                                    if(graphPath == null){
                                        continue;
                                    }
                                    ShortCut<Node> shortCut = new ShortCut<>();
                                    shortCut.setSource(v);
                                    shortCut.setTarget(w);
                                    shortCut.setPath(graphPath.getVertexList());
                                    shortCut.setLength(graphPath.getWeight());
                                    roadNetwork.addEdge(v, w, shortCut);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Block belongTo(@NotNull Node node){
        int i = (int) (node.getX() / blockSize1);
        int j = (int) (node.getY() / blockSize2);
        if(i >= n || j >= n)
            return null;
        return blocks[i][j];
    }


    public TNR(String dirName){

    }

    public boolean containsEdge(long sourceId, long targetId){
        Node source = hashtable.get(sourceId);
        Node target = hashtable.get(targetId);
        return roadNetwork.containsEdge(source, target);
    }

    public Node getNode(long id){
        return hashtable.get(id);
    }

    public ShortestPath getPath(long sourceId, long targetId){
        Node source = hashtable.get(sourceId);
        Node target = hashtable.get(targetId);
        return getPath(source, target);
    }

    public ShortestPath getPath(Node source, Node target){
        Block sourceBlock = belongTo(source);
        Block targetBlock = belongTo(target);
        if(sourceBlock == targetBlock){
            return sourceBlock.getPath(source, target);
        }
        ShortestPath result = null;
        for(Node sourceTransNode : sourceBlock.getTransNodes()){
            for(Node targetTransNode : targetBlock.getTransNodes()){
                ShortestPath path1 = sourceBlock.getPath(source, sourceTransNode);
                GraphPath graphPath = dijkstraShortestPath.getPath(sourceTransNode, targetTransNode);
                ShortestPath path2 = ch.getPath(sourceTransNode, targetTransNode);
                ShortestPath path3 = targetBlock.getPath(targetTransNode, target);
                if(path1 == null || path2 == null || path3 == null){
                    continue;
                }
                try {
                    ShortestPath path = (ShortestPath) path1.append(path2).append(path3);
                    if(result == null || result.compareTo(path) > 0){
                        result = path;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    public List<Node> getPathVertex(Node source, Node sink) {
        return getPath(source,sink).getVertexList();
    }

    @Override
    public double getPathWeight(Node source, Node sink) {
        return getPath(source, sink).getWeight();
    }
}
