/* Lexical-Smallest-Solution-Knapsack Problem
 *
 * You have a knapsack with budget C and N items can be picked into knapsack,
 * each (i-th) item has cost ci, value vi and can only be picked once.
 * There can be many optimal picking solutions that can get max value 
 * without exceeding budget, let solution be a picking sequence of item indices sorted incrementally.
 * What is the lexical smallest solution?
 * 
 * e.g. both "10 11" and "2 3" are the optimal solutions, return "2 3" as it is lexical smaller (2 < 10).
 * 
 * Input:
 * First line has two integers: N and C, seperated by space
 * Then followed by next N lines representing items:
 *   each line has two integers: item's cost and value, sperated by space
 * 
 * Output:
 * One line with multiple integers: the picking sequence sorted incrementally by item index
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
 *   4 6
 * Sample Output:
 *   1 4
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
        
        List<Integer> res = knapsack();
        
        for(int n : res) {
            System.out.print(n + " ");
        }
    }
    
    /* For each status, log pick or not pick current item.
     *
     * But when tracing a solution in dp, it is starting from N,C and go backwards, so we need to relabel
     * the items: first iteration is actually for last item, ith item is actually for N-i+1 item
     * In this way, going backwards from N,C will pick items in increasing order.
     *
     * And when we pick items in increasing order, if we have choices, we need to pick current lower indexed item 
     * to ensure we end up with a smallest lexical solution. In that way, when update the dp transition, whenever
     * dp[j] == dp[j-c[k-1]]+v[k-1], we always choose to pick current item.
     */ 
    static List<Integer> knapsack() {
        int[] dp = new int[C+1];
        boolean[][] pick = new boolean[N+1][C+1];
        
        for(int i=1; i<=N; i++) {
            int k = N-i+1; // re-labeling of items
            for(int j=C; j>=c[k-1]; j--) {
                if(dp[j] <= dp[j-c[k-1]]+v[k-1]) {
                    pick[i][j] = true;
                }
                dp[j] = Math.max(dp[j], dp[j-c[k-1]]+v[k-1]);
            }
        }
        
        List<Integer> res = new ArrayList<>();
        for(int i=N, j=C; i>=1; i--) {
            if(pick[i][j]) {
                res.add(N-i+1);
                j -= c[N-i];
            }
        }
        
        return res;
    }
}
