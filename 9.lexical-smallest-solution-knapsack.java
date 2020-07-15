import java.util.*;
import java.io.*;

/*
Input format:

First line is two integers N and C, representing unique item count and knapsack capacity, seperated by space
Following N lines where each line is two integers si and vi, representing ith item's size and value

Output format:
One line of several integers (1..N) seperated by spaces, which are the choices of items selected but is smallest in lexical order
(e.g. 1, 2, 8 ... is smaller than 1,3,....)
*/

public class Main {

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
        StringBuilder sb = new StringBuilder();
        for (int i : knapsackDPSolution2(N, C, ss, vs)) {
            sb.append(i).append(" ");
        }
        System.out.println(sb.toString());
    }

    // for each status, log pick or not pick current item. Because when tracing a solution, it is from N,C and go backwards
    // In order to get optimal solution, we need to reverse the labeling of items, e.g. The ith item in dp tracking actually
    // refers to N-ith item, in this way, when tracing a solution from N,C, it actually prints a solution incrementally
    // In this way, when log pick or not pick i (item N-i), if both ways got same cost, we need to log pick (otherwise, a bigger item
    // will be picked in future causing bigger lexical order)
    //
    // Actually this is kinda greedy, for c, if not pick i (item N-i+1), then the follwing (item N-i+1..N) will try to occupy c.
    // Then there will exist an index from N-i+1..N to be chosen causing bigger lexical order.
    private static List<Integer> knapsackDPSolution(int N, int C, int[] ss, int[] vs) {
        int[] dp = new int[C+1];
        int[][] g = new int[N+1][C+1]; // 0 means not pick, 1 means pick

        for (int i=1; i<=N; i++) {
            int k = N-i; // consider the array is N....1, first item is actually last one
            for (int j=C; j>=ss[k]; j--) {
                int pickValue = dp[j-ss[k]] + vs[k];
                int notPickValue = dp[j];
                g[i][j] = notPickValue > pickValue ? 0 : 1;
                dp[j] = Math.max(notPickValue, pickValue);
            }
        }

        List<Integer> ans = new ArrayList<>();

        // Try to trace back to get selections
        int c = C;
        for (int i=N; i>=1; i--) {
            if (g[i][c] == 1) {
                ans.add(N-i+1);
                c -= ss[N-i];
            }
        }

        return ans;
    }

    // Don't need to mantain 2D to track picking/not-picking status
    private static List<Integer> knapsackDPSolution2(int N, int C, int[] ss, int[] vs) {
        int[][] dp = new int[N+1][C+1];

        for (int i=1; i<=N; i++) {
            for (int j=1; j<=C; j++) {
                dp[i][j] = Math.max(dp[i-1][j], j>=ss[N-i] ? (dp[i-1][j-ss[N-i]] + vs[N-i]) : 0);
            }
        }

        List<Integer> ans = new ArrayList<>();

        // Try to trace back to get selections
        int c = C;
        for (int i=N; i>=1; i--) {
            int k = N-i;
            if (c >= ss[k] && dp[i-1][c-ss[k]]+vs[k] >= dp[i-1][c]) {
                ans.add(k+1);
                c -= ss[k];
            }
        }

        return ans;
    }
}