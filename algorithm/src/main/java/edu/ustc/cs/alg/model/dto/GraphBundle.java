package edu.ustc.cs.alg.model.dto;

import lombok.Data;
import org.jgrapht.graph.AbstractBaseGraph;
import routing.Edges;
import routing.Nodes;

/**
 * Created by zyb on 2017/4/10.
 */
@Data
public class GraphBundle {
    AbstractBaseGraph graph;
    Edges edges;
    Nodes nodes;
}
