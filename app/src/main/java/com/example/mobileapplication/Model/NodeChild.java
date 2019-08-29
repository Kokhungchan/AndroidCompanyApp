package com.example.mobileapplication.Model;


import com.example.mobileapplication.Model.Data;

import java.util.ArrayList;
import java.util.List;

public class NodeChild {

    private Data center;
    private List<Data> children;

    public NodeChild(Data center, List<Data> children){
        this.center = center;
        this.children = removeDuplicates(children);
    }

    public Data getCenter() {
        return center;
    }

    public List<Data> getChildren() {
        return children;
    }

    private static List<Data> removeDuplicates(List<Data> list) {
        List<Data> newList = new ArrayList();

        for(Data data: list) {
         if(!newList.contains(data)){
             newList.add(data);
         }
        }
        return newList;
    }

}
