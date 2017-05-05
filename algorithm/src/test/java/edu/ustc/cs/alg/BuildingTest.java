package edu.ustc.cs.alg;

import edu.ustc.cs.alg.model.edge.EdgeAdapter;
import edu.ustc.cs.alg.model.graph.Building;
import edu.ustc.cs.alg.model.path.ShortestPath;
import edu.ustc.cs.alg.model.vertex.VertexAdapter;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * Created by zyb on 2017/4/23.
 */
public class BuildingTest {
    private Building building;
    private Hashtable<Long, VertexAdapter> nodeTable;
    @Before
    public void init() throws FileNotFoundException {
        Scanner nodeScanner = new Scanner(new File("F:\\test\\buildingNode.txt"));
        Scanner edgeScanner = new Scanner(new File("F:\\test\\buildingEdge.txt"));
        building = new Building();
        nodeTable = new Hashtable<>();
        while(nodeScanner.hasNext()) {
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
            } else{
                vertex.setId(Long.valueOf(lineInfo[1]));
                vertex.setX(Integer.valueOf(lineInfo[2]));
                vertex.setY(Integer.valueOf(lineInfo[3]));
                vertex.setFlag(Short.valueOf(lineInfo[4]));
                vertex.setFloor(Short.valueOf(lineInfo[5]));
                vertex.setRoomNum(Short.valueOf(lineInfo[6]));
                building.getExteriorDoors().add(vertex);
            }
            building.getGraph().addVertex(vertex);
            nodeTable.put(vertex.getId(), vertex);
        }
        nodeScanner.close();
        int count = 0;
        while(edgeScanner.hasNext()){
            count ++;
            String line = edgeScanner.nextLine();
            String[] lineInfo = line.split(" ");
            EdgeAdapter edge = new EdgeAdapter();
            if(Boolean.valueOf(lineInfo[0])){
                String name = lineInfo[1];
                edge.setSource(nodeTable.get(Long.valueOf(lineInfo[2])));
                edge.setTarget(nodeTable.get(Long.valueOf(lineInfo[3])));
                edge.setId(Long.valueOf(lineInfo[4]));
                edge.setLength(Double.valueOf(lineInfo[5]));
                building.getGraph().addEdge(edge.getSource(),edge.getTarget(),edge);
                building.getGraph().addEdge(edge.getSource(),edge.getTarget(),edge.reverse());

            } else{
                //建筑物之间相连的边
                edge.setSource(nodeTable.get(Long.valueOf(lineInfo[1])));
                edge.setTarget(nodeTable.get(Long.valueOf(lineInfo[2])));
                edge.setId(Long.valueOf(lineInfo[3]));
                edge.setLength(Double.valueOf(lineInfo[4]));
                building.getGraph().addEdge(edge.getSource(),edge.getTarget(),edge);
                building.getGraph().addEdge(edge.getSource(),edge.getTarget(),edge.reverse());
            }
        }
        edgeScanner.close();
        building.init();
    }

    @Test
    public void testGetPath(){
        VertexAdapter source = nodeTable.get(new Long(7447));
        VertexAdapter target = nodeTable.get(new Long(7449));
        System.out.println(building.getGraph().containsVertex(source));
        ShortestPath sp = building.getPath(source, target);
        System.out.println(sp.getVertexList());
    }
}
