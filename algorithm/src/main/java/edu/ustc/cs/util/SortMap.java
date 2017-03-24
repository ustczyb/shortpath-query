package edu.ustc.cs.util;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by zyb on 2017/3/23.
 * 根据value进行排序的map(treemap是根据key进行排序)
 */
public class SortMap<K ,V extends Comparable> {

    private HashMap<K, V> hashMap;
    private LinkedList<K> keyList;
    private LinkedList<V> valueList;

    public SortMap(){
        hashMap = new HashMap<K, V>();
        keyList = new LinkedList<K>();
        valueList = new LinkedList<V>();
    }

    public LinkedList<K> getKeyList(){
        return keyList;
    }

    public LinkedList<V> getValueList() {
        return valueList;
    }

    public boolean isEmpty(){
        return hashMap.isEmpty();
    }

    public V remove(K k){
        keyList.remove(k);
        valueList.remove(get(k));
        return hashMap.remove(k);
    }

    public V get(K k){
        return hashMap.get(k);
    }

    /*
    put方法需要保证key的唯一性
     */
    public void put(K k, V v){

        if(keyList.isEmpty()){
            hashMap.put(k,v);
            keyList.add(k);
            valueList.add(v);
        } else{
            int indexOfK = keyList.indexOf(k);
            if(indexOfK != -1){
                keyList.remove(k);
                valueList.remove(indexOfK);
            }
            int index = 0;
            for(V value : valueList){
                if(v.compareTo(value) < 0){
                    valueList.add(index, v);
                    keyList.add(index, k);
                    hashMap.put(k,v);
                    return;
                }
                index ++;
            }
            valueList.add(index, v);
            keyList.add(index, k);
        }
    }

    public K removeFirst(){
        K key = keyList.remove(0);
        hashMap.remove(key);
        valueList.remove(0);
        return key;
    }

    public static void main(String[] args) {
        SortMap<String, Double> sortMap = new SortMap<>();
        sortMap.put("a",1.0);
        sortMap.put("b",2.0);
        sortMap.put("c",0.5);
        sortMap.put("d",0.25);
        System.out.println(sortMap.getKeyList());
        System.out.println(sortMap.getValueList());
        sortMap.removeFirst();
        System.out.println(sortMap.getKeyList());
        System.out.println(sortMap.getValueList());
        sortMap.put("b",0.15);
        System.out.println(sortMap.getKeyList());
        System.out.println(sortMap.getValueList());
    }
}
