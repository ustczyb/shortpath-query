package edu.ustc.cs.util;

import edu.ustc.cs.alg.alg.CH;
import edu.ustc.cs.alg.util.ReadBinaryFileUtil;
import org.jgrapht.graph.AbstractBaseGraph;
import org.junit.Test;


import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by zyb on 2017/4/10.
 */
public class ReadBinaryFileTest {
    @Test
    public void testEdgeRead() throws IOException {
        DataInputStream in = new DataInputStream(new FileInputStream("F:\\code\\Code\\UrbanGen\\dataset\\SanJoaquin\\Originalnetwork.edge"));
        long id1 = in.readLong();
        long id2 = in.readLong();
        byte len = in.readByte();
        System.out.println(id1);
        System.out.println(id2);
        System.out.println(len);
        if (len > 0) {
            byte[] data = new byte[len];
            in.readFully (data);
            System.out.println(String.valueOf(data));
            long eID = in.readLong();
            int eClass = in.readInt();
            System.out.println(eID);
            System.out.println(eClass);
        }
        else {
            long eID = in.readLong();
            int eClass = in.readInt();
            System.out.println(eID);
            System.out.println(eClass);
        }
    }

    @Test
    public void testNodeRead() throws IOException {
        DataInputStream in = new DataInputStream(new FileInputStream("F:\\code\\Code\\UrbanGen\\dataset\\SanJoaquin\\Originalnetwork.node"));
        byte len = in.readByte();
        System.out.println(len);
        if (len > 0) {
            byte[] data = new byte[len];
            in.readFully (data);
            long pID = in.readLong();
            int x = in.readInt();
            int y = in.readInt();
        }
        else {
            long pID = in.readLong();
            int x = in.readInt();
            int y = in.readInt();
        }
    }
}
