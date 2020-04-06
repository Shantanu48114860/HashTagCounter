import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class hashtagcounter {

    public static void main(String[] args) {
        // write your code here
        //String file = "/Users/shantanughosh/Desktop/Shantanu_MS/Spring_20/ADS/Project/Git_hub/HashTagCounter/src/sampleInput.txt";
        //String file = "/Users/shantanughosh/Desktop/Shantanu_MS/Spring_20/ADS/Project/Git_hub/HashTagCounter/src/input1.txt";

        new HashTagCounterMaster().initiate(args[0]);
    }
}

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


class Util {
    public static String getTag(String line) {
        return line.split(" ")[0].substring(1, line.split(" ")[0].length());
    }

    public static int getFrequency(String line) {
        return Integer.parseInt(line.split(" ")[1]);
    }
}

class HashTagCounterMaster {
    //output file
    File output = new File("output_file.txt");
    //ArrayList to store removed Nodes
    ArrayList<FibHeapNode> nodes_deleted = new ArrayList<FibHeapNode>();
    //Max Fibonacci heap
    FibonacciHeapOperations heap_operations = new FibonacciHeapOperations();
    //Hash table
    Hashtable<String, FibHeapNode> fib_store = new Hashtable<String, FibHeapNode>();

    void initiate(String input_file) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(input_file));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("#")) {
                    createNewNode(Util.getTag(line), Util.getFrequency(line));
                } else if (line.equalsIgnoreCase("stop")) {
                    return;
                } else {
                    process(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNewNode(String hash_tag, int frequency) {
        if (!fib_store.containsKey(hash_tag)) {
            insertNew(hash_tag, frequency);
        } else {
            int new_frequency = fib_store.get(hash_tag).getKey() + frequency;
            increseKey(hash_tag, new_frequency);
        }
    }

    private void increseKey(String hash_tag, int newFrequency) {
        FibHeapNode incresed_key = heap_operations.increaseKey(fib_store.get(hash_tag), newFrequency);
        fib_store.remove(hash_tag);
        fib_store.put(hash_tag, incresed_key);

    }

    private void insertNew(String hash_tag, int frequency) {
        FibHeapNode new_node = new FibHeapNode();
        new_node.setKey(frequency);
        new_node.setTag(hash_tag);
        heap_operations.insert(new_node, frequency);
        fib_store.put(hash_tag, new_node);
    }

    private void writeFile(File output_file, String output_line) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(output_file, true));
            bw.write(output_line);
            bw.newLine();
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Generates the Max Fibonacci heap and calls removeMax() for
     * query number of times.
     *
     * @param str_query
     */
    void process(String str_query) {
        int query = 0;
        try {
            query = Integer.parseInt(str_query);
        } catch (NumberFormatException e) {
            e.getStackTrace();
        }

        String output_line = removeMaxNode(query);
        writeFile(output, output_line.substring(0, output_line.length() - 1));
        addBackRemovedNode();

    }

    private void addBackRemovedNode() {
        for (int count = 0; count < nodes_deleted.size(); count++) {
            heap_operations.insert(nodes_deleted.get(count), nodes_deleted.get(count).getKey());
            fib_store.put(nodes_deleted.get(count).getTag(), nodes_deleted.get(count));
        }
        nodes_deleted.clear();
    }

    private String removeMaxNode(int query) {
        String output_line = "";
        while (query > 0) {
            String tag_in_max_node = getTagForMaxNode();
            fib_store.remove(tag_in_max_node);
            output_line = output_line + tag_in_max_node + ",";
            query--;
        }
        return output_line;
    }

    private String getTagForMaxNode() {
        //remove max node from fibonacci heap
        FibHeapNode max_node = heap_operations.removeMax();
        FibHeapNode removedNode = new FibHeapNode();
        removedNode.setKey(max_node.getKey());
        removedNode.setTag(max_node.getTag());
        nodes_deleted.add(removedNode);
        return removedNode.getTag();
    }
}

class FibonacciHeapOperations {
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
