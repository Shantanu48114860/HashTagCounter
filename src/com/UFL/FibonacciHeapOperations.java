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

    public FibHeapNode increaseKey(FibHeapNode current, int new_key) {
        FibHeapNode parent = null;
        current.key = new_key;
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
        FibHeapNode node_to_delete = node_with_max_key;
        if (node_to_delete != null) {
            updateChildCount(node_to_delete);
            removeNodeFromRoot(node_to_delete);
        }
        return node_to_delete;
    }

    private void removeNodeFromRoot(FibHeapNode node_to_delete) {
        node_to_delete.left.right = node_to_delete.right;
        node_to_delete.right.left = node_to_delete.left;
        if (node_to_delete == node_to_delete.right) {
            node_with_max_key = null;
        } else {
            node_with_max_key = node_to_delete.right;
            pairwiseCombine();
        }
        no_of_nodes--;
    }

    private void updateChildCount(FibHeapNode node_to_delete) {
        int child_count = node_to_delete.degree_of_node;
        FibHeapNode child = node_to_delete.child;
        deleteNodes(child_count, child);

    }

    private void deleteNodes(int child_count, FibHeapNode child) {
        FibHeapNode temp_right;
        while (child_count > 0) {
            temp_right = child.right;
            child.left.right = child.right;
            child.right.left = child.left;
            child.left = node_with_max_key;
            child.right = node_with_max_key.right;
            node_with_max_key.right = child;
            child.right.left = child;
            child.parent = null;
            child = temp_right;
            child_count--;
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
        FibHeapNode max_key_node = travaseTree(node_with_max_key);
        while (numRoots > 0) {
            int degree = max_key_node.degree_of_node;
            FibHeapNode next = max_key_node.left;
            while (roots[degree] != null) { //if there exists a root node with same degree
                max_key_node = deleteRootWithSameDegree(roots, max_key_node, degree);
                roots[degree] = null;
                degree++;
            }
            roots[degree] = max_key_node;
            max_key_node = next;
            numRoots--;
        }
        return roots;
    }

    private FibHeapNode deleteRootWithSameDegree(FibHeapNode[] roots, FibHeapNode max_key_node, int degree) {
        FibHeapNode node_to_delete = roots[degree];
        if (max_key_node.key < node_to_delete.key) {
            FibHeapNode temp = node_to_delete;
            node_to_delete = max_key_node;
            max_key_node = temp;
        }

        // remove y from root list of heap
        removeYFromHeap(max_key_node, node_to_delete);
        return max_key_node;
    }
}
