package edu.ustc.cs.model.coordinate;

import com.sun.org.apache.regexp.internal.RE;
import edu.ustc.cs.model.vertex.Node;

import java.util.List;

/**
 * Created by zyb on 2017/3/21.
 */
public class Flat implements Region {

    private double x1,x2;
    private double y1,y2;

    public Flat(List<Node> list){
        y1 = x1 = Double.POSITIVE_INFINITY;
        y2 = x2 = Double.NEGATIVE_INFINITY;
        for(Node n : list){
            if(n.getX() < x1){
                x1 = n.getX();
            }
            if(n.getY() < y1){
                y1 = n.getY();
            }
            if(n.getX() > x2){
                x2 = n.getX();
            }
            if(n.getY() > y2){
                y2 = n.getY();
            }
        }
    }

    @Override
    public boolean isBelongTo(Node node) {
        return false;
    }

    @Override
    public boolean isBelongTo(Region region) {
        return false;
    }
}
