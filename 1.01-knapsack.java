/* 01-Knapsack Problem
 *
 * Given a knapsack with volumn V and N items where each item has volumn v[i] and weight w[i],
 * each item can only be picked once, what's the max total weight by putting items into 
 * knapsack without exceeding its volumn.
 * 
 * Input:
 * First line has two integers: N and V, seperated by space
 * Each of next N lines has two integers: each item's volumn and weight, sperated by space
 * 
 * Output:
 * One line with one integer: max total item weight
 *
 * Sample Input:
 *   4 5
 *   1 2
 *   2 4
 *   3 4
 *   4 5
 * Sample Output:
 *   8
 */

import java.util.*;

public class Main {
    
    static int N;
    static int V;
    static int[] v;
    static int[] w;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        N = sc.nextInt();
        V = sc.nextInt();
        v = new int[N];
        w = new int[N];

        for(int i=0; i<N; i++) {
            v[i] = sc.nextInt();
            w[i] = sc.nextInt();
        }
        
        int res = knapsack1DOptimize();
        
        System.out.println(res);
    }

    /* 1) Standard DP, time O(NV), space O(NV)
     *
     * Suppose dp[i][j] is the optimal max value of puting (first) i items into a knapsack with capacity j,
     * then dp[i][j] = max{ dp[i-1][j], dp[i-1][j-v[i]] + w[i] } is optimal. Inductively, it will lead to 
     * final optimal solution in dp[N][V], which is Dynamic Programming
     *
     * Note in the inner loop, it must loop from 1 -> V instead of v[i-1] -> V, because dp[i][1..v[i-1]] still
     * need to be computed just in case later rows need those values.
     */
    static int knapsack() {
        int[][] dp = new int[N+1][V+1];
        for(int i=1; i<=N; i++) {
            for(int j=1; j<=V; j++) {
                dp[i][j] = Math.max(dp[i-1][j], j>=v[i-1] ? dp[i-1][j-v[i-1]]+w[i-1] : 0);
            }
        }
        return dp[N][V];
    }
    
    /* 2) Standard DP with rolling 1D array, time O(NV), space O(V)
     *
     * To compute each of dp[i][0..V], it only need to access previous row, so we can just use
     * a rolling 1D array which represents both the current (after update) and previous (before update) row.
     * But need to update the array from right-to-left: dp[j](new-value) = max{ dp[j](old-value), dp[j-v[i]](old-value)+w[i] }
     *
     * Note in the inner loop, it can stop loop at v[i-1], not necessarily to 1, for ith row dp[1..v[i-1]]
     * are just the previous row's values, feel the difference against 2D version
     */
     static int knapsack1D() {
        int[] dp = new int[V+1];
        for (int i=1; i<=N; i++) {
            for (int j=V; j>=v[i-1]; j--) {
                dp[j] = Math.max(dp[j], dp[j-v[i-1]]+w[i-1]);
            }
        }
        return dp[V];
    }
    
    /* 3) Standard DP optimized, time O(NV), space O(NV)
     * 
     * The right-bottom corner of the table dp[N][V] will be the final solution, so reversely
     * To compute dp[N][V], only dp[N-1][V-v[N-1]] and dp[N-1][V] will be used
     * To compute dp[N-1][V-v[N-1]], only dp[N-2][V-v[N-1]-v[N-2]] and dp[N-2][V-v[N-1]] will be used
     * 
     * So for each row i, when update, we only need to calculate starting from V-Σv[i..N]
     * Visually, the table is updated in a somewhat right-upper triangular fashion
     */
    static int knapsackOptimize() {
        int[][] dp = new int[N+1][V+1];
        int sum = 0;
        for (int i=1; i<=N; i++) {
            sum += v[i-1];
        }
        for(int i=1; i<=N; i++) {
            sum -= v[i-1];
            for(int j=Math.max(V-sum, 1); j<=V; j++) {
                dp[i][j] = Math.max(dp[i-1][j], j>=v[i-1] ? dp[i-1][j-v[i-1]]+w[i-1] : 0);
            }
        }
        return dp[N][V];
    }
    
    /* 4) Standard DP with rolling 1D array optimized
     * Same as 3) but using rolling 1D array
     */
    static int knapsack1DOptimize() {
        int[] dp = new int[V+1];
        int sum = 0;
        for (int i=1; i<=N; i++) {
            sum += v[i-1];
        }
        for (int i=1; i<=N; i++) {
            sum -= v[i-1];
            for (int j=V; j>=Math.max(V-sum, v[i-1]); j--) {
                dp[j] = Math.max(dp[j], dp[j-v[i-1]]+w[i-1]);
            }
        }
        return dp[V];
    }
    
    /* 5) Standard DP using rolling 1D array but happen to make knapsack full
     *
     * If question asks what is max total weight by putting item to make knapsack full; return -1 if not possible
     * Similar solution like above but initially dp[0] should be 0 and dp[1..V] should all be -1. It means initially for zero items
     * we can only make knapsack of 0 volumn full, not possible for other non-zero volumns.
     * When update dp using 1D rolling array: dp[j] = max(dp[j], dp[j-v[i]]+w[i]), we need to check if previous value is -1 or not, if -1
     * we cannot make knapsack full.
     */
    static int knapsackFull() {
        int[] dp = new int[V+1];
        for(int i=1; i<=V; i++) {
            dp[i] = -1;
        }
        for (int i=1; i<=N; i++) {
            for (int j=V; j>=v[i-1]; j--) {
                dp[j] = Math.max(dp[j]<0 ? -1 : dp[j], dp[j-v[i-1]]<0 ? -1 : dp[j-v[i-1]]+w[i-1]);
            }
        }
        return dp[V];
    }
}
