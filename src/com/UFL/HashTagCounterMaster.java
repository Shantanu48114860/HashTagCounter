package com.UFL;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

class Util {
    public static String getTag(String line) {
        return line.split(" ")[0].substring(1, line.split(" ")[0].length());
    }

    public static int getFrequency(String line) {
        return Integer.parseInt(line.split(" ")[1]);
    }
}

public class HashTagCounterMaster {
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
