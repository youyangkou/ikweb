package com.kouyy.ikweb.tfidf;

import java.util.*;

/**
 * 对HashMap根据value值进行排序
 * @author kouyy
 */
public class RankMapUtil {

    /**
     * 对HashMap根据value值进行降序排序
     * @param map
     * @return
     */
    public static LinkedHashMap sortMapByValue(Map map){
        ArrayList<Map.Entry<String, Double>> entries = new ArrayList<Map.Entry<String, Double>>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> obj1 , Map.Entry<String, Double> obj2) {
                //o1 to o2升序   o2 to o1降序
                return obj2.getValue().compareTo(obj1.getValue());
            }
        });
        LinkedHashMap linkedHashMap=new LinkedHashMap();
        for( int i=0;i<map.keySet().size();i++){
            linkedHashMap.put(entries.get(i).getKey(),entries.get(i).getValue());
        }
        return linkedHashMap;
    }

    public static void main(String[] args) {
        HashMap<String, Double> map = new HashMap<>();
        map.put("a", 1.11);
        map.put("b", 2.50);
        map.put("c", 1.25);
        map.put("d", 1.00);
        LinkedHashMap linkedHashMap = sortMapByValue(map);
        Set<Map.Entry<String,Double>> set = linkedHashMap.entrySet();
        set.forEach(entry-> System.out.println(entry.getValue()));
    }


    }
