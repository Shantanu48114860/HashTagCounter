package com.UFL;

public class FibonacciHeapOperations {
    /** to store the maximum node in Fibonacci heap. */
    private FibHeapNode maxNode;
    /** to store the number of nodes in Fibonacci heap. */
    private int nodeCount;

    /**
     * Function isHeapEmpty returns TRUE if the heap is empty
     * and FALSE otherwise.
     * @return
     */
    public boolean isHeapEmpty() {
        if(maxNode == null)
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }

    /**
     * Function to inserts nodes into Fibonacci heap.
     * @param newNode
     * @param key
     */
    public void insert(FibHeapNode newNode, int key) {
        newNode.key = key;
        //Heap is not empty
        if(maxNode != null) {
            newNode.left = maxNode;
            newNode.right = maxNode.right;
            maxNode.right = newNode;
            newNode.right.left = newNode;
            //update maxNode with newNode
            if(key > maxNode.key) {
                maxNode = newNode;
            }
        }
        //Heap is empty.
        else {
            maxNode = newNode;
        }
        //Incrementing the node count after insertion.
        nodeCount++;
    }

    /**
     * Increases the key of the given node
     * by the value passed.
     * @param current
     * @param newKey
     * @return FibHeapNode
     */
    public FibHeapNode increaseKey(FibHeapNode current, int newKey) {
        FibHeapNode parent=null;
        current.key = newKey;
        parent = current.parent;
        //If the current node's key is greater than that of its parent
        //then call cut and cascadeCut to preserve
        //Max Fibonacci heap properties
        if((parent != null) && (current.key > parent.key)) {
            cut(current, parent);
            cascadeCut(parent);
        }
        //Updating the maxNode with current node if maxNode is greater
        //than that of current node
        if(current.key > maxNode.key) {
            maxNode = current;
        }
        return current;
    }

    /**
     * Removes and returns the maxNode from the
     * Max Fibonacci heap
     * @return FibHeapNode
     */
    public FibHeapNode removeMax() {
        FibHeapNode nodeToDelete = maxNode;
        if(nodeToDelete != null) {
            int  childCount = nodeToDelete.degree_of_node;
            FibHeapNode child = nodeToDelete.child;
            FibHeapNode tempRight;
            // for each child of the node to delete...
            while(childCount > 0) {
                tempRight = child.right;
                //remove child from child list
                child.left.right = child.right;
                child.right.left = child.left;
                //add the child to root list
                child.left = maxNode;
                child.right = maxNode.right;
                maxNode.right = child;
                child.right.left = child;
                //set the parent of child to null
                child.parent = null;
                child = tempRight;
                childCount--;
            }
            //remove the node to delete from the root list
            nodeToDelete.left.right = nodeToDelete.right;
            nodeToDelete.right.left = nodeToDelete.left;
            if(nodeToDelete == nodeToDelete.right) {
                maxNode = null;
            }
            else {
                maxNode = nodeToDelete.right;
                pairwiseCombine();
            }
            //decrement the heap size
            nodeCount--;
        }
        //return the removed maxNode
        return nodeToDelete;
    }

    /**
     * removes child from the child list of parent.
     * @param child
     * @param parent
     */
    protected void cut(FibHeapNode child, FibHeapNode parent) {
        //remove the node from the child list of parent
        child.left.right = child.right;
        child.right.left = child.left;
        parent.degree_of_node--;
        //set the child of parent to appropriate node
        if(parent.child == child) {
            parent.child = child.right;
        }
        if(parent.degree_of_node == 0) {
            parent.child = null;
        }
        //add the child to the root list of heap
        child.left = maxNode;
        child.right = maxNode.right;
        maxNode.right = child;
        child.right.left = child;
        child.parent = null;
        child.child_cut = false;
    }

    /**
     * Cuts the child from its parent till a parent with
     * child_cut value FALSE is encountered.
     * @param child
     */
    protected void cascadeCut(FibHeapNode child) {
        FibHeapNode parent = child.parent;
        if(parent != null) {
            if(!child.child_cut) {
                child.child_cut = true;
            }
            else {
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
        int arraySize = nodeCount + 1;
        FibHeapNode[] roots = new FibHeapNode[arraySize];
        for(int i = 0; i < arraySize; i++) {
            roots[i] = null;
        }
        // Find the number of root nodes.
        int numRoots = 0;
        FibHeapNode x = maxNode;

        if(x != null) {
            numRoots++;
            x = x.left;

            while(x != maxNode) {
                numRoots++;
                x = x.left;
            }
        }
        while(numRoots > 0) {
            int d = x.degree_of_node;
            FibHeapNode next = x.left;
            while(roots[d] != null) { //if there exists a root node with same degree
                FibHeapNode y = roots[d];
                if(x.key < y.key) {
                    FibHeapNode temp = y;
                    y = x;
                    x = temp;
                }

                // remove y from root list of heap
                y.left.right = y.right;
                y.right.left = y.left;
                y.parent = x;
                if(x.child == null) {
                    x.child = y;
                    y.right = y;
                    y.left = y;
                }
                else {
                    y.left = x.child;
                    y.right = x.child.right;
                    x.child.right = y;
                    y.right.left = y;
                }
                //Increment the degree of the x
                x.degree_of_node++;
                //Set the child_cut of y to FALSE
                y.child_cut = false;

                roots[d] = null;
                d++;
            }
            roots[d] = x;
            x = next;
            numRoots--;
        }
        maxNode = null;
        // Reconstruct the root list from the array entries in roots[].
        for(int i = 0; i < arraySize; i++) {
            if(roots[i] != null) {
                if(maxNode != null) {
                    roots[i].left.right = roots[i].right;
                    roots[i].right.left = roots[i].left;
                    roots[i].left = maxNode;
                    roots[i].right = maxNode.right;
                    maxNode.right = roots[i];
                    roots[i].right.left = roots[i];
                    //Setting the maxNode
                    if(roots[i].key > maxNode.key) {
                        maxNode = roots[i];
                    }
                }
                else {
                    maxNode = roots[i];
                }
            }
        }
    }
}
