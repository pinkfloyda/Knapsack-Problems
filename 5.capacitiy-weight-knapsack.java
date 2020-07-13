import java.util.*;
import java.io.*;

/*
Input format:

First line is three integers N, C and W, representing unique item count, knapsack capacity and knapsack weight
Following N lines where each line is three integers si, wi and vi, representing ith item's size, weight and value

Output format:
One line of one integer, which is the final result: the possible max value
*/

public class Main {

    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        // Read N, C and W
        String[] s = in.readLine().split(" ");
        int N = Integer.parseInt(s[0]); // unique item count
        int C = Integer.parseInt(s[1]); // knapsack capacity
        int W = Integer.parseInt(s[2]); // knapsack weight

        int[] ss = new int[N]; // size array for each item
        int[] ws = new int[N]; // weight array for each item
        int[] vs = new int[N]; // value array for each item

        // Read size and value for each item
        for (int i=0; i<N; i++) {
            s = in.readLine().split(" ");
            ss[i] = Integer.parseInt(s[0]);
            ws[i] = Integer.parseInt(s[1]);
            vs[i] = Integer.parseInt(s[2]);
        }

        // Output result
        System.out.println(knapsackDP(N, C, W, ss, ws, vs));
    }

    private static int knapsackDP(int N, int C, int W, int[] ss, int[] ws, int[] vs) {
        int[][] dp = new int[C+1][W+1];
        for (int i=1; i<=N; i++) {
            for (int j=C; j>=1; j--) {
                for (int k=W; k>=1; k--) {
                    if (j>=ss[i-1] && k>=ws[i-1]) {
                        dp[j][k] = Math.max(dp[j][k], dp[j-ss[i-1]][k-ws[i-1]]+vs[i-1]);
                    }
                }
            }
        }
        return dp[C][W];
    }
}