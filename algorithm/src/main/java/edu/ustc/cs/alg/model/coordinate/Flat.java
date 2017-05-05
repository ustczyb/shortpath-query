package edu.ustc.cs.alg.model.coordinate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by zyb on 2017/4/25.
 */
@Getter
@Setter
@Builder
public class Flat {
    long x1,x2;
    long y1,y2;

    public long getSize1(){
        return x2 - x1;
    }
    public long getSize2(){
        return y2 - y1;
    }

    public Node getCenter(){
        return new Node() {
            @Override
            public long getX() {
                return (x1 + x2) / 2;
            }

            @Override
            public long getY() {
                return (y1 + y2) / 2;
            }
        };
    }

    public boolean contain(Node node){
        long x = node.getX();
        long y = node.getY();
        if(x >= x2 || x < x1){
            return false;
        }
        if(y >= y2 || y < y1){
            return false;
        }
        return true;
    }
}
