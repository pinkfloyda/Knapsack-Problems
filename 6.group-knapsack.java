import java.util.*;
import java.io.*;

/*
Input format:

First line is two integers N and C, representing unique item group count and knapsack capacity, seperated by space
Then followed by lines reprenting group and item info:
   First line is group's item number M, then each of following M lines is two integers si and vi, reprenting each item's size and value

Output format:
One line of one integer, which is the final result: the possible max value
*/

public class Main {

    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        // Read N and C
        String[] s = in.readLine().split(" ");
        int N = Integer.parseInt(s[0]); // unique group count
        int C = Integer.parseInt(s[1]); // knapsack capacity

        List<List<Integer>> ss = new ArrayList<>(); // size array for each item
        List<List<Integer>> vs = new ArrayList<>(); // value array for each item

        // Read size and value for each item
        for (int i=0; i<N; i++) {
            ss.add(new ArrayList<>());
            vs.add(new ArrayList<>());
            int m = Integer.parseInt(in.readLine());
            for (int j=0; j<m; j++) {
                s = in.readLine().split(" ");
                ss.get(i).add(Integer.parseInt(s[0]));
                vs.get(i).add(Integer.parseInt(s[1]));
            }
        }

        // Output result
        System.out.println(knapsackDP(N, C, ss, vs));
    }

    private static int knapsackDP(int N, int C, List<List<Integer>> ss, List<List<Integer>> vs) {
        int[] dp = new int[C+1];
        for (int i=0; i<N; i++) {
            for (int j=C; j>=1; j--) {
                for (int k=0; k<ss.get(i).size(); k++) {
                    if (j>=ss.get(i).get(k)) {
                         dp[j] = Math.max(dp[j], dp[j-ss.get(i).get(k)]+vs.get(i).get(k));
                    }
                }
            }
        }
        return dp[C];
    }
}