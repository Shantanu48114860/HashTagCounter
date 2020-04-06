package com.UFL;

public class hashtagcounter {

    public static void main(String[] args) {
        // write your code here
        String file = "/Users/shantanughosh/Desktop/Shantanu_MS/Spring_20/ADS/Project/Git_hub/HashTagCounter/src/sampleInput.txt";
        //String file = "/Users/shantanughosh/Desktop/Shantanu_MS/Spring_20/ADS/Project/Git_hub/HashTagCounter/src/input1.txt";

        // new HashTagCounterMaster().initiate(args[0]);
        new HashTagCounterMaster().initiate(file);
    }
}
