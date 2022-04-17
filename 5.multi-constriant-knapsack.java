/* Multi-Constraints-Knapsack Problem
 *
 * Given a knapsack with volumn V, budget C and N items where each item has volumn v[i], cost c[i]
 * and weight w[i], each item can only be picked once, what's the max total weight by putting items 
 * into knapsack without exceeding its volumn and budget
 * 
 * Input:
 * First line has three integers: N, V, C, seperated by space
 * Each of next N lines has three integers: item's volumn, cost and weight, sperated by space
 * 
 * Output:
 * One line with one integer: max total item weight
 *
 * Sample Input:
 *   4 5 6
 *   1 2 3
 *   2 4 5
 *   3 4 5
 *   4 5 6
 * Sample Output:
 *   8
 */

import java.util.*;

public class Main {
    
    static int N;
    static int V;
    static int C;
    static int[] v;
    static int[] c;
    static int[] w;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        N = sc.nextInt();
        V = sc.nextInt();
        C = sc.nextInt();
        v = new int[N];
        c = new int[N];
        w = new int[N];

        for(int i=0; i<N; i++) {
            v[i] = sc.nextInt();
            c[i] = sc.nextInt();
            w[i] = sc.nextInt();
        }
        
        int res = knapsack();
        
        System.out.println(res);
    }
    
    /* 1) Standard DP
     *
     * Just add a new dimension for dp status and transition loop
     * for 1D rolling array, just need to update backwards for all dimensions
     *
     * The question can change to 01-knapsack problem but also not exceed total M number of items
     * that can also be solved by adding M as additional dimension and the second loop will count from M to 1
     */
    static int knapsack() {
        int[][] dp = new int[V+1][C+1];
        for(int i=1; i<=N; i++) {
            for(int j=V; j>=v[i-1]; j--) {
                for(int k=C; k>=c[i-1]; k--) {
                    dp[j][k] = Math.max(dp[j][k], dp[j-v[i-1]][k-c[i-1]]+w[i-1]);
                }
            }
        }
        return dp[V][C];
    }  
}
