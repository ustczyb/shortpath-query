package edu.ustc.cs.alg.model.graph;

import com.sun.istack.internal.NotNull;
import edu.ustc.cs.alg.alg.CH;
import edu.ustc.cs.alg.alg.SILC;
import edu.ustc.cs.alg.model.edge.Edge;
import edu.ustc.cs.alg.model.edge.ShortCut;
import edu.ustc.cs.alg.model.path.ShortestPath;
import edu.ustc.cs.alg.model.vertex.VertexAdapter;
import lombok.Data;
import lombok.NonNull;
import org.jgrapht.Graph;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import routing.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 *
 * Created by zyb on 2017/2/22.
 */
@Data
public class SpatialNetwork implements Serializable {

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

    public void writeCH(String dirName) throws IOException {
        ch.writeObject(dirName);
    }

    public void initFromFile(String dirName) throws IOException, ClassNotFoundException {
        ch = CH.readObject(dirName);
    }

    public void prehandle(){
        int count = 0;
        for(Building building : buildings){
            building.init();
            List<VertexAdapter> list = building.getExteriorDoors();
            for(VertexAdapter v : list){
                for(VertexAdapter w : list){
                    ShortestPath path = building.getPath(v,w);
                    if(path == null){
                        continue;
                    }
                    ShortCut<edu.ustc.cs.alg.model.coordinate.Node> shortCut = new ShortCut<>();
                    shortCut.setSource(v);
                    shortCut.setTarget(w);
                    shortCut.setLength(path.getWeight());
                    shortCut.setPath(path.getVertexList());
                    graph.addEdge(v,w,shortCut);
                    count++;
                }
            }
        }
        System.out.println("prehandle edge add:" + count);
    }

    public void init(){
        ch = new CH((AbstractBaseGraph) graph);
        ch.init();
    }

    public ShortestPath getPath(VertexAdapter source, VertexAdapter target) throws Exception {
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


    public ShortestPath outdoorToIndoor(@NotNull VertexAdapter source, @NotNull VertexAdapter target) throws Exception {
        //因为一个building可能不是一个联通图，因此building内的寻路可能为空，因此添加运行时异常处理
        Building building = buildingTable.get(target.getId());
        if(building.getDijkstraShortestPath() == null){
            building.init();
        }
        List<VertexAdapter> transVertex = building.getExteriorDoors();
        ShortestPath result = null;
        for(VertexAdapter v : transVertex){
            try{
                ShortestPath pathIndoor = building.getPath(v,target);
                ShortestPath pathOutdoor = ch.getPath(source, v);
                ShortestPath path = (ShortestPath) pathOutdoor.append(pathIndoor);
                if(result == null || result.compareTo(path) > 0){
                    result = path;
                }
            } catch (RuntimeException e){
                continue;
            }
        }
        return result;
    }

    public List<VertexAdapter> getOrder(){
        List<VertexAdapter> order = ch.getOrder();
        for(Building building : buildings){
            List<VertexAdapter> buildingOrder = building.getOrder();
            order.addAll(buildingOrder);
        }
        return order;
    }

    public ShortestPath indoorToIndoor(VertexAdapter source, VertexAdapter target){

        Building sourceBuilding = buildingTable.get(source.getId());
        Building targetBuilding = buildingTable.get(target.getId());
        if(sourceBuilding.getDijkstraShortestPath() == null){
            sourceBuilding.init();
        }
        if(targetBuilding.getDijkstraShortestPath() == null){
            targetBuilding.init();
        }
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
                    continue;
                }
            }
            return result;
        }
    }
}
