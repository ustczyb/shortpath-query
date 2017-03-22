package edu.ustc.cs.model.coordinate;

import edu.ustc.cs.model.vertex.Node;

/**
 * Created by zyb on 2017/3/21.
 */
public interface Region {
    /*
    点是否在该区间中
     */
    public boolean isBelongTo(Node node);
    /*
    区间是否为该区间的子区间
     */
    public boolean isBelongTo(Region region);
}
