package edu.ustc.cs.alg.model.vertex;

import edu.ustc.cs.alg.model.coordinate.Node;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by zyb on 2017/2/22.
 */

@Getter
@Setter
@ToString
public class VertexAdapter implements Serializable, Node {

    private static long serialVersionUID = 7275943455794999920l;

    private String name;
    private long id;
    private int x;
    private int y;
    private short flag;
    private short floor;
    private short roomNum;

    @Override
    public long getX() {
        return x;
    }

    @Override
    public long getY() {
        return y;
    }
}
