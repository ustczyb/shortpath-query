package edu.ustc.cs.alg.model.graph;

import edu.ustc.cs.alg.model.coordinate.Flat;
import edu.ustc.cs.alg.model.edge.Edge;
import edu.ustc.cs.alg.model.coordinate.Node;
import edu.ustc.cs.alg.model.path.ShortestPath;
import lombok.Getter;
import lombok.Setter;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by zyb on 2017/4/25.
 */
@Getter
@Setter
public class Block {

    private DefaultDirectedWeightedGraph<Node, Edge> graph;
    private HashSet<Node> transNodes;
    private DijkstraShortestPath dijkstraShortestPath;
    private Flat flat;

    public Block(){
        graph = new DefaultDirectedWeightedGraph<Node, Edge>(Edge.class);
        transNodes = new HashSet<>();
    }

    public void init(){
        dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    public GraphPath getGraphPath(Node source, Node target){
        return dijkstraShortestPath.getPath(source,target);
    }

    public ShortestPath getPath(Node source, Node target){
        GraphPath path = getGraphPath(source, target);
        if(path == null){
    //        System.out.println("block sp null:" + source + " " + target);
            return null;
        }
        return new ShortestPath(path.getVertexList(),path.getEdgeList(),path.getWeight());
    }

    public boolean contains(Node node){
        return flat.contain(node);
    }

}
