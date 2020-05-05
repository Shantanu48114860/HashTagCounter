
# Problem description
A system is implemented to find the n most popular hashtags that appear on social media such as Facebook or Twitter. For the scope of this project hashtags will be given from an input file. Basic idea for the implementation is to use a max priority structure to find out the most popular hashtags.
Assumption: There will be a large number of hashtags appearing in the stream and I need to perform increase key operation many times. Max Fibonacci heap is recommended because it has an amortized complexity of O(1) for the increase key operation. I have implemented all Fibonacci heap functions discussed in class. For the hash table, existing implementation of Hashtable in the java.util package has been used.

# Programming Environment
This project is implemented in Java version 13.

# Execution:
Following are the required steps to execute the project:
• javac hashtagcounter.java
• java hashtagcounter <input_file_name> <output_file_name>
• If output file name is mentioned, view the output in the output file, else view the output
in the output_file.txt

