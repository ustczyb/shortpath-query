package edu.ustc.cs.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zyb on 2017/5/13.
 */
public class Test1 extends Test {
    public Set<Integer> generate(){
        Set<Integer> set = new HashSet<>(10);
        for(int i = 10; i < 16; i++){
            set.add(i);
        }
        return set;
    }

    public static void main(String[] args) {
        Test1 test1 = new Test1();
        test1.print();
    }
}
