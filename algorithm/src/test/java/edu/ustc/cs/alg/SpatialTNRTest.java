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

/**
 * Created by zyb on 2017/5/4.
 */
public class SpatialTNRTest {
    SpatialNetwork spatialNetwork;
    SpatialTNR tnr;
    Flat flat;

    @Before
    public void init() throws FileNotFoundException {
        spatialNetwork = ReadBinaryFileUtil.file2graph("F:\\code\\Code\\UrbanGen\\dataset\\");
        flat = Flat.builder().x1(0l).x2(22000l).y1(0l).y2(31000l).build();
        tnr = new SpatialTNR(spatialNetwork,flat,20);
        tnr.init();
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
        ShortestPath sp = tnr.getPath(sourceId, targetId);
        System.out.println(sp.getVertexList());
    }
}
