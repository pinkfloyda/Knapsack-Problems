import java.util.*;
import java.io.*;

/*
Input format:

First line is two integers N and C, representing unique item count and knapsack capacity, seperated by space
Following N lines where each line is two integers si and vi, representing ith item's size and value

Output format:
One line of one integer, which is the final result: the possible max value
*/

// 1) Brute-force using DFS + Memoization
// 2) DP using 2D array
// 3) DP using 2D array, optimized by reducing to 01 knapsack with binary division
// 4) DP Using 1D array, optimized by reducing to 01 knapsack with binary division
// 5) DP using 2D array, optimized for O(N*C)
// 6) DP using 1D array, optimized for O(N*C)

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
        System.out.println(knapsackDPBinaryDivideRolling(N, C, ss, vs));
    }

    // Similar to 01-knapsack, for each item, consider pick or not pick, but for pick case, it can
    // pick 1 or more times. So the sub-problems will be not-pick, pick-1-time, pick-2-times, etc.
    // The math formation will be F(i,c) = max{F(i-1,c), F(i-1,c-c(i))+v(i), F(i-1,c-2*c(i))+2*v(i), ...}
    // Until c-m*c(i) is negative. Make it concise, it is F(i,c)=max{F(i-1,c-m*c(i))+m*v(i), m is [0..c/c(i)]}
    // So 01-knapsack is actually a special case of this complete-knapsack problem
    //
    // Visually, the recursion is a decision tree with multiple children, without memoization, it is O(C^N) considering
    // worst case of items with size 1. With memoization, the memo table is still a 2D array, but when updating each entry
    // it will consult m times where m=c/c(i), in worst case of itmes with size 1, m == C. So it is O(N*C*C)
    private static int knapsackRecursion(int N, int C, int[] ss, int[] vs, boolean optimize) {
        int[][] memo = null;

        if (optimize) {
            memo = new int[N][C+1];
            for (int i=0; i<N; i++) {
                for (int j=0; j<C+1; j++) {
                    memo[i][j] = -1; // -1 means no value
                }
            }
            for (int i=0; i<N; i++) {
                memo[i][0] = 0; // for ZERO capacity there is nothing we can put, so initialize to be 0
            }
        }

        // Count from end to start, can also count from start to end, but not straight-forward when converting to DP solution
        return knapsackRecursion(ss, vs, N-1, C, memo);
    }

    private static int knapsackRecursion(int[] ss, int[] vs, int i, int c, int[][] memo) {
        if (i < 0) {
            return 0;
        }

        if (memo != null && memo[i][c] >= 0) {
            return memo[i][c];
        }

        int ans = 0;
        for (int m=0; m<=c/ss[i]; m++) {
            ans = Math.max(ans, knapsackRecursion(ss, vs, i-1, c-m*ss[i], memo) + m*vs[i]);
        }

        if (memo != null) {
            memo[i][c] = ans;
        }

        return ans;
    }

    // With the formation F(i,c)=max{F(i-1,c-m*c(i))+m*v(i), m is [0..c/c(i)]}
    // Only two variables here i and c, so it has DP solution, where F(i,c) means the max value for first i items
    // putting into a knapsack of capacity c. The final anwser will be F(N,C)
    private static int knapsackDP(int N, int C, int[] ss, int[] vs) {
        // initialize all to be 0s, especially for dp[i][0] (knapsack with ZERO capacity) and dp[0][j] (ZERO items to choose from)
        int[][] dp = new int[N+1][C+1];
        for (int i=1; i<=N; i++) {
            for (int j=1; j<=C; j++) {
                for (int k=0; k<=j/ss[i-1]; k++) {
                    dp[i][j] = Math.max(dp[i][j], dp[i-1][j-k*ss[i-1]] + k*vs[i-1]);
                }
            }
        }
        return dp[N][C];
    }

    // For each item, we can break it up into m items with c(i) and v(i) where m is C/c(i)
    // Then it is converted to a 01 knapsack problem, the complexity is not changed
    //
    // To optimize it further, we can compact those m items into k items where each item is of size
    // c(i)*2^k and value v(i)*2^k where k always make c(i)*2^k <= C. In this way, they can represent
    // numbers ranging from 1 ~ 2^(k+1)-1, and because c(i)*2^k <= C, so C must be less or equal to 2^(k+1)-1
    // (otherwise, k should be choosen to be k+1)
    //
    // Now the complexity is improved to be O(N*C*log(C/ci))
    private static int knapsackDPBinaryDivide(int N, int C, int[] ss, int[] vs) {
        // Augment size and value array
        List<Integer> ss2 = new ArrayList<>();
        List<Integer> vs2 = new ArrayList<>();
        for (int i=0; i<N; i++) {
            for (int s=ss[i], v=vs[i]; s<=C; s*=2, v*=2) {
                ss2.add(s);
                vs2.add(v);
            }
        }
        int N2 = ss2.size();

        // DP for 01 knapsack solution
        int[][] dp = new int[N2+1][C+1];
        for (int i=1; i<=N2; i++) {
            for (int j=1; j<=C; j++) {
                dp[i][j] = Math.max(dp[i-1][j], ss2.get(i-1)<=j ? (dp[i-1][j-ss2.get(i-1)] + vs2.get(i-1)) : 0);
            }
        }
        return dp[N2][C];
    }

    // Use 1D rolling array and also optimize the space further by embeding binary division into for loop
    private static int knapsackDPBinaryDivideRolling(int N, int C, int[] ss, int[] vs) {
        int[] dp = new int[C+1];
        for (int i=0; i<N; i++) {
            for (int s=ss[i], v=vs[i]; s<=C; s*=2, v*=2) {
                for (int j=C; j>=s; j--) {
                    dp[j] = Math.max(dp[j], dp[j-s]+v);
                }
            }
        }
        return dp[C];
    }

    // This optimization is tricky in a way that is not easy to come up with and understand
    //
    // Originally F(i,c) = max{F(i-1,c), F(i-1,c-ci)+vi, F(i-1,c-2ci)+2vi, ...}   #1
    // But F(i,c-ci) = max{F(i-1,c-ci), F(i-1,c-2ci)+vi, F(i-1,c-3ci)+2vi, ...}   #2
    // Then F(i,c-ci)+vi = max{F(i-1,c-ci)+vi, F(i-1,c-2ci)+2vi, ...}             #3
    // Replace #3 into #1 will get F(i,c) = max{F(i-1,c), F(i,c-ci)+vi}              #4
    // Clearly it is of O(N*C)
    //
    // It can also observed visually, table entry(i,c-ci) look up entry(i-1,c-ci),entry(i-1,c-2ci),....
    // table entry(i,c) look up entry(i-1,c),entry(i-1,c-ci),entry(i-1,c-2ci),....
    // so the original DP duplicate computations in table building, can just re-use entry(i,c-ci)
    //
    // This solution is not that straight-forward to come up with in first thought
    // One way of thinking is for each item, try to think about if pick (multiple times) or not pick,
    // which leads to the above DP solution, the problem is broken down into subproblems of not pick, pick once, pick twice, etc.
    // But we can stick with pick or not pick for a while. For sub-problem of not pick,
    // obviously F(i-1,c) is the optimal solution of this sub-problem, For sub-problem of pick,
    // because there is no limit on picking ith item, if we already know F(i,c-ci), choose one more that is
    // F(i,c-ci)+wi will be the optimal solution of this sub-problem
    // So combining two sub-problems: F(i,c) = max{F(i-1,c), F(i,c-ci)+vi}
    private static int knapsackDPOptimize(int N, int C, int[] ss, int[] vs) {
        int[][] dp = new int[N+1][C+1];
        for (int i=1; i<=N; i++) {
            for (int j=1; j<=C; j++) {
                dp[i][j] = Math.max(dp[i-1][j], ss[i-1]<=j ? (dp[i][j-ss[i-1]] + vs[i-1]) : 0);
            }
        }
        return dp[N][C];
    }

    // In F(i,c) = max{F(i-1,c), F(i,c-ci)+vi}, for each table entry update, only need to access top and left
    // entries, we can use just 1D array to update, when update dp[c], it is actually i-1's value, and i's c-ci is already
    // computed in dp[c-ci], so unlike 01 knapsack, this 1D array is update from left-to-right
    private static int knapsackDPOptimizeRolling(int N, int C, int[] ss, int[] vs) {
        int[] dp = new int[C+1];
        for (int i=1; i<=N; i++) {
            for (int j=ss[i-1]; j<=C; j++) {
                dp[j] = Math.max(dp[j], dp[j-ss[i-1]] + vs[i-1]);
            }
        }
        return dp[C];
    }
}