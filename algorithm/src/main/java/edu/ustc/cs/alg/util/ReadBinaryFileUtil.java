package edu.ustc.cs.alg.util;


import edu.ustc.cs.alg.model.edge.EdgeAdapter;
import edu.ustc.cs.alg.model.graph.*;
import edu.ustc.cs.alg.model.vertex.VertexAdapter;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.io.*;
import java.util.*;


/**
 * Created by zyb on 2017/4/7.
 */
public class ReadBinaryFileUtil {

    public static SpatialNetwork file2graph(String dirName) throws FileNotFoundException {

        Scanner nodeScanner = new Scanner(new File(dirName + "node.data"));
        Scanner edgeScanner = new Scanner(new File(dirName + "edge.data"));

        Graph<VertexAdapter,EdgeAdapter> graph = new DefaultDirectedWeightedGraph(EdgeAdapter.class);
        List<edu.ustc.cs.alg.model.graph.Building> buildings = new ArrayList<>();
        Hashtable<Long, VertexAdapter> nodeTable = new Hashtable<>();
        Hashtable<Long, edu.ustc.cs.alg.model.graph.Building> buildingTable = new Hashtable<>();

        //读取顶点信息
        boolean flag = false;            //用来记录是否需要添加新的building
        //1.构建室外图
        while(nodeScanner.hasNext()){
            String line = nodeScanner.nextLine();
            //nodeline的数据依次为:hasName,(name),id,x,y,flag,floor,roomId
            String[] lineInfo = line.split(" ");
            VertexAdapter vertex = new VertexAdapter();
            if(Boolean.valueOf(lineInfo[0])){
                vertex.setName(lineInfo[1]);
                vertex.setId(Long.valueOf(lineInfo[2]));
                vertex.setX(Integer.valueOf(lineInfo[3]));
                vertex.setY(Integer.valueOf(lineInfo[4]));
                vertex.setFlag(Short.valueOf(lineInfo[5]));
                vertex.setFloor(Short.valueOf(lineInfo[6]));
                vertex.setRoomNum(Short.valueOf(lineInfo[7]));
                if(lineInfo[1].equals("*")){
                    //室外图
                    graph.addVertex(vertex);
                } else if(flag){
                    //flag为true时，表明这个点仍然在上一个建筑物中，直接添加即可
                    Building building = buildings.get(buildings.size()-1);
                    building.getGraph().addVertex(vertex);
                    buildingTable.put(vertex.getId(), building);
                } else{
                    //flag为false时，表明这个点不在上一个建筑物中，新建新的建筑物
                    Building building = new Building();
                    building.getGraph().addVertex(vertex);
                    flag = true;
                    buildings.add(building);
                    buildingTable.put(vertex.getId(), building);
                }
            } else{
                //没有name的点即为室内和室外相交的部分，出现false说明这个建筑物即将结束，在下一个true时建立新的building
                vertex.setId(Long.valueOf(lineInfo[1]));
                vertex.setX(Integer.valueOf(lineInfo[2]));
                vertex.setY(Integer.valueOf(lineInfo[3]));
                vertex.setFlag(Short.valueOf(lineInfo[4]));
                vertex.setFloor(Short.valueOf(lineInfo[5]));
                vertex.setRoomNum(Short.valueOf(lineInfo[6]));
                flag = false;
                graph.addVertex(vertex);
                Building building = buildings.get(buildings.size()-1);
                building.getGraph().addVertex(vertex);
                buildingTable.put(vertex.getId(), building);
                building.getExteriorDoors().add(vertex);
            }
            nodeTable.put(vertex.getId(),vertex);
        }
        nodeScanner.close();
        //读取边信息
        while(edgeScanner.hasNext()){
            String line = edgeScanner.nextLine();
            String[] lineInfo = line.split(" ");
            EdgeAdapter edge = new EdgeAdapter();
            if(Boolean.valueOf(lineInfo[0])){
                String name = lineInfo[1];
                edge.setSource(nodeTable.get(Long.valueOf(lineInfo[2])));
                edge.setTarget(nodeTable.get(Long.valueOf(lineInfo[3])));
                edge.setId(Long.valueOf(lineInfo[4]));
                edge.setLength(Double.valueOf(lineInfo[5]));
                if(name.equals("*") && graph.containsVertex(edge.getTarget())){
                    //室外图
                    graph.addEdge(edge.getSource(),edge.getTarget(),edge);
                    graph.addEdge(edge.getTarget(),edge.getSource(),edge.reverse());
                } else{
                    //室内图或室内室外相连的边
                    Building building = buildingTable.get(edge.getTarget().getId());
                    building.getGraph().addEdge(edge.getSource(),edge.getTarget(),edge);
                    building.getGraph().addEdge(edge.getTarget(),edge.getSource(),edge.reverse());
                }
            } else{
                //建筑物之间相连的边
                edge.setSource(nodeTable.get(Long.valueOf(lineInfo[1])));
                edge.setTarget(nodeTable.get(Long.valueOf(lineInfo[2])));
                edge.setId(Long.valueOf(lineInfo[3]));
                edge.setLength(Double.valueOf(lineInfo[4]));
                Building building = buildingTable.get(edge.getTarget().getId());
                building.getGraph().addEdge(edge.getSource(),edge.getTarget(),edge);
                building.getGraph().addEdge(edge.getTarget(),edge.getSource(),edge.reverse());

            }
        }
        edgeScanner.close();

        SpatialNetwork spatialNetwork = new SpatialNetwork();
        spatialNetwork.setBuildings(buildings);
        spatialNetwork.setBuildingTable(buildingTable);
        spatialNetwork.setGraph(graph);
        spatialNetwork.setNodeTable(nodeTable);
        return spatialNetwork;
    }

    public static void readNode(){

    }

    public static void readEdge(){

    }

    public static void main(String[] args) throws IOException {
        String path = "F:\\code\\Code\\UrbanGen\\dataset\\";
        SpatialNetwork network = file2graph(path);
        System.out.println(network.getBuildings().size());
    }
}
