/* Group-Knapsack Problem
 *
 * You have a knapsack with budget C, and N groups of items to be picked into knapsack,
 * each (i-th) group has n[i] items, each (j-th item in i-th group) item has cost c[i][j] 
 * and value v[i][j], at most one item can be picked per group.
 * What is the max value you can get without exceeding budget?
 * 
 * Input:
 * First line has two integers: N and C, seperated by space
 * Then followed by N groups of lines, for each group:
 *   The first line has one integer: n, the number of items in this group
 *   Then followed by next n lines representing items:
 *     each line has two integers: item's cost and value, sperated by space
 * 
 * Output:
 * One line with one integer: max value
 *
 * Data Range:
 * 0 < N,C ≤ 100
 * 0 < n[i] ≤100
 * 0 < c[i][j],v[i][j] ≤ 100
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
    static int C;
    static List<List<Integer>> c = new ArrayList<>();
    static List<List<Integer>> v = new ArrayList<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        N = sc.nextInt();
        C = sc.nextInt();

        for(int i=0; i<N; i++) {
            int n = sc.nextInt();
            c.add(new ArrayList<>());
            v.add(new ArrayList<>());
            for(int j=0; j<n; j++) {
                c.get(i).add(sc.nextInt());
                v.get(i).add(sc.nextInt());
            }
        }
        
        int res = knapsack1D();
        
        System.out.println(res);
    }
 
    /* 1) Standard DP using 2D array
     *
     * The thiking process if very similar to unbounded and bounded knapsack problem, but iterate over groups
     * for ith group, there are multiple choices: pick which item within the group.
     * So state transition is dp[i][j] = max{ dp[i-1][j], dp[i-1][j-c[i][k]]+v[i][k], k is group's item index }
     *
     * But when implement using 2D array, dp[i-1][j] needs to move out to 2nd loop instead of in most inner loop
     * e.g. the answer should be max { a, b, c }, if we move into most inner loop, answser will be max { a, c } only
     * which is wrong; we need to compute it as max { max { a b }, c }
     *
     * Sounds conter-intuitive, why it is different from 01 knapsack? Well in 01 knapsack we just need to compute
     * max { a, b }. Actually this tricky case will happens for any problem with max of more than 2 items
     *
     * Rolling 1D array will not have such problem because in a rolling array, dp[j] automatically inherit the 
     * previous value, so no need explicit copy like 2D array.
     */
    static int knapsack() {
        int[][] dp = new int[N+1][C+1];
        for(int i=1; i<=N; i++) {
            for(int j=1; j<=C; j++) {
                dp[i][j] = dp[i-1][j];
                for(int k=1; k<=c.get(i-1).size(); k++) {
                    if(j >= c.get(i-1).get(k-1)) {
                        dp[i][j] = Math.max(dp[i][j], dp[i-1][j-c.get(i-1).get(k-1)]+v.get(i-1).get(k-1));
                    }
                }
            }
        }
        return dp[N][C];
    }
    
    /* 2) Standard DP using 1D rolling array
     *
     * Noted that in rolling array, we need to count C towards 1 as we don't know the minimum
     * costs of items in a group yet
     *
     * And we cannot swap the loop order by looping group items first then loop through costs
     * which is wrong in logic. In that way, it is actually 01 Knapsack problem given all items 
     * from all groups, which can choose multiple items per group
     */
    static int knapsack1D() {
        int[] dp = new int[C+1];
        for (int i=0; i<N; i++) {
            for (int j=C; j>=1; j--) {
                for (int k=0; k<c.get(i).size(); k++) {
                    if (j >= c.get(i).get(k)) {
                         dp[j] = Math.max(dp[j], dp[j-c.get(i).get(k)]+v.get(i).get(k));
                    }
                }
            }
        }
        return dp[C];
    }
}
