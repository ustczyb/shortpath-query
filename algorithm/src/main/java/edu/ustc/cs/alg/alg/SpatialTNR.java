package edu.ustc.cs.alg.alg;

import com.sun.istack.internal.NotNull;
import edu.ustc.cs.alg.model.coordinate.Flat;
import edu.ustc.cs.alg.model.coordinate.Node;
import edu.ustc.cs.alg.model.edge.Edge;
import edu.ustc.cs.alg.model.edge.ShortCut;
import edu.ustc.cs.alg.model.graph.Block;
import edu.ustc.cs.alg.model.graph.Building;
import edu.ustc.cs.alg.model.graph.SpatialNetwork;
import edu.ustc.cs.alg.model.path.ShortestPath;
import edu.ustc.cs.alg.model.vertex.VertexAdapter;
import edu.ustc.cs.alg.util.GraphUtil;
import lombok.Getter;
import lombok.Setter;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by zyb on 2017/5/2.
 * 含建筑物路网的TNR算法
 */
@Getter
@Setter
public class SpatialTNR {

    protected DefaultDirectedWeightedGraph<Node, Edge> network;
    private Hashtable<Long, VertexAdapter> hashtable;
    protected Block[][] blocks;
    protected CH ch;
    //长
    protected long blockSize1;
    //宽
    protected long blockSize2;
    //n×n的分块数
    protected int n;

    private Hashtable<Long, Building> buildingHashtable;

    private DijkstraShortestPath<Node,Edge> dijkstraShortestPath;

    Hashtable<Building, Block> buildingBlockHashtable;


    public SpatialTNR(SpatialNetwork spatialNetwork, Flat flat , int n){
        spatialNetwork.prehandle();
        hashtable = spatialNetwork.getNodeTable();
        buildingHashtable = spatialNetwork.getBuildingTable();
        buildingBlockHashtable = new Hashtable<>();
        network = new DefaultDirectedWeightedGraph<Node, Edge>(Edge.class);
        this.n = n;
        blockSize1 = flat.getSize1()/n;
        blockSize2 = flat.getSize2()/n;
        blocks = new Block[n][n];
        Graph<Node,Edge> roadGraph = spatialNetwork.getGraph();

        //添加平面道路网络
        for(Node v : roadGraph.vertexSet()){
            VertexAdapter vertexAdapter = (VertexAdapter)v;
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
            for(Edge edge : roadGraph.edgesOf(v)){
                Node w = (Node) edge.getAnotherVertex(v);
                if(!block.contains(w)){
                    //有通向block外的边，即为中转结点
                    block.getTransNodes().add(v);
                    network.addVertex(v);
                    network.addVertex(w);
                    network.addEdge(v, w, edge);
                } else{
                    blockGraph.addVertex(w);
                    blockGraph.addEdge(v, w, edge);
                }
            }
        }

        //添加建筑
        List<Building> buildings = spatialNetwork.getBuildings();
        for(Building building : buildings){
            Node buildingNode = building.getFlat().getCenter();
            int i = (int) (buildingNode.getX() / blockSize1);
            int j = (int) (buildingNode.getY() / blockSize2);
            Block block = blocks[i][j];
            if(block == null){
                block = new Block();
                Flat flat1 = Flat.builder().x1(i * blockSize1).y1(j * blockSize2).x2((i + 1) * blockSize1).y2((j + 1) * blockSize2).build();
                block.setFlat(flat1);
                blocks[i][j] = block;
            }
            buildingBlockHashtable.put(building,block);
            //添加building到所属的block中
            Graph<Node,Edge> graph = block.getGraph();
            for(Node node : building.getGraph().vertexSet()){
                graph.addVertex(node);
            }
            for(Edge<Node> edge : building.getGraph().edgeSet()){
                graph.addEdge(edge.getSource(),edge.getTarget(),edge);
            }
            //判断building中的transit node是否为block中的transit node
            for(VertexAdapter v : building.getExteriorDoors()){
                if(!block.contains(v)){
                    block.getTransNodes().add(v);
                }
            }
        }

        //在network中添加一些边，保证其连通性
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                Block block = blocks[i][j];
                if(block != null){
                    block.init();
                    HashSet<Node> set = block.getTransNodes();
                    for(Node v : set){
                        for(Node w : set){
                            if(v != w){
                                if(!roadGraph.containsEdge(v,w)){
                                    GraphPath graphPath = block.getGraphPath(v,w);
                                    if(graphPath == null){
                                        continue;
                                    }
                                    ShortCut<Node> shortCut = new ShortCut<>();
                                    shortCut.setSource(v);
                                    shortCut.setTarget(w);
                                    shortCut.setPath(graphPath.getVertexList());
                                    shortCut.setLength(graphPath.getWeight());
                                    network.addEdge(v, w, shortCut);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public void init(){
//        ch = new CH(network);
//        ch.init();
        dijkstraShortestPath = new DijkstraShortestPath<Node, Edge>(network);
    }

    private Block belongTo(@NotNull Node node){
        VertexAdapter vertexAdapter = (VertexAdapter) node;
        if(vertexAdapter.getFlag() == 0){
            int i = (int) (node.getX() / blockSize1);
            int j = (int) (node.getY() / blockSize2);
            if(i >= n || j >= n)
                return null;
            return blocks[i][j];
        } else{
            Building building = buildingHashtable.get(vertexAdapter.getId());
            return buildingBlockHashtable.get(building);
        }

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
           //     try{

                    ShortestPath path1 = sourceBlock.getPath(source, sourceTransNode);
                    GraphPath graphPath = dijkstraShortestPath.getPath(sourceTransNode, targetTransNode);
                    ShortestPath path2 = GraphUtil.graphPath2SP(graphPath);
                    ShortestPath path3 = targetBlock.getPath(targetTransNode, target);
                    if(path1 == null || path2 == null || path3 == null){
                        continue;
                    }

                ShortestPath path = null;
                try {
                    path = (ShortestPath) path1.append(path2).append(path3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(result == null || result.compareTo(path) > 0){
                        result = path;
                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                   // continue;
//                }
            }
        }
        return result;
    }

}
