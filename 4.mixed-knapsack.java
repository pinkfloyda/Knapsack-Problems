/* Mixed-Knapsack Problem
 *
 * Given a knapsack with volumn V and N items where each item has volumn v[i], weight w[i]
 * and can be either picked once, unlimited or with quantity n[i], what's the max total weight 
 * by putting items into knapsack without exceeding its volumn.
 * 
 * Input:
 * First line has two integers: N and V, seperated by space
 * Each of next N lines has three integers: item's volumn, weight and quantity, sperated by space
 *   quantity == -1 means this item can be picked once
 *   quantity == 0 means this item can be picked unlimited
 *   quantity > 0 means this item can be picked with quantity
 * 
 * Output:
 * One line with one integer: max total item weight
 *
 * Sample Input:
 *   4 5
 *   1 2 -1
 *   2 4 1
 *   3 4 0
 *   4 5 2
 * Sample Output:
 *   8
 */

import java.util.*;

public class Main {
    
    static int N;
    static int V;
    static int[] v;
    static int[] w;
    static int[] n;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        N = sc.nextInt();
        V = sc.nextInt();
        v = new int[N];
        w = new int[N];
        n = new int[N];

        for(int i=0; i<N; i++) {
            v[i] = sc.nextInt();
            w[i] = sc.nextInt();
            n[i] = sc.nextInt();
        }
        
        int res = knapsack();
        
        System.out.println(res);
    }
 
    /* 1) Standard DP
     *
     * Just combine solutions based on which kind of item
     */
    static int knapsack() {
        int[] dp = new int[V+1];
        for (int i=1; i<=N; i++) {
            if(n[i-1] < 0) { // using 01 knapsack solution
                for (int j=V; j>=v[i-1]; j--) {
                    dp[j] = Math.max(dp[j], dp[j-v[i-1]]+w[i-1]);
                }
            } else if(n[i-1] == 0) { // using unbounded knapsack solution
                for (int j=v[i-1]; j<=V; j++) {
                    dp[j] = Math.max(dp[j], dp[j-v[i-1]]+w[i-1]);
                }
            } else { // using bounded knapsack solution
                int m = n[i-1];
                for(int k=1, v2=v[i-1], w2=w[i-1]; m-k>0; v2*=2, w2*=2, k*=2) {
                    for(int j=V; j>=v2; j--) {
                        dp[j] = Math.max(dp[j], dp[j-v2]+w2);
                    }
                    m -= k; 
                }
                for(int j=V, v2=m*v[i-1], w2=m*w[i-1]; j>=v2; j--) {
                    dp[j] = Math.max(dp[j], dp[j-v2]+w2);
                }
            }
        }
        return dp[V];
    }
}
