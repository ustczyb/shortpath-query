package edu.ustc.cs.model;

/**
 * Created by zyb on 2017/2/28.
 */
public interface Edge<V> {

    //边的终点
    V edgeTo();

    //边的长度
    Double length();
}
