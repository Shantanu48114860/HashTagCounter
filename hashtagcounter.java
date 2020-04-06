import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Starting point
 */
public class hashtagcounter {
    /**
     * Main method
     *
     * @param args
     */
    public static void main(String[] args) {
        // write your code here
        //String file = "/Users/shantanughosh/Desktop/Shantanu_MS/Spring_20/ADS/Project/Git_hub/HashTagCounter/src/sampleInput.txt";
        //String file = "/Users/shantanughosh/Desktop/Shantanu_MS/Spring_20/ADS/Project/Git_hub/HashTagCounter/src/input1.txt";
        if (args.length == 0) {
            System.out.println("Enter valid argument");
        } else if (args.length == 1) {
            new HashTagCounterBL().initiateCounter(args[0], "");
        } else if (args.length == 2) {
            new HashTagCounterBL().initiateCounter(args[0], args[1]);
        }
    }
}

/**
 * Node Structure of Fibonacci Heap
 */
class FibHeapNode {
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

    /**
     * getter and setter methods
     */
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

/**
 * Utility Class
 */
class Util {
    public static String getTag(String line) {
        return line.split(" ")[0].substring(1, line.split(" ")[0].length());
    }

    public static int getFrequency(String line) {
        return Integer.parseInt(line.split(" ")[1]);
    }

    public static int getQueryStr(String line) {
        return Integer.parseInt(line);
    }

    public static String getSubString(String str_op){
        return str_op.substring(0, str_op.length() - 1);
    }
}

/**
 * Business logic of finding n most popular hastags
 */
class HashTagCounterBL {
    File file_out = new File("output_file.txt");
    StringBuilder sb = new StringBuilder();
    ArrayList<FibHeapNode> node_removed_store = new ArrayList<FibHeapNode>();
    FibonacciHeapOperations heap_operations = new FibonacciHeapOperations();
    Hashtable<String, FibHeapNode> fib_store = new Hashtable<String, FibHeapNode>();

    /**
     * Initiate the Fibonacci Heap
     *
     * @param input_file
     * @param output_file_name
     */
    public void initiateCounter(String input_file, String output_file_name) {
        if (output_file_name != "") {
            file_out = new File(output_file_name);
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(input_file));
            String input;
            while ((input = reader.readLine()) != null) {
                if (input.startsWith("#")) {
                    createNewNode(Util.getTag(input), Util.getFrequency(input));
                } else if (input.equalsIgnoreCase("stop")) {
                    writeToOutput(sb.toString());
                    return;
                } else {
                    queryHeap(input);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Write to the output_file.txt
     *
     * @param output_line
     * @throws FileNotFoundException
     */
    private void writeToOutput(String str_out) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(file_out);
        try {
            byte[] bytesArray = str_out.getBytes();
            fos.write(bytesArray);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Create new node
     *
     * @param hash_tag
     * @param frequency
     */
    private void createNewNode(String hash_tag, int frequency) {
        if (!fib_store.containsKey(hash_tag)) {
            insertNew(hash_tag, frequency);
        } else {
            int new_frequency = fib_store.get(hash_tag).getKey() + frequency;
            increseKey(hash_tag, new_frequency);
        }
    }

    /**
     * Perform Incresekey operation
     *
     * @param hash_tag
     * @param newFrequency
     */
    private void increseKey(String hash_tag, int updated_frequency) {
        FibHeapNode incresed_key = heap_operations.increaseKey(fib_store.get(hash_tag),
                updated_frequency);
        fib_store.remove(hash_tag);
        fib_store.put(hash_tag, incresed_key);
    }

    /**
     * Insert a new tag
     *
     * @param hash_tag
     * @param frequency
     */
    private void insertNew(String hash_tag, int frequency) {
        FibHeapNode new_node = new FibHeapNode();
        new_node.setKey(frequency);
        new_node.setTag(hash_tag);
        heap_operations.insert(new_node, frequency);
        fib_store.put(hash_tag, new_node);
    }

    /**
     * Query the Fibbonacci Heap with given query
     *
     * @param str_query
     */
    private void queryHeap(String str_query) {
        int key_qr = 0;
        try {
            key_qr = Util.getQueryStr(str_query);
        } catch (NumberFormatException e) {
            e.getStackTrace();
        }

        String str_op = removeMaxNode(key_qr);
        str_op = Util.getSubString(str_op);
        sb.append(str_op);
        sb.append(System.getProperty("line.separator"));
        addBackRemovedNode();
    }

    private void addBackRemovedNode() {
        for (int count = 0; count < node_removed_store.size(); count++) {
            heap_operations.insert(node_removed_store.get(count),
                    node_removed_store.get(count).getKey());
            fib_store.put(node_removed_store.get(count).getTag(),
                    node_removed_store.get(count));
        }
        node_removed_store.clear();
    }

    /**
     * Remove the node from the heap with Max key from the top and
     * the perform pairwise comparison and addition
     *
     * @param query
     * @return
     */
    private String removeMaxNode(int qr_str) {
        String str_op = "";
        while (qr_str > 0) {
            String tag_in_max_node = getTagForMaxNode();
            fib_store.remove(tag_in_max_node);
            str_op = str_op + tag_in_max_node + ",";
            qr_str--;
        }
        return str_op;
    }

    /**
     * Get the tag for the node to be deleted with max key
     *
     * @return
     */
    private String getTagForMaxNode() {
        //remove max node from fibonacci heap
        FibHeapNode max_node = heap_operations.removeMax();
        FibHeapNode removed_node = new FibHeapNode();
        removed_node.setKey(max_node.getKey());
        removed_node.setTag(max_node.getTag());
        node_removed_store.add(removed_node);
        return removedNode.getTag();
    }
}

/**
 * Class where all the operations of a Fibbonacci heap are implemented
 */
class FibonacciHeapOperations {
    private FibHeapNode node_with_max_key;
    private int no_of_nodes;
    int numRoots = 0;

    /**
     * Insert a new node in the Fibbonacci Heap
     *
     * @param new_node
     * @param key
     */
    public void insert(FibHeapNode new_node, int key) {
        new_node.key = key;
        if (node_with_max_key != null) {
            addNode(new_node, key);
        } else {
            node_with_max_key = new_node;
        }
        no_of_nodes++;
    }

    /**
     * Remove the node with maximum key
     *
     * @return
     */
    public FibHeapNode removeMax() {
        FibHeapNode node_to_delete = node_with_max_key;
        if (node_to_delete != null) {
            updateChildCount(node_to_delete);
            removeNodeFromRoot(node_to_delete);
        }
        return node_to_delete;
    }

    /**
     * Increse Key
     *
     * @param current
     * @param new_key
     * @return
     */
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

    /**
     * Util method to remove arbitary node from the Heap
     *
     * @param x
     * @param y
     */
    private void removeArbitaryNodeFromHeap(FibHeapNode temp1, FibHeapNode temp2) {
        temp2.left.right = temp2.right;
        temp2.right.left = temp2.left;
        temp2.parent = temp1;
        if (temp1.child == null) {
            temp1.child = temp2;
            temp2.right = temp2;
            temp2.left = temp2;
        } else {
            temp2.left = temp1.child;
            temp2.right = temp1.child.right;
            temp1.child.right = temp2;
            temp2.right.left = temp2;
        }
        temp1.degree_of_node++;
        temp2.child_cut = false;
    }

    /**
     * Delete nodes with same degree
     *
     * @param roots
     * @param max_key_node
     * @param degree
     * @return
     */
    private FibHeapNode deleteRootWithSameDegree(FibHeapNode[] roots, FibHeapNode max_key_node, int degree) {
        FibHeapNode node_to_delete = roots[degree];
        if (max_key_node.key < node_to_delete.key) {
            FibHeapNode temp = node_to_delete;
            node_to_delete = max_key_node;
            max_key_node = temp;
        }

        removeArbitaryNodeFromHeap(max_key_node, node_to_delete);
        return max_key_node;
    }

    /**
     * Perform Delete Nodes
     *
     * @param child_count
     * @param child
     */
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
     * Reconstruct nodes during pairwise combine
     *
     * @param arraySize
     * @param roots
     */
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

    /**
     * Add given node during insertion operation of Fibbonacci Heap
     *
     * @param new_node
     * @param key
     */
    private void addNode(FibHeapNode new_node, int key) {
        new_node.left = node_with_max_key;
        new_node.right = node_with_max_key.right;
        node_with_max_key.right = new_node;
        new_node.right.left = new_node;
        if (key > node_with_max_key.key) {
            node_with_max_key = new_node;
        }
    }

    /**
     * Perform removal of node from Fibonacci heap
     *
     * @param node_to_delete
     */
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

    /**
     * Update the degree of the child during removeMax operation of Fibonacci Heap
     *
     * @param node_to_delete
     */
    private void updateChildCount(FibHeapNode node_to_delete) {
        int child_count = node_to_delete.degree_of_node;
        FibHeapNode child = node_to_delete.child;
        deleteNodes(child_count, child);

    }

    /**
     * Perform the ChildCut operation
     *
     * @param child
     * @param parent
     */
    private void cut(FibHeapNode child, FibHeapNode parent) {
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
     * Cascade the childcut operation
     *
     * @param child
     */
    private void cascadeCut(FibHeapNode child) {
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
     * Performs the pairwise combine operation
     */
    private void pairwiseCombine() {
        int arraySize = no_of_nodes + 1;
        FibHeapNode[] roots = new FibHeapNode[arraySize];
        for (int i = 0; i < arraySize; i++) {
            roots[i] = null;
        }

        roots = rootNumList(roots);
        node_with_max_key = null;
        reconstruct_root_list(arraySize, roots);
    }

    /**
     * Travarse the Fibonacci Heap
     *
     * @param node_with_max_key
     * @return
     */
    private FibHeapNode travarseTree(FibHeapNode node_with_max_key) {
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

    /**
     * Uitlity method to remove a node with same degree
     *
     * @param roots
     * @return
     */
    private FibHeapNode[] rootNumList(FibHeapNode[] roots) {
        FibHeapNode max_key_node = travarseTree(node_with_max_key);
        while (numRoots > 0) {
            int degree = max_key_node.degree_of_node;
            FibHeapNode next = max_key_node.left;
            while (roots[degree] != null) {
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
}
