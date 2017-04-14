package edu.ustc.cs.alg.model.coordinate;

import edu.ustc.cs.alg.model.vertex.Point;

/**
 * Created by zyb on 2017/3/21.
 */
public interface Region {
    /*
    点是否在该区间中
     */
    public boolean isBelongTo(Point node);
    /*
    区间是否为该区间的子区间
     */
    public boolean isBelongTo(Region region);
}
