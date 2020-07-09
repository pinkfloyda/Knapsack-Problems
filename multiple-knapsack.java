import java.util.*;
import java.io.*;

/*
Input format:

First line is two integers N and C, representing unique item count and knapsack capacity, seperated by space
Following N lines where each line is three integers si, vi and qi, representing ith item's size, value and quantity

Output format:
One line of one integer, which is the final result: the possible max value
*/

// 1) DP using 1D array
// 2) DP using 1D array, optimized by reducing to 01 knapsack with binary division

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

        // Read size and value for each item
        for (int i=0; i<N; i++) {
            s = in.readLine().split(" ");
            ss[i] = Integer.parseInt(s[0]);
            vs[i] = Integer.parseInt(s[1]);
            qs[i] = Integer.parseInt(s[2]);
        }

        // Output result
        System.out.println(knapsackDPBinaryDivideOptimize(N, C, ss, vs, qs));
    }

    // F(i,c) = max{F(i-1,c-k*ci)+k*vi, 0<=k<=Mi}
    // Complexity is O(N*C*Mi)
    private static int knapsackDP(int N, int C, int[] ss, int[] vs, int[] qs) {
        int[] dp = new int[C+1];
        for (int i=0; i<N; i++) {
            for (int j=C; j>=ss[i]; j--) {
                for (int k=1, s=ss[i], v=vs[i]; s<=j && k<=qs[i]; s+=ss[i], v+=vs[i], k++) {
                    dp[j] = Math.max(dp[j], dp[j-s]+v);
                }
            }
        }
        return dp[C];
    }

    // Consider divide each item into qs[i] items, then apply 01 knapsack algorithm, the complexity is same as above
    private static int knapsackDPDivide(int N, int C, int[] ss, int[] vs, int[] qs) {
        int[] dp = new int[C+1];
        for (int i=0; i<N; i++) {
            for (int k=1, s=ss[i], v=vs[i]; k<=qs[i]; k++) {
                for (int j=C; j>=s; j--) {
                    dp[j] = Math.max(dp[j], dp[j-s]+v);
                }
            }
        }
        return dp[C];
    }

    // Can consider binary divide them into 1,2,4,...
    // But a tricky part is the possible max number can be represented cannot exceed Mi, otherwise
    // it can choose item i beyond Mi, which is wrong. So it must be of values
    // 1,2,2^2,...2^(k-1),Mi-2^k+1. As we know the max number can be represented is 1<=m<=2^k-1
    // So first of all, we must make 2^k-1 <= Mi, and for the remanining, we choose the final value to be Mi-2^k+1
    // In this way, we can represent number of range [1,Mi] (noted that every number between 2^k and Mi can be represented)
    //
    // Refer to below line, B-C is the value of Mi-2^k+1, which is the multiplier of the last item, clearly, by combining this value
    // With any value between A and B can yield values between B-C, which means this setup can represent any numbers between [1..Mi]
    // (1)            (2^k-1)          Mi    (2^(k+1)-1)
    //  A----------------B-------------C----------D-----------
    //
    // Complexity is O(N*C*log(Mi))
    private static int knapsackDPBinaryDivide(int N, int C, int[] ss, int[] vs, int[] qs) {
        int[] dp = new int[C+1];
        for (int i=0; i<N; i++) {
            int k=1;
            for (int s=ss[i], v=vs[i]; (int)Math.pow(2,k)-1<=qs[i]; s*=2, v*=2, k++) {
                for (int j=C; j>=s; j--) {
                    dp[j] = Math.max(dp[j], dp[j-s]+v);
                }
            }
            int m = qs[i]-(int)Math.pow(2,k-1)+1;
            for (int j=C; j>=m*ss[i]; j--) {
                dp[j] = Math.max(dp[j], dp[j-m*ss[i]]+m*vs[i] );
            }
        }
        return dp[C];
    }

    // Slight optimize by checking if can solve for complete knapsack for some items
    private static int knapsackDPBinaryDivideOptimize(int N, int C, int[] ss, int[] vs, int[] qs) {
        int[] dp = new int[C+1];
        for (int i=0; i<N; i++) {
            if (qs[i] * ss[i] >= C) { // Go with complete knapsack algorithm
                for (int j=ss[i]; j<=C; j++) {
                    dp[j] = Math.max(dp[j], dp[j-ss[i]] + vs[i]);
                }
            } else { // Go with multiple knapsack algorithm
                int k=1;
                for (int s=ss[i], v=vs[i]; (int)Math.pow(2,k)-1<=qs[i]; s*=2, v*=2, k++) {
                    for (int j=C; j>=s; j--) {
                        dp[j] = Math.max(dp[j], dp[j-s]+v);
                    }
                }
                int m = qs[i]-(int)Math.pow(2,k-1)+1;
                for (int j=C; j>=m*ss[i]; j--) {
                    dp[j] = Math.max(dp[j], dp[j-m*ss[i]]+m*vs[i] );
                }
            }
        }
        return dp[C];
    }
}