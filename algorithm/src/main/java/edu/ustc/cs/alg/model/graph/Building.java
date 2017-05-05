package edu.ustc.cs.alg.model.graph;

import edu.ustc.cs.alg.alg.CH;
import edu.ustc.cs.alg.model.coordinate.Flat;
import edu.ustc.cs.alg.model.edge.EdgeAdapter;
import edu.ustc.cs.alg.model.path.ShortestPath;
import edu.ustc.cs.alg.model.vertex.VertexAdapter;
import lombok.Getter;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

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
    private DefaultDirectedWeightedGraph<VertexAdapter,EdgeAdapter> graph;
    /*
        建筑物和路网的交集
     */
    private List<VertexAdapter> exteriorDoors;

    private Flat flat;

    private DijkstraShortestPath<VertexAdapter, EdgeAdapter> dijkstraShortestPath;

    public Building(){
        graph = new DefaultDirectedWeightedGraph<VertexAdapter, EdgeAdapter>(EdgeAdapter.class);
        exteriorDoors = new ArrayList<>();
    }

    public Flat getFlat(){
        if(flat == null){
            long x1 = Integer.MAX_VALUE,x2 = 0l,y1 = Integer.MAX_VALUE,y2 = 0l;
            for(VertexAdapter v : graph.vertexSet()){
                long x = v.getX();
                long y = v.getY();
                if(x < x1){
                    x1 = x;
                }
                if(x > x2){
                    x2 = x;
                }
                if(y < y1){
                    y1 = y;
                }
                if(y > y2){
                    y2 = y;
                }
            }
            flat = Flat.builder().x1(x1).x2(x2).y1(y1).y2(y2).build();
        }
        return flat;
    }

    public List<VertexAdapter> getOrder(){
        CH ch = new CH(graph);
        ch.init();
        List<VertexAdapter> order = ch.getOrder();
        order.removeAll(exteriorDoors);
        return order;
    }

    public void init(){
        dijkstraShortestPath = new DijkstraShortestPath<VertexAdapter, EdgeAdapter>(graph);
    }

    public ShortestPath getPath(VertexAdapter source, VertexAdapter target){
        GraphPath graphPath = dijkstraShortestPath.getPath(source, target);
        if(graphPath == null){
            return null;
        }
        List<VertexAdapter> vertexAdapterList = graphPath.getVertexList();
        ShortestPath shortestPath = new ShortestPath(vertexAdapterList,graphPath.getEdgeList(),graphPath.getWeight());
        return shortestPath;
    }

}
