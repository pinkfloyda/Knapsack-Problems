/* 01-Knapsack Problem
 *
 * You have a knapsack with budget C and N items can be picked into knapsack,
 * each (i-th) item has cost ci, value vi and can only be picked once.
 * What is the max value you can get without exceeding budget?
 * 
 * Input:
 * First line has two integers: N and C, seperated by space
 * Then followed by next N lines representing items:
 *   each line has two integers: item's cost and value, sperated by space
 * 
 * Output:
 * One line with one integer: max value
 *
 * Data range:
 * 0 < N,C ≤ 1000
 * 0 < ci,vi ≤ 1000
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
    static int C;
    static int[] c;
    static int[] v;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        N = sc.nextInt();
        C = sc.nextInt();
        
        c = new int[N];
        v = new int[N];

        for(int i=0; i<N; i++) {
            c[i] = sc.nextInt();
            v[i] = sc.nextInt();
        }
        
        int res = knapsack1DOptimize();
        
        System.out.println(res);
    }

    /* 1) Standard DP using 2D array, time O(NC), space O(NC)
     *
     * dp[i][j] is the max value of picking from first i items without execeeding cost j,
     * Suppose you have all values of dp[i-1][1..C], max values of picking from first i-1 items without
     * execeeding each cost of 1 ~ C, then you will have option to choose ith item or not (for each cost of
     * 1 ~ C), so dp[i][j] = max{ dp[i-1][j], dp[i-1][j-c[i]]+v[i] } (1<=j<=C)
     * dp[N][C] will be the final answer: the max value of picking from N items without execeeding C
     *
     * Note in the inner loop, it must loop from 1->C instead of c[i-1]->C, because dp[i][1..c[i-1]] still
     * needed by later items, they must be assigned values of dp[i-1][1..c[i-1]]
     */
    static int knapsack() {
        int[][] dp = new int[N+1][C+1];
        for(int i=1; i<=N; i++) {
            for(int j=1; j<=C; j++) {
                dp[i][j] = dp[i-1][j];
                if (j >= c[i-1]) {
                    dp[i][j] = Math.max(dp[i][j], dp[i-1][j-c[i-1]]+v[i-1]);
                }
            }
        }
        return dp[N][C];
    }
    
    /* 2) Standard DP using 1D rolling array, time O(NC), space O(NC)
     *
     * To compute each of dp[i][1..C], it only need to access previous row, so we can just use
     * a rolling 1D array which represents both the current (after update) and previous (before update) rows.
     * But need to update the array from right-to-left like below to avoid dp[j-c[i]] being overwritten:
     * dp[j](new-value) = max{ dp[j](old-value), dp[j-c[i]](old-value)+v[i] } (1<=j<=C)
     *
     * Note in the inner loop, it can stop loop at c[i-1], not necessarily to 1, for ith row dp[1..c[i-1]]
     * are just inherit the previous values, unlike 2D array that they must be assigned values explicitly
     */
     static int knapsack1D() {
        int[] dp = new int[C+1];
        for (int i=1; i<=N; i++) {
            for (int j=C; j>=c[i-1]; j--) {
                dp[j] = Math.max(dp[j], dp[j-c[i-1]]+v[i-1]);
            }
        }
        return dp[C];
    }
    
    /* 3) Optimized DP using 2D array, time O(NC), space O(NC)
     * 
     * The right-bottom corner of the table dp[N][C] will be the final answer, so think reversely:
     * To compute dp[N][C], only dp[N-1][C-c[N-1]] and dp[N-1][C] will be used
     * To compute dp[N-1][C-c[N-1]], only dp[N-2][C-c[N-1]-c[N-2]] and dp[N-2][C-c[N-1]] will be used
     * 
     * So for each row i, when update, only values starting from C-Σc[i..N] needed by later rows
     * Visually, the table is updated in a somewhat right-upper triangular fashion
     */
    static int knapsackOptimize() {
        int[][] dp = new int[N+1][C+1];
        int sum = 0;
        for (int i=1; i<=N; i++) {
            sum += c[i-1];
        }
        for(int i=1; i<=N; i++) {
            sum -= c[i-1];
            for(int j=Math.max(C-sum, 1); j<=C; j++) {
                dp[i][j] = dp[i-1][j];
                if (j >= c[i-1]) {
                    dp[i][j] = Math.max(dp[i][j], dp[i-1][j-c[i-1]]+v[i-1]);
                }
            }
        }
        return dp[N][C];
    }
    
    /* 4) Optimized DP using 1D rolling array, time O(NC), space O(C)
     */
    static int knapsack1DOptimize() {
        int[] dp = new int[C+1];
        int sum = 0;
        for (int i=1; i<=N; i++) {
            sum += c[i-1];
        }
        for (int i=1; i<=N; i++) {
            sum -= c[i-1];
            for (int j=C; j>=Math.max(C-sum, c[i-1]); j--) {
                dp[j] = Math.max(dp[j], dp[j-c[i-1]]+v[i-1]);
            }
        }
        return dp[C];
    }
    
    /* 5) Standard DP using rolling 1D array but happen to meet budget (no more no less)
     *
     * What is max value by picking item but happen to meet budget, return -1 if not possible
     * Now dp[i][j] stands for max value of picking from first i items with cost happen to be j
     * Initially dp[0] should be 0 and dp[1..C] should all be -1. 
     * It means initially for zero items we can meet budget of 0 without picking anything and max value is 0;
     * not possible for other non-zero budget as we have nothing to pick from.
     *
     * When update 1D rolling array: dp[j] = max(dp[j], dp[j-c[i]]+v[i]), for dp[j-c[i]]+v[i] part, we need to 
     * check if dp[j-c[i]] is -1 or not, if so we cannot pick ith item.
     */
    static int knapsackFull() {
        int[] dp = new int[C+1];
        for(int i=1; i<=C; i++) {
            dp[i] = -1;
        }
        for (int i=1; i<=N; i++) {
            for (int j=C; j>=c[i-1]; j--) {
                dp[j] = Math.max(dp[j], dp[j-c[i-1]]<0 ? -1 : dp[j-c[i-1]]+v[i-1]);
            }
        }
        return dp[C];
    }
}
