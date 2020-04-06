package com.UFL;

public class FibHeapNode {
    FibHeapNode child;
    FibHeapNode left;
    FibHeapNode parent;
    FibHeapNode right;
    private String tag;
    int key;
    int degree_of_node;
    boolean child_cut;


    public FibHeapNode() {
        right = this;
        left = this;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public final int getKey() {
        return key;
    }
}
