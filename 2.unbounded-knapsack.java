/* Unbounded-Knapsack Problem
 *
 * You have a knapsack with budget C and N items can be picked into knapsack,
 * each (i-th) item has cost ci, value vi and can be picked unlimited times.
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
 *   10
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
        
        int res = knapsackOptimize1D();
        
        System.out.println(res);
    }

    /* 1) Standard DP using 2D array, time O(NCC), space O(NC)
     *
     * Similar to 01 knapsack problem, dp[i][j] stands for the max value picking from 
     * first i items without exceeding cost j. But this time, when encounter ith item
     * the choice is not pick vs non-pick, the choice is pick-n-times (n==0 means non-pick)
     * 
     * So the state transition is dp[i][j]=max{ dp[i-1][C-k*c[i]]+k*v[i] } (1<=j<=C, 0<=k<=j/c[i])
     */
    static int knapsack() {
        int[][] dp = new int[N+1][C+1];
        for(int i=1; i<=N; i++) {
            for(int j=1; j<=C; j++) {
                for(int k=0; k<=j/c[i-1]; k++) {
                    dp[i][j] = Math.max(dp[i][j], dp[i-1][j-k*c[i-1]] + k*v[i-1]);
                }
            }
        }
        return dp[N][C];
    }
    
    /* 2) Item-Divide DP using 2D array, time O(NCC), space O(NC)
     *
     * This is an alternative thinking process, we can expand the total items by divide each
     * item with multiple items with different costs k*c[i] and values k*v[i]
     * Then with these expanded items, we can apply 01 knapsack problem
     * 
     * In implmentation, the first two loops simulate we iterate over the expanded items and 
     * then apply 01 knapsack algorithm
     
     * Time comlexity not changed but worse than 1) because when we divide items, we just divide
     * them as many as possible util single item's cost not exceed C 
     */
    static int knapsackDivide() {
        int[][] dp = new int[N+1][C+1];
        for(int i=1; i<=N; i++) {
            for(int c2=c[i-1], v2=v[i-1]; c2<=C; c2+=c[i-1], v2+=v[i-1]) {
                for(int j=1; j<=C; j++) {
                    dp[i][j] = dp[i-1][j];
                    if(j >= c2) {
                        dp[i][j] = Math.max(dp[i][j], dp[i-1][j-c2] + v2);
                    }
                }
            }
        }
        return dp[N][C];
    }
    
    /* 3) Item-Binary-Divide DP using 1D rolling array, time O(NClogC), space O(C)
     *
     * Continued from above item-divide thinking process, but we can divide item in a smarter
     * way: divide item into k items that each item has cost (2^k)*c[i] and value (2^k)*v[i]
     * where k starts from zero until the max value that make (2^k)*c[i] <= C
     *
     * This works because combinations of 1,2,4,....2^k can represent any number from 1 to 2^(k+1)-1
     * And time complexity improves as the total number of expanded items to iterate is NlogC
     */
    static int knapsackBinaryDivide() {
        int[] dp = new int[C+1];
        for(int i=1; i<=N; i++) {
            for(int c2=c[i-1], v2=v[i-1]; c2<=C; c2*=2, v2*=2) {
                for(int j=C; j>=c2; j--) {
                    dp[j] = Math.max(dp[j], dp[j-c2] + v2);
                }
            }
        }
        return dp[C];
    }
    
    /* 4) Optimized DP using 2D array, time O(NC), space O(NC)
     *
     * In standard DP, state transition is dp[i][j]=max{ dp[i-1][j-k*c[i]]+k*v[i], 0<=k<=j/c[i]]}
     * dp[i][j] needs to access dp[i-1][j], dp[i-1][j-c[i]], dp[i-1][j-2*c[i]], ...dp[i-1][1]
     * dp[i][j-c[i]] needs to access        dp[i-1][j-c[i]], dp[i-1][j-2*c[i]], ...dp[i-1][1]
     * To compute dp[i][j], we just need to access dp[i-1][j] and dp[i][j-c[i]]
     * So the new DP transition is dp[i][j]=max{ dp[i-1][j], dp[i][j-c[i]]+v[i] }
     */
    static int knapsackOptimize() {
        int[][] dp = new int[N+1][C+1];
        for (int i=1; i<=N; i++) {
            for (int j=1; j<=C; j++) {
                dp[i][j] = dp[i-1][j];
                if(j >= c[i-1]) {
                    dp[i][j] = Math.max(dp[i][j], dp[i][j-c[i-1]] + v[i-1]);
                }
            }
        }
        return dp[N][C];
    }
    
    /* 5) Optimized DP using 1D rolling array, time O(NC), space O(C)
     * 
     * Each row only access previous and current row, so can just 1D rolling array
     * But unlike 01 knapsack, it must update from left to right, because the former updated dp values
     * are needed by later ones. 
     * dp[j](new-value) = max{ dp[j](old-value), dp[j-c[i]](new-value)+v[i] }
     *
     * Noted that the inner loop need to start from c[i-1] so those less than c[i-1] just inherit the old values
     */
    static int knapsackOptimize1D() {
        int[] dp = new int[C+1];
        for (int i=1; i<=N; i++) {
            for (int j=c[i-1]; j<=C; j++) {
                dp[j] = Math.max(dp[j], dp[j-c[i-1]] + v[i-1]);
            }
        }
        return dp[C];
    }
}
