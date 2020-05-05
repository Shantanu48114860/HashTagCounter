
# Problem description
A system is implemented to find the n most popular hashtags that appear on social media such as Facebook or Twitter. For the scope of this project hashtags will be given from an input file. Basic idea for the implementation is to use a max priority structure to find out the most popular hashtags.
Assumption: There will be a large number of hashtags appearing in the stream and I need to perform increase key operation many times. Max Fibonacci heap is recommended because it has an amortized complexity of O(1) for the increase key operation. I have implemented all Fibonacci heap functions discussed in class. For the hash table, existing implementation of Hashtable in the java.util package has been used.

# Implementation:
1. Max Fibonacci heap: Fibonacci heap is a data structure which is primarily used for priority queue operations. It has better amortized complexity than normal Binary heap and Binomial heap. The amortized time complexity for find_minimum, insert, decrese_key operations are constant. For a heap size of n, the amortized complexity of deleting an element is O(logn).
In this project, Fibonacci heap is used to keep track of the frequencies of hashtags.
2. Hash table: The key for the hash table is the hashtag, and the value is the pointer to the corresponding node in the Fibonacci heap.

# Programming Environment
This project is implemented in Java version 13.

# Execution:
Following are the required steps to execute the project:<br/>
• javac hashtagcounter.java <br/>
• java hashtagcounter <input_file_name> <output_file_name> <br/>
• If output file name is mentioned, view the output in the output file, else view the output
in the output_file.txt

# Detail Problem description
Detail problem description is written at the file: ./projectSpring2020.pdf

# Project Report:
Project report is written at the file: ./ProjectReport-converted.pdf

