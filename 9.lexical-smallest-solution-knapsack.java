/* Lexical-Smallest-Solution-Knapsack Problem
 *
 * Given a knapsack with volumn V and N items where each item has volumn v[i] and weight w[i],
 * each item can only be picked once, putting items (in increasing order) into knapsack to get max 
 * total weight and without exceeding its volumn. Return the smallest lexical solution.
 * e.g. both "2 3" and "1 4" are the optimal solutin, return "1 4" as it is lexically smaller.
 * 
 * Input:
 * First line has two integers: N and V, seperated by space
 * Each of next N lines has two integers: each item's volumn and weight, sperated by space
 * 
 * Output:
 * One line with integers: item number in increasing order, sperated by space
 *
 * Sample Input:
 *   4 5
 *   1 2
 *   2 4
 *   3 4
 *   4 6
 * Sample Output:
 *   1 4
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
        
        List<Integer> res = knapsack();
        
        for(int n : res) {
            System.out.print(n + " ");
        }
    }
    
    /* For each status, log pick or not pick current item.
     *
     * But when tracing a solution in dp, it is starting from N,V and go backwards, so we need to relabel
     * the items, e.g. first iteration is actually for last item, ith item is actually for N-i+1 item
     * In this way, the final dp from N,V will pick items in increasing order.
     *
     * And when we pick items in increasing order, if possible we need to pick current lower indexed item 
     * to ensure we end up with a smallest lexical solution.
     * 
     * To ensure we always pick current lower indexed item, in the dp transition, we need to log pick status
     * when dp[j] <= dp[j-v[k-1]]+w[k-1]
     */ 
    static List<Integer> knapsack() {
        int[] dp = new int[V+1];
        boolean[][] pick = new boolean[N+1][V+1];
        
        for(int i=1; i<=N; i++) {
            int k = N-i+1; // re-labeling of items
            for(int j=V; j>=v[k-1]; j--) {
                if(dp[j] <= dp[j-v[k-1]]+w[k-1]) {
                    pick[i][j] = true;
                }
                dp[j] = Math.max(dp[j], dp[j-v[k-1]]+w[k-1]);
            }
        }
        
        List<Integer> res = new ArrayList<>();
        for(int i=N, j=V; i>=1; i--) {
            if(pick[i][j]) {
                res.add(N-i+1);
                j -= v[N-i];
            }
        }
        
        return res;
    }
}
