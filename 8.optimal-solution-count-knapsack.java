/* Optimal-Solution-Count-Knapsack Problem
 *
 * Given a knapsack with volumn V and N items where each item has volumn v[i] and weight w[i],
 * each item can only be picked once, can get max total weight by putting items into 
 * knapsack without exceeding its volumn, compute how many optimal solutions.
 * 
 * Input:
 * First line has two integers: N and V, seperated by space
 * Each of next N lines has two integers: each item's volumn and weight, sperated by space
 * 
 * Output:
 * One line with one integer: max total item weight, return number % (10^9 + 7)
 *
 * Sample Input:
 *   4 5
 *   1 2
 *   2 4
 *   3 4
 *   4 6
 * Sample Output:
 *   2
 */

import java.util.*;

public class Main {
    static int M = 1000000007;
    
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
        
        int res = knapsack();
        
        System.out.println(res);
    }
    
    static int knapsack() {
        int[][] dp = new int[V+1][2]; // [0] means optimal weight, [1] means solution count
        
        for(int i=0; i<=V; i++) {
            dp[i][1] = 1; // initially, for knapsack with 0 volumn, the only optimal solution is don't put any
        }
        
        for(int i=1; i<=N; i++) {
            for(int j=V; j>=v[i-1]; j--) {
                if(dp[j][0] == dp[j-v[i-1]][0] + w[i-1]) {
                    dp[j][1] = (dp[j][1] + dp[j-v[i-1]][1]) % M;
                } else if(dp[j][0] < dp[j-v[i-1]][0] + w[i-1]) {
                    dp[j][0] = dp[j-v[i-1]][0] + w[i-1];
                    dp[j][1] = dp[j-v[i-1]][1];
                }
                // if dp[j][0] > dp[j-v[i-1]][0] + w[i-1], don't need to do anything
                // as dp[j][*] just inherit the previous value
            }
        }
        
        return dp[V][1];
    }
}
