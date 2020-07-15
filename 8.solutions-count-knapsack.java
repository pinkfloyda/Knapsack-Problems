import java.util.*;
import java.io.*;

/*
Input format:

First line is two integers N and C, representing unique item count and knapsack capacity, seperated by space
Following N lines where each line is two integers si and vi, representing ith item's size and value

Output format:
One line of one integer, which is the final result: the number of optimized solutions
*/

public class Main {

    private static final int M = 1000000007;

    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        // Read N and C
        String[] s = in.readLine().split(" ");
        int N = Integer.parseInt(s[0]); // unique item count
        int C = Integer.parseInt(s[1]); // knapsack capacity
        int[] ss = new int[N]; // size array for each item
        int[] vs = new int[N]; // value array for each item

        // Read size and value for each item
        for (int i=0; i<N; i++) {
            s = in.readLine().split(" ");
            ss[i] = Integer.parseInt(s[0]);
            vs[i] = Integer.parseInt(s[1]);
        }

        // Output result
        System.out.println(knapsackDPCount(N, C, ss, vs));
    }

    // For solution counts, it is another dp status, for each item, if both picking and not-picking got the same values
    // Then it has two branches of solutions to choose from, so up to this point, counts(i, c) = counts(i-1, c) + counts(i-1, c-ci)
    // Similarly for cases picking <> not-picking, to get maximum value can only choose one route, so counts(i, c) = counts(i-1, c) or counts(i-1, c-ci)
    // depending on which value is bigger.
    //
    // Similar to the value dp, can optmize to use 1D array for updating
    private static int knapsackDPCount(int N, int C, int[] ss, int[] vs) {
        int[] dp = new int[C+1];
        int[] counts = new int[C+1];
        Arrays.fill(counts, 1);

        for (int i=0; i<N; i++) {
            for (int j=C; j>=ss[i]; j--) {
                int notPickValue = dp[j];
                int pickValue = dp[j-ss[i]]+vs[i];
                if (pickValue > notPickValue) {
                    counts[j] = counts[j-ss[i]] % M;
                } else if (notPickValue == pickValue) {
                    counts[j] = (counts[j] + (counts[j-ss[i]])) % M;
                }
                dp[j] = Math.max(notPickValue, pickValue);
            }
        }

        return counts[C];
    }
}