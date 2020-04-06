package com.UFL;

public class FibonacciHeapOperations {
    private FibHeapNode node_with_max_key;
    private int no_of_nodes;
    int numRoots = 0;

    public void insert(FibHeapNode new_node, int key) {
        new_node.key = key;
        if (node_with_max_key != null) {
            addNode(new_node, key);
        } else {
            node_with_max_key = new_node;
        }
        no_of_nodes++;
    }

    private void addNode(FibHeapNode new_node, int key) {
        new_node.left = node_with_max_key;
        new_node.right = node_with_max_key.right;
        node_with_max_key.right = new_node;
        new_node.right.left = new_node;
        if (key > node_with_max_key.key) {
            node_with_max_key = new_node;
        }
    }

    public FibHeapNode increaseKey(FibHeapNode current, int newKey) {
        FibHeapNode parent = null;
        current.key = newKey;
        parent = current.parent;
        if ((parent != null) && (current.key > parent.key)) {
            cut(current, parent);
            cascadeCut(parent);
        }
        if (current.key > node_with_max_key.key) {
            node_with_max_key = current;
        }
        return current;
    }

    public FibHeapNode removeMax() {
        FibHeapNode nodeToDelete = node_with_max_key;
        if (nodeToDelete != null) {
            updateChildCount(nodeToDelete);
            removeNodeFromRoot(nodeToDelete);
        }
        return nodeToDelete;
    }

    private void removeNodeFromRoot(FibHeapNode nodeToDelete) {
        nodeToDelete.left.right = nodeToDelete.right;
        nodeToDelete.right.left = nodeToDelete.left;
        if (nodeToDelete == nodeToDelete.right) {
            node_with_max_key = null;
        } else {
            node_with_max_key = nodeToDelete.right;
            pairwiseCombine();
        }
        no_of_nodes--;
    }

    private void updateChildCount(FibHeapNode nodeToDelete) {
        int childCount = nodeToDelete.degree_of_node;
        FibHeapNode child = nodeToDelete.child;
        deleteNodes(childCount, child);

    }

    private void deleteNodes(int childCount, FibHeapNode child) {
        FibHeapNode tempRight;
        while (childCount > 0) {
            tempRight = child.right;
            child.left.right = child.right;
            child.right.left = child.left;
            child.left = node_with_max_key;
            child.right = node_with_max_key.right;
            node_with_max_key.right = child;
            child.right.left = child;
            child.parent = null;
            child = tempRight;
            childCount--;
        }
    }

    /**
     * removes child from the child list of parent.
     *
     * @param child
     * @param parent
     */
    protected void cut(FibHeapNode child, FibHeapNode parent) {
        //remove the node from the child list of parent
        child.left.right = child.right;
        child.right.left = child.left;
        parent.degree_of_node--;
        if (parent.child == child) {
            parent.child = child.right;
        }
        if (parent.degree_of_node == 0) {
            parent.child = null;
        }
        child.left = node_with_max_key;
        child.right = node_with_max_key.right;
        node_with_max_key.right = child;
        child.right.left = child;
        child.parent = null;
        child.child_cut = false;
    }

    /**
     * Cuts the child from its parent till a parent with
     * child_cut value FALSE is encountered.
     *
     * @param child
     */
    protected void cascadeCut(FibHeapNode child) {
        FibHeapNode parent = child.parent;
        if (parent != null) {
            if (!child.child_cut) {
                child.child_cut = true;
            } else {
                cut(child, parent);
                cascadeCut(parent);
            }
        }
    }

    /**
     * Combines the trees in the heap by joining trees of equal degree
     * until there are no more trees of equal degree in the root list.
     */
    protected void pairwiseCombine() {
        int arraySize = no_of_nodes + 1;
        FibHeapNode[] roots = new FibHeapNode[arraySize];
        for (int i = 0; i < arraySize; i++) {
            roots[i] = null;
        }

        roots = rootNumList(roots);
        node_with_max_key = null;
        reconstruct_root_list(arraySize, roots);
    }

    private void reconstruct_root_list(int arraySize, FibHeapNode[] roots) {
        for (int i = 0; i < arraySize; i++) {
            if (roots[i] != null) {
                if (node_with_max_key != null) {
                    roots[i].left.right = roots[i].right;
                    roots[i].right.left = roots[i].left;
                    roots[i].left = node_with_max_key;
                    roots[i].right = node_with_max_key.right;
                    node_with_max_key.right = roots[i];
                    roots[i].right.left = roots[i];
                    //Setting the node_with_max_key
                    if (roots[i].key > node_with_max_key.key) {
                        node_with_max_key = roots[i];
                    }
                } else {
                    node_with_max_key = roots[i];
                }
            }
        }
    }

    private FibHeapNode travaseTree(FibHeapNode node_with_max_key) {
        FibHeapNode x = node_with_max_key;
        if (x != null) {
            numRoots++;
            x = x.left;
            while (x != node_with_max_key) {
                numRoots++;
                x = x.left;
            }
        }
        return x;
    }

    private void removeYFromHeap(FibHeapNode x, FibHeapNode y) {
        y.left.right = y.right;
        y.right.left = y.left;
        y.parent = x;
        if (x.child == null) {
            x.child = y;
            y.right = y;
            y.left = y;
        } else {
            y.left = x.child;
            y.right = x.child.right;
            x.child.right = y;
            y.right.left = y;
        }
        x.degree_of_node++;
        y.child_cut = false;
    }

    private FibHeapNode[] rootNumList(FibHeapNode[] roots) {
        FibHeapNode x = travaseTree(node_with_max_key);
        while (numRoots > 0) {
            int d = x.degree_of_node;
            FibHeapNode next = x.left;
            while (roots[d] != null) { //if there exists a root node with same degree
                FibHeapNode y = roots[d];
                if (x.key < y.key) {
                    FibHeapNode temp = y;
                    y = x;
                    x = temp;
                }

                // remove y from root list of heap
                removeYFromHeap(x, y);
                roots[d] = null;
                d++;
            }
            roots[d] = x;
            x = next;
            numRoots--;
        }
        return roots;
    }
}
