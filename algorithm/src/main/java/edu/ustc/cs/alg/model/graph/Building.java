package edu.ustc.cs.alg.model.graph;

import edu.ustc.cs.alg.alg.SILC;
import edu.ustc.cs.alg.model.dto.EdgeAdapter;
import edu.ustc.cs.alg.model.path.ShortestPath;
import edu.ustc.cs.alg.model.vertex.VertexAdapter;
import lombok.Getter;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import routing.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * 建筑物
 * Created by zyb on 2017/4/17.
 */
@Getter
public class Building {

    /*
        室内图
     */
    private Graph<VertexAdapter,EdgeAdapter> graph;
    /*
        建筑物和路网的交集
     */
    private List<VertexAdapter> exteriorDoors;

    private DijkstraShortestPath<VertexAdapter, EdgeAdapter> dijkstraShortestPath;

    public Building(){
        graph = new DefaultDirectedWeightedGraph<VertexAdapter, EdgeAdapter>(EdgeAdapter.class);
        exteriorDoors = new ArrayList<>();
    }

    public void init(){
        dijkstraShortestPath = new DijkstraShortestPath<VertexAdapter, EdgeAdapter>(graph);
    }

    public ShortestPath getPath(VertexAdapter source, VertexAdapter target){
        GraphPath graphPath = dijkstraShortestPath.getPath(source, target);
        ShortestPath shortestPath = new ShortestPath(graphPath.getVertexList(),graphPath.getEdgeList(),graphPath.getWeight());
        return shortestPath;
    }

}
