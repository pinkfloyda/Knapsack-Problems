/* Mixed-Knapsack Problem
 *
 * You have a knapsack with budget C and N items can be picked into knapsack,
 * each (i-th) item has cost ci, value vi and some items can be picked once, 
 * some can be picked unlimited times and some can be picked at most n[i] times
 * What is the max value you can get without exceeding budget?
 * 
 * 
 * Input:
 * First line has two integers: N and C, seperated by space
 * Then followed by next N lines representing items:
 *   each line has three integers: item's cost, value and count, sperated by space
 *     count == -1 means this item can be picked once
 *     count == 0 means this item can be picked unlimited times
 *     count > 0 means this item can be picked at most count times
 * 
 * Output:
 * One line with one integer: max value
 * 
 * Data range:
 * 0 < N,C ≤ 1000 
 * 0 < ci,vi ≤ 1000
 * −1 ≤ ni ≤ 1000
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
    static int C;
    static int[] c;
    static int[] v;
    static int[] n;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        N = sc.nextInt();
        C = sc.nextInt();
        c = new int[N];
        v = new int[N];
        n = new int[N];

        for(int i=0; i<N; i++) {
            c[i] = sc.nextInt();
            v[i] = sc.nextInt();
            n[i] = sc.nextInt();
        }
        
        int res = knapsack();
        
        System.out.println(res);
    }
 
    /* 1) Standard DP
     *
     * Just combine solutions based on the kind of item picking contraints
     */
    static int knapsack() {
        int[] dp = new int[C+1];
        for (int i=1; i<=N; i++) {
            if(n[i-1] < 0) { // using 01 knapsack solution
                for (int j=C; j>=c[i-1]; j--) {
                    dp[j] = Math.max(dp[j], dp[j-c[i-1]]+v[i-1]);
                }
            } else if(n[i-1] == 0) { // using unbounded knapsack solution
                for (int j=c[i-1]; j<=C; j++) {
                    dp[j] = Math.max(dp[j], dp[j-c[i-1]]+v[i-1]);
                }
            } else { // using bounded knapsack solution
                int m = n[i-1];
                for(int k=1, c2=c[i-1], v2=v[i-1]; m-k>0; c2*=2, v2*=2, k*=2) {
                    for(int j=C; j>=c2; j--) {
                        dp[j] = Math.max(dp[j], dp[j-c2]+v2);
                    }
                    m -= k;
                }
                for(int j=C, c2=m*c[i-1], v2=m*v[i-1]; j>=c2; j--) {
                    dp[j] = Math.max(dp[j], dp[j-c2]+v2);
                }
            }
        }
        return dp[C];
    }
}
