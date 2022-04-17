/* Group-Knapsack Problem
 *
 * Given a knapsack with volumn V and N groups of items where each group has n[i] items, 
 * each item has volumn v[i], weight w[i], at most one item can be picked from each group,
 * what's the max total weight by putting items into knapsack without exceeding its volumn.
 * 
 * Input:
 * First line has two integers: N and V, seperated by space
 * Then followed by N groups of lines, for each group:
 *   The first line has one integer: n, the number of items in this group
 *   Each of next n lines has two integers: each item's volumn and weight, sperated by space
 * 
 * Output:
 * One line with one integer: max total item weight
 *
 * Sample Input:
 *   3 5
 *   2
 *   1 2
 *   2 4
 *   1
 *   3 4
 *   1
 *   4 5
 * Sample Output:
 *   8
 */

import java.util.*;

public class Main {
    
    static int N;
    static int V;
    static List<List<Integer>> v = new ArrayList<>();
    static List<List<Integer>> w = new ArrayList<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        N = sc.nextInt();
        V = sc.nextInt();

        for(int i=0; i<N; i++) {
            int n = sc.nextInt();
            v.add(new ArrayList<>());
            w.add(new ArrayList<>());
            for(int j=0; j<n; j++) {
                v.get(i).add(sc.nextInt());
                w.get(i).add(sc.nextInt());
            }
        }
        
        int res = knapsack1D();
        
        System.out.println(res);
    }
 
    /* 1) Standard DP
     *
     * dp[i][j] = max{ dp[i-1][j], dp[i-1][j-v[i][k]]+w[i][k], k is each group's item index }
     * But when implement using 2D array, dp[i-1][j] needs to move out to 2nd loop instead of in most inner loop
     * e.g. the answer should be max { a, b, c, d }, if we move into most inner loop, answser will be max { a, d } only
     * which is wrong; we need to compute it as max { max { max { a b }, c }, d }}}
     *
     * Sounds conter-intuitive, why it is different from 01 knapsack? Well in 01 knapsack we just need to compute
     * max { a, b }. Actually this tricky case will happens for any problem with max { a, b, ... }
     */
    static int knapsack() {
        int[][] dp = new int[N+1][V+1];
        for(int i=1; i<=N; i++) {
            for(int j=1; j<=V; j++) {
                dp[i][j] = dp[i-1][j];
                for(int k=1; k<=v.get(i-1).size(); k++) {
                    if(j>=v.get(i-1).get(k-1)) {
                        dp[i][j] = Math.max(dp[i][j], dp[i-1][j-v.get(i-1).get(k-1)]+w.get(i-1).get(k-1));
                    }
                }
            }
        }
        return dp[N][V];
    }
    
    /* 2) Standard DP using 1D rolling array
     *
     * Noted that in rolling array, we need to count V towards 1, not like 01 knapsack
     * And we cannot swap the loop order by looping group items first then loop through volumn
     * that is wrong in logic, which means 01 Knapsack problem given all items from all groups
     * instead of picking at most one item per group.
     */
    static int knapsack1D() {
        int[] dp = new int[V+1];
        for (int i=0; i<N; i++) {
            for (int j=V; j>=1; j--) {
                for (int k=0; k<v.get(i).size(); k++) {
                    if (j>=v.get(i).get(k)) {
                         dp[j] = Math.max(dp[j], dp[j-v.get(i).get(k)]+w.get(i).get(k));
                    }
                }
            }
        }
        return dp[V];
    }
}
