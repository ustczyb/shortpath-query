package edu.ustc.cs.alg.util;

import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

import java.util.HashMap;

/**
 * Created by zyb on 2017/3/28.
 */
public class FibonacciMap<K> {

    FibonacciHeap<K> fibonacciHeap = new FibonacciHeap<K>();
    HashMap<K, FibonacciHeapNode<K>> hashMap = new HashMap<K, FibonacciHeapNode<K>>();

    public boolean isEmpty(){
        return fibonacciHeap.isEmpty();
    }

    public void put(K key, double v){
        FibonacciHeapNode<K> keyNode = hashMap.get(key);
        if(keyNode != null){
            fibonacciHeap.delete(keyNode);
            hashMap.remove(key);
        }
        FibonacciHeapNode node = new FibonacciHeapNode(key);
        hashMap.put(key, node);
        fibonacciHeap.insert(node,v);
    }

    public K removeMin(){
        K result = fibonacciHeap.removeMin().getData();
        hashMap.remove(result);
        return result;
    }

    public static void main(String[] args) {
        FibonacciMap<String> fibonacciMap = new FibonacciMap<>();
        String b = "b";
        fibonacciMap.put("a",1.0);
        fibonacciMap.put(b,2.0);
        fibonacciMap.put("c",0.5);
        fibonacciMap.put("d",0.25);
        fibonacciMap.put(b, 0.15);
        while(!fibonacciMap.isEmpty()){
            System.out.println(fibonacciMap.removeMin());
        }
    }

}
