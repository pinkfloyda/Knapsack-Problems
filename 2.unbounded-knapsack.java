/* Unbounded-Knapsack Problem
 *
 * Got N items and a knapsack with volumn V, each item has volumn v[i] and weight w[i],
 * each item can be picked unlimited times, what's the max total weight by putting items into 
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
 *   10
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
        
        int res = knapsackOptimize1D();
        
        System.out.println(res);
    }

    /* 1) Standard DP, time O(NVV), space O(NV)
     *
     * dp[i][j]=max{ dp[i-1][V-k*v[i]]+k*w[i], 0<=k<=j/v[i]] }
     */
    static int knapsack() {
        int[][] dp = new int[N+1][V+1];
        for(int i=1; i<=N; i++) {
            for(int j=1; j<=V; j++) {
                for(int k=0; k<=j/v[i-1]; k++) {
                    dp[i][j] = Math.max(dp[i][j], dp[i-1][j-k*v[i-1]] + k*w[i-1]);
                }
            }
        }
        return dp[N][V];
    }
    
    /* 2) Standard DP with binary compaction, time O(NVlogV), space O(NV)
     *
     * For each item, we can try to break it up into m items that each item has v[i] and w[i]
     * Then it is converted to a 01 knapsack problem, but the complexity is not changed
     *
     * To optimize it further, we can compact those m items into k items that each item is of volumn
     * v[i]*2^k and weight w[i]*2^k where k always make v[i]*2^k <= V. This works because combinations
     * of 1,2,4,....2^k can represent number from 1 to 2^(k+1)-1
     *
     * Noted that it is convenient to use 1D rolling array as we don't care how many items after
     * binary divide of each item.
     */
    static int knapsackBinary() {
        int[] dp = new int[V+1];
        for(int i=1; i<=N; i++) {
            for(int v2=v[i-1], w2=w[i-1]; v2<=V; v2*=2, w2*=2) {
                for(int j=V; j>=v2; j--) {
                    dp[j] = Math.max(dp[j], dp[j-v2] + w2);
                }
            }
        }
        return dp[V];
    }
    
    /* 3) DP with optimization, time O(NV), space O(NV)
     *
     * The standard DP is dp[i][j]=max{ dp[i-1][V-k*v[i]]+k*w[i], 0<=k<=j/v[i]]}
     * dp[i][j] needs to access dp[i-1][j], dp[i-1][j-v[i]], dp[i-1][j-2*v[i]], ...dp[i-1][1]
     * dp[i][j-v[i]] needs to access        dp[i-1][j-v[i]], dp[i-1][j-2*v[i]], ...dp[i-1][1]
     * To compute dp[i][j], we just need to access dp[i-1][j] and dp[i][j-v[i]]
     * So the new DP transition is dp[i][j]=max{ dp[i-1][j], dp[i][j-v[i]]+w[i] }
     */
    static int knapsackOptimize() {
        int[][] dp = new int[N+1][V+1];
        for (int i=1; i<=N; i++) {
            for (int j=1; j<=V; j++) {
                dp[i][j] = Math.max(dp[i-1][j], v[i-1]<=j ? dp[i][j-v[i-1]] + w[i-1] : 0);
            }
        }
        return dp[N][V];
    }
    
    /* 4) DP with optimization using rolling 1D array
     * 
     * Each row only access previous row and current row, so can just 1D rolling array
     * But unlike 01 knapsack, it must update from left to right, because the first updated dp values
     * are needed by later ones. 
     * dp[j](new-value) = max{ dp[j](old-value), dp[j-v[i]](new-value)+w[i] }
     *
     * Noted that the inner loop need to start from v[i-1] because former ones just old values
     */
    static int knapsackOptimize1D() {
        int[] dp = new int[V+1];
        for (int i=1; i<=N; i++) {
            for (int j=v[i-1]; j<=V; j++) {
                dp[j] = Math.max(dp[j], dp[j-v[i-1]]+w[i-1]);
            }
        }
        return dp[V];
    }
}
