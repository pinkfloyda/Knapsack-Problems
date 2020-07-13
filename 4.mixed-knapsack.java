import java.util.*;
import java.io.*;

/*
Input format:

First line is two integers N and C, representing unique item count and knapsack capacity, seperated by space
Following N lines where each line is three integers si, vi and qi, representing ith item's size, value and quantity (-1 means 01, 0 means unbounded)

Output format:
One line of one integer, which is the final result: the possible max value
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
        int[] qs = new int[N]; // quantity array for each item

        // Read size, value and quantity for each item
        for (int i=0; i<N; i++) {
            s = in.readLine().split(" ");
            ss[i] = Integer.parseInt(s[0]);
            vs[i] = Integer.parseInt(s[1]);
            qs[i] = Integer.parseInt(s[2]);
        }

        // Output result
        System.out.println(knapsackDP(N, C, ss, vs, qs));
    }

    // Just combine 01/complete/multiple knapsack algorithms
    private static int knapsackDP(int N, int C, int[] ss, int[] vs, int[] qs) {
        int[] dp = new int[C+1];
        for (int i=0; i<N; i++) {
            if (qs[i] < 0) { // solve by using 01 knapsack algorithm
                for (int j=C; j>=ss[i]; j--) {
                    dp[j] = Math.max(dp[j], dp[j-ss[i]]+vs[i]);
                }
            } else if (qs[i] == 0) { // solve by using complete knapsack algorithm
                for (int j=ss[i]; j<=C; j++) {
                    dp[j] = Math.max(dp[j], dp[j-ss[i]]+vs[i]);
                }
            } else { // solve by using multiple knapsack algorithm
                int m = qs[i];
                for (int s=ss[i], v=vs[i], k=1; m-k>0; s*=2, v*=2, k*=2) {
                    for (int j=C; j>=s; j--) {
                        dp[j] = Math.max(dp[j], dp[j-s]+v);
                    }
                    m -= k;
                }
                for (int j=C; j>=m*ss[i]; j--) {
                    dp[j] = Math.max(dp[j], dp[j-m*ss[i]]+m*vs[i]);
                }
            }
        }
        return dp[C];
    }

}