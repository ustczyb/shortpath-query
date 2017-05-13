package edu.ustc.cs.alg;

import edu.ustc.cs.alg.alg.SpatialTNR;
import edu.ustc.cs.alg.alg.TNR;
import edu.ustc.cs.alg.model.coordinate.Flat;
import edu.ustc.cs.alg.model.coordinate.Node;
import edu.ustc.cs.alg.model.edge.Edge;
import edu.ustc.cs.alg.model.graph.SpatialNetwork;
import edu.ustc.cs.alg.model.path.ShortestPath;
import edu.ustc.cs.alg.util.ReadBinaryFileUtil;
import org.jgrapht.Graph;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by zyb on 2017/5/4.
 */
public class SpatialTNRTest {
    SpatialNetwork spatialNetwork;
    SpatialTNR tnr;
    Flat flat;

    @Before
    public void init() throws FileNotFoundException {
        spatialNetwork = ReadBinaryFileUtil.file2graph("dataset/");
        flat = Flat.builder().x1(0l).x2(22000l).y1(0l).y2(31000l).build();
        long startTime = System.currentTimeMillis();
        tnr = new SpatialTNR(spatialNetwork,flat,10);
        tnr.init();
        long endTime = System.currentTimeMillis();
        System.out.println("init time : " + (endTime - startTime));
    }

    @Test
    public void testInit(){
        System.out.println(tnr.getNetwork().vertexSet().size());
        System.out.println(tnr.getNetwork().edgeSet().size());
    }

    @Test
    public void testSP(){
        long sourceId = 7442l;
        long targetId = 162674l;
//        Scanner scanner = new Scanner(System.in);
//        while(true){
//            sourceId = scanner.nextLong();
//            targetId = scanner.nextLong();
            long startTime = System.currentTimeMillis();
            ShortestPath sp = tnr.getPath(sourceId, targetId);
            long endTime = System.currentTimeMillis();
            System.out.println("query time : " + (endTime - startTime));
            System.out.println(sp.getVertexList());
//        }

    }
}
