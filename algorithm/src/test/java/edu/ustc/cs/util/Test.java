package edu.ustc.cs.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zyb on 2017/5/13.
 */
public class Test {
    public void print(){
        testPrivate();
        System.out.println(generate());
    }

    private void testPrivate(){
        System.out.println("testPrivate");
        System.out.println(generate());
    }

    public Set<Integer> generate(){
        Set<Integer> set = new HashSet<>(10);
        for(int i = 0; i < 6; i++){
            set.add(i);
        }
        return set;
    }
}
