/* Bounded-Knapsack Problem
 *
 * Given a knapsack with volumn V and N items where each item has volumn v[i], weight w[i]
 * and quantity s[i], what's the max total weight by putting items into knapsack without 
 * exceeding its volumn.
 * 
 * Input:
 * First line has two integers: N and V, seperated by space
 * Each of next N lines has three integers: item's volumn, weight and quantity, sperated by space
 * 
 * Output:
 * One line with one integer: max total item weight
 *
 * Sample Input:
 *   4 5
 *   1 2 3
 *   2 4 1
 *   3 4 3
 *   4 5 2
 * Sample Output:
 *   10
 */

import java.util.*;

public class Main {
    
    static int N;
    static int V;
    static int[] v;
    static int[] w;
    static int[] s;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        N = sc.nextInt();
        V = sc.nextInt();
        v = new int[N];
        w = new int[N];
        s = new int[N];

        for(int i=0; i<N; i++) {
            v[i] = sc.nextInt();
            w[i] = sc.nextInt();
            s[i] = sc.nextInt();
        }
        
        int res = knapsackBinary();
        
        System.out.println(res);
    }
 
    /* 1) Standard DP, time O(VΣs[i]) ≈ O(VN^2)
     *
     * dp[i][j] = max{ dp[i-1][j-k*v[i]]+k*w[i], 0<=k<=s[i] && k*v[i]<=j }
     */
    static int knapsack() {
        int[] dp = new int[V+1];
        for (int i=1; i<=N; i++) {
            for (int j=V; j>=v[i-1]; j--) {
                for (int k=1, v2=v[i-1], w2=w[i-1]; v2<=j && k<=s[i-1]; v2+=v[i-1], w2+=w[i-1], k++) {
                    dp[j] = Math.max(dp[j], dp[j-v2]+w2);
                }
            }
        }
        return dp[V];
    }
    
    /* 2) DP with binary divide, time O(VΣlogs[i]) ≈ O(VlogN)
     *
     * Can consider binary divide them into 1,2,4,...
     * But the tricky part is the possible max number can be represented cannot exceed s[i].
     * So it must be of values 1,2,2^2,...2^(k-1),s[i]-2^k+1 where k is the largest value to make 2^k-1 < s[i]
     * In this way, we can represent number of range [1,s[i]]
     *
     * s[i] can be represented by adding all numbers 1,2,2^2,...2^(k-1),s[i]-2^k+1, which is the max possible number
     * And using 1,2,4....2^(k-1) can represent any number from 1 to 2^k-1, can we represent any number between
     * 2^k and s[i]? Yes, number between 2^k and s[i] can be represented by s[i]-M, where M can be as large as
     * s[i]-2^k which is smaller or equal to 2^k-1 (if not, then  2^(k+1)-1 < s[i], which is wrong as k is largest 
     * value to make 2^k-1 < s[i]), noted s[i] is total sum, so number between 2^k and s[i] is the sum(allItems - itemsCanSumEqM)
     *   1              2^k-1   s[i]     2^(k+1)-1
     *   A----------------B-------C----------D
     */
    static int knapsackBinary() {
        int[] dp = new int[V+1];
        for(int i=1; i<=N; i++) {
            int k = 2;
            for(int v2=v[i-1], w2=w[i-1]; k-1<s[i-1]; v2*=2, w2*=2, k*=2) {
                for(int j=V; j>=v2; j--) {
                    dp[j] = Math.max(dp[j], dp[j-v2]+w2);
                }
            }
            k = s[i-1] - k/2 + 1;
            for(int j=V, v2=k*v[i-1], w2=k*w[i-1]; j>=v2; j--) {
                dp[j] = Math.max(dp[j], dp[j-v2]+w2);
            }
        }
        return dp[V];
    }
}
