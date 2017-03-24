package edu.ustc.cs.model.dto;

/**
 * Created by zyb on 2017/3/23.
 */
public class Priority<V> implements Comparable {

    /*
    顶点信息
     */
    private V v;
    /*
    顶点的优先级
     */
    private double priority;

    public Priority(V v, double priority) {
        this.v = v;
        this.priority = priority;
    }

    public V getV() {
        return v;
    }

    public double getPriority() {
        return priority;
    }

    @Override
    public int compareTo(Object o) {
        Priority another = (Priority)o;
        if(this.priority > another.getPriority()){
            return 1;
        } else if(this.priority < another.getPriority()){
            return -1;
        } else {
            return 0;
        }
    }
}
