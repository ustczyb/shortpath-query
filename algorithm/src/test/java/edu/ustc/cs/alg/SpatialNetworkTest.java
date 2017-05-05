package edu.ustc.cs.alg;

import edu.ustc.cs.alg.model.graph.SpatialNetwork;
import edu.ustc.cs.alg.model.path.ShortestPath;
import edu.ustc.cs.alg.model.vertex.VertexAdapter;
import edu.ustc.cs.alg.util.ReadBinaryFileUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * Created by zyb on 2017/4/23.
 */
public class SpatialNetworkTest {

    private SpatialNetwork spatialNetwork;

    @Before
    public void init() throws IOException, ClassNotFoundException {
        spatialNetwork = ReadBinaryFileUtil.file2graph("F:\\code\\Code\\UrbanGen\\dataset\\");
        //spatialNetwork.init();
        spatialNetwork.initFromFile("F:\\test");
    }

    @Test
    public void testWriteCH() throws IOException {
        spatialNetwork.writeCH("F:\\test");
    }

    @Test
    public void getPathTest() throws Exception {
        Hashtable<Long, VertexAdapter> table = spatialNetwork.getNodeTable();
//        Scanner in = new Scanner(System.in);
//
//            long sourceNum = in.nextLong();
//            long targetNum = in.nextLong();
            VertexAdapter source = table.get(new Long(13222));
            VertexAdapter target = table.get(new Long(12606));
            ShortestPath path = spatialNetwork.getPath(source, target);
            System.out.println(path.getVertexList());


    }

}
