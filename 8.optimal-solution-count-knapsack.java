/* Optimal-Solution-Count-Knapsack Problem
 *
 * You have a knapsack with budget C and N items can be picked into knapsack,
 * each (i-th) item has cost ci, value vi and can only be picked once.
 * How many optimal picking solutions that can get max value without exceeding budget?
 * 
 * Input:
 * First line has two integers: N and C, seperated by space
 * Then followed by next N lines representing items:
 *   each line has two integers: item's cost and value, sperated by space
 * 
 * Output:
 * One line with one integer: number of optimal solutions % 1000000007
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
 *   2
 */

import java.util.*;

public class Main {
    static int M = 1000000007;
    
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
        
        int res = knapsack();
        
        System.out.println(res);
    }
    
    static int knapsack() {
        int[][] dp = new int[C+1][2]; // [0] means max value, [1] means solution count
        
        for(int i=0; i<=C; i++) {
            dp[i][1] = 1; // initially, for knapsack with 0 volumn, the only optimal solution is don't put any
        }
        
        for(int i=1; i<=N; i++) {
            for(int j=C; j>=c[i-1]; j--) {
                if(dp[j][0] == dp[j-c[i-1]][0] + v[i-1]) {
                    dp[j][1] = (dp[j][1] + dp[j-c[i-1]][1]) % M;
                } else if(dp[j][0] < dp[j-c[i-1]][0] + v[i-1]) {
                    dp[j][0] = dp[j-c[i-1]][0] + v[i-1];
                    dp[j][1] = dp[j-c[i-1]][1];
                }
                // if dp[j][0] > dp[j-c[i-1]][0] + v[i-1], don't need to do anything
                // as dp[j][*] just inherit the previous value
            }
        }
        
        return dp[C][1];
    }
}
