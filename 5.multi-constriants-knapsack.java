/* Multi-Constraints-Knapsack Problem
 *
 * You have a knapsack with budget C, weight limit W and N items can be picked into knapsack,
 * each (i-th) item has cost ci, weight wi and value vi , each item can only be picked once 
 * What is the max value you can get without exceeding budget?
 * 
 * Input:
 * First line has three integers: N, C, W, seperated by space
 * Then followed by next N lines representing items:
 *   each line has three integers: item's cost, weight and value, sperated by space
 * 
 * Output:
 * One line with one integer: max value
 *
 * Data Range:
 * 0 < N ≤ 1000 
 * 0 < C,W ≤ 100
 * 0 < ci,wi ≤ 100
 * 0 < vi ≤ 1000
 *
 * Sample Input:
 *   4 5 6
 *   1 2 3
 *   2 4 4
 *   3 4 5
 *   4 5 6
 * Sample Output:
 *   8
 */

import java.util.*;

public class Main {
    
    static int N;
    static int C;
    static int W;
    static int[] c;
    static int[] w;
    static int[] v;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        N = sc.nextInt();
        C = sc.nextInt();
        W = sc.nextInt();
        c = new int[N];
        w = new int[N];
        v = new int[N];

        for(int i=0; i<N; i++) {
            c[i] = sc.nextInt();
            w[i] = sc.nextInt();
            v[i] = sc.nextInt();
        }
        
        int res = knapsack2D();
        
        System.out.println(res);
    }
    
    /* 1) Standard DP using 3D array, time O(NCW), space O(NCW)
     *
     * Just add a new dimension for dp status and transition loop
     *
     * The question can be changed to include additional constraint: picking not more than M items in total
     * that can also be solved by adding M as additional dimension and the second loop will count from M to 1
     */
    static int knapsack() {
        int[][][] dp = new int[N+1][C+1][W+1];
        for(int i=1; i<=N; i++) {
            for(int j=1; j<=C; j++) {
                for(int k=1; k<=W; k++) {
                    dp[i][j][k] = dp[i-1][j][k];
                    if(j >= c[i-1] && k >= w[i-1]) {
                         dp[i][j][k] = Math.max(dp[i][j][k], dp[i-1][j-c[i-1]][k-w[i-1]]+v[i-1]);
                    }
                }
            }
        }
        return dp[N][C][W];
    }
    
    /* 2) Standard DP using 2D rolling array, time O(NCW), space O(CW)
     *
     * Just update the array in backwards for all inner (contraint) dimensions
     */
    static int knapsack2D() {
        int[][] dp = new int[C+1][W+1];
        for(int i=1; i<=N; i++) {
            for(int j=C; j>=c[i-1]; j--) {
                for(int k=W; k>=w[i-1]; k--) {
                    dp[j][k] = Math.max(dp[j][k], dp[j-c[i-1]][k-w[i-1]]+v[i-1]);
                }
            }
        }
        return dp[C][W];
    }
}
