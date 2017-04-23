package edu.ustc.cs.alg;

import edu.ustc.cs.alg.model.graph.SpatialNetwork;
import edu.ustc.cs.alg.model.path.ShortestPath;
import edu.ustc.cs.alg.model.vertex.VertexAdapter;
import edu.ustc.cs.alg.util.ReadBinaryFileUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Hashtable;

/**
 * Created by zyb on 2017/4/23.
 */
public class SpatialNetworkTest {

    private SpatialNetwork spatialNetwork;

    @Before
    public void init() throws FileNotFoundException {
        spatialNetwork = ReadBinaryFileUtil.file2graph("F:\\code\\Code\\UrbanGen\\dataset\\");
        spatialNetwork.init();
    }

    @Test
    public void getPathTest(){
        Hashtable<Long, VertexAdapter> table = spatialNetwork.getNodeTable();
        VertexAdapter source = table.get(new Long(7442));
        VertexAdapter target = table.get(new Long(7381));
        ShortestPath path = spatialNetwork.getPath(source, target);
        System.out.println(path.getVertexList());
    }

}
