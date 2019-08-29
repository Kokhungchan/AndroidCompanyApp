package com.example.mobileapplication.Model;

import com.example.mobileapplication.Model.Data;

public class Node {

    private Data data;
    private float x;
    private float y;

    public Node(Data data,float x, float y) {
        this.data = data;
        this.x = x;
        this.y = y;
    }

    public Data getData() {
        return data;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
