package edu.ustc.cs.alg.model.graph;

import com.sun.istack.internal.NotNull;
import edu.ustc.cs.alg.alg.CH;
import edu.ustc.cs.alg.alg.SILC;
import edu.ustc.cs.alg.model.path.ShortestPath;
import edu.ustc.cs.alg.model.vertex.VertexAdapter;
import lombok.Data;
import lombok.NonNull;
import org.jgrapht.Graph;
import org.jgrapht.graph.AbstractBaseGraph;
import routing.Node;

import java.util.Hashtable;
import java.util.List;

/**
 *
 * Created by zyb on 2017/2/22.
 */
@Data
public class SpatialNetwork {

    /*
        室外的平面路网
     */
    Graph graph;
    /*
        CH算法
     */
    CH ch;
    /*
        建筑物
     */
    List<Building> buildings;
    /*
        id和顶点的对应关系表
     */
    Hashtable<Long, VertexAdapter> nodeTable;
    /*
        id和building的对应关系表
     */
    Hashtable<Long, Building> buildingTable;

    public void init(){
        ch = new CH((AbstractBaseGraph) graph);
        ch.init();
    }

    public ShortestPath getPath(VertexAdapter source, VertexAdapter target){
        if(graph.containsVertex(source)){
            if(graph.containsVertex(target)){
                //室外->室外
                return ch.getPath(source,target);
            } else{
                //室外->室内
                return outdoorToIndoor(source, target);
            }
        } else{
            if(graph.containsVertex(target)){
                //室内->室外
                return outdoorToIndoor(target,source).reverse();
            } else{
                //室内->室内
                return indoorToIndoor(source, target);
            }
        }
    }


    public ShortestPath outdoorToIndoor(@NotNull VertexAdapter source, @NotNull VertexAdapter target){
        Building building = buildingTable.get(target.getId());
        List<VertexAdapter> transVertex = building.getExteriorDoors();
        ShortestPath result = null;
        for(VertexAdapter v : transVertex){
            try {
                ShortestPath path = (ShortestPath) ch.getPath(source, v).append(building.getPath(v,target));
                if(result == null || result.compareTo(path) > 0){
                    result = path;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public ShortestPath indoorToIndoor(VertexAdapter source, VertexAdapter target){

        Building sourceBuilding = buildingTable.get(source.getId());
        Building targetBuilding = buildingTable.get(target.getId());
        //两点在同一建筑物中
        if(sourceBuilding == targetBuilding){
            return sourceBuilding.getPath(source,target);
        } else{
            List<VertexAdapter> sourceList = sourceBuilding.getExteriorDoors();
            ShortestPath result = null;
            for(VertexAdapter v : sourceList){
                ShortestPath path = null;
                try {
                    path = (ShortestPath) sourceBuilding.getPath(source,v).append(outdoorToIndoor(v, target));
                    if(result == null || result.compareTo(path) > 0){
                        result = path;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    }
}
