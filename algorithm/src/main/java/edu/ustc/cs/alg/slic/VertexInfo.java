package edu.ustc.cs.alg.slic;

import java.util.HashSet;

/**
 * Created by zyb on 2017/3/7.
 */
public class VertexInfo<V> {
    V v;
    HashSet<V> set;

    public V getV() {
        return v;
    }

    public void setV(V v) {
        this.v = v;
    }

    public HashSet<V> getSet() {
        return set;
    }

    public void setSet(HashSet<V> set) {
        this.set = set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VertexInfo<?> that = (VertexInfo<?>) o;

        if (!v.equals(that.v)) return false;
        return set.equals(that.set);
    }

    @Override
    public int hashCode() {
        int result = v.hashCode();
        result = 31 * result + set.hashCode();
        return result;
    }
}
