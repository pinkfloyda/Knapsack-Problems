/* Bounded-Knapsack Problem
 *
 * You have a knapsack with budget C and N items can be picked into knapsack,
 * each (i-th) item has cost ci, value vi and can be picked at most ni times.
 * What is the max value you can get without exceeding budget?
 * 
 * Input:
 * First line has two integers: N and C, seperated by space
 * Then followed by next N lines representing items:
 *   each line has three integers: item's cost, value and count, sperated by space
 * 
 * Output:
 * One line with one integer: max value
 *
 * Data range:
 * 0 < N,C ≤ 100
 * 0 < ci,vi,ni ≤ 100
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
        
        int res = knapsackBinaryDivide();
        
        System.out.println(res);
    }
 
    /* 1) Standard DP using 1D rolling array, time O(CΣn[i]) ≈ O(CNN), space O(C)
     *
     * Same thinking process as unbounded knapsack, just for ith item, can have mulitple choices
     * with different costs and values.
     * dp[i][j] = max{ dp[i-1][j-k*c[i]]+k*v[i], 0<=k<=n[i] && k*c[i]<=j }
     */
    static int knapsack() {
        int[] dp = new int[C+1];
        for(int i=1; i<=N; i++) {
            for(int j=C; j>=c[i-1]; j--) {
                for(int k=1, c2=c[i-1], v2=v[i-1]; c2<=j && k<=n[i-1]; c2+=c[i-1], v2+=v[i-1], k++) {
                    dp[j] = Math.max(dp[j], dp[j-c2] + v2);
                }
            }
        }
        return dp[C];
    }
    
    /* 2) Item-Binary-Divide DP using 1D rolling array, time O(VΣlogn[i]) ≈ O(CNlogN), space O(C)
     *
     * Noted that this problem cannot do a simple item divide into 1,2,....k items like unbounded knapsack
     * In that way, it may end up pick ith items more than n[i], so need below binary divide methods
     *
     * Can consider binary divide them into 1,2,4,...
     * But the tricky part is the possible max number can be represented cannot exceed n[i].
     * So it must be of values 1,2,2^2,...2^(k-1),n[i]-2^k+1 where k is the largest value to make 2^k-1 < n[i]
     * In this way, we can represent number of range [1,n[i]]
     *
     * n[i] can be represented by adding all numbers 1,2,2^2,...2^(k-1),n[i]-2^k+1, which is the max possible number
     * And using 1,2,4....2^(k-1) can represent any number from 1 to 2^k-1, can we represent any number between
     * 2^k and n[i]-1? Yes, number between 2^k and n[i]-1 can be represented by n[i]-M, where M should be as large as
     * n[i]-2^k which is <= 2^k-1 (if not, then 2^(k+1)-1 < n[i], which cannot happen as k is largest 
     * value to make 2^k-1 < n[i]). n[i] is total sum, so number between 2^k and n[i]-1 is the sum of (allItems - itemsCanSumEqM)
     *   1              2^k-1   s[i]     2^(k+1)-1
     *   A----------------B-------C----------D
     */
    static int knapsackBinaryDivide() {
        int[] dp = new int[C+1];
        for(int i=1; i<=N; i++) {
            int m = n[i-1];
            for(int k=1, c2=c[i-1], v2=v[i-1]; m-k>0; c2*=2, v2*=2, k*=2) {
                for(int j=C; j>=c2; j--) {
                    dp[j] = Math.max(dp[j], dp[j-c2] + v2);
                }
                m -= k; 
            }
            for(int j=C, c2=m*c[i-1], v2=m*v[i-1]; j>=c2; j--) {
                dp[j] = Math.max(dp[j], dp[j-c2] + v2);
            }
        }
        return dp[C];
    }
    
    /* 3) Optimized DP using sliding window with monotone deque, time O(NC), space O(C)
     *
     * Consider 1D rolling array state transition
     * dp[j] = max{                                             dp[j-mc]+mv, dp[j-(m-1)c]+(m-1)v, ..., dp[j-2c]+2v, dp[j-c]+v, dp[j] }
     * dp[j-c]+v = max{                    dp[j-(m+1)c]+(m+1)v, dp[j-mc]+mv, dp[j-(m-1)c]+(m-1)v, ..., dp[j-2c]+2v, dp[j-c]+v }
     * dp[j-2c]+2v = max{ dp[j-(m+2)c]+mv, dp[j-(m+1)c]+(m+1)v, dp[j-mc]+mv, dp[j-(m-1)c]+(m-1)v, ..., dp[j-2c]+2v }
     * ...
     * 
     * So it is observed that it looks like a sliding window with max length of n[i], if we update 1D rolling array from right to left, the sliding window
     * will slide from right to left. If sliding window is full, each slide will cause new item in front added and last item in back dropped 
     * Now this problem is to find sliding window max value, which can be solved using a monotone deque where the front the deque always the largest
     * When add item to the deque, first remove any smaller items from last, then add to the last
     * When remove item from the deque, check if item is same value of deque's first, if so, remove the first item
     * In this way, the deque always contains items starting from largest
     *
     * And each sliding window will cover the sequence j, j-c, j-2c, .... max(j-mc, 0), we need to do this starting with all
     * [j, j-c), so it can cover sequence j+1, j+1-c, ...; j+2, j+2-c, ....;
     */
    static int knapsackOptimize() {
        int[] dp = new int[C+1];
        for(int i=1; i<=N; i++) {
            for(int j=C; j>C-c[i-1] && j>0; j--) {
                Deque<Integer> q = new LinkedList<>();
                int hi = 0;
                int lo = 0;
                while(j-hi*c[i-1] >= 0) {
                    if(hi-lo<n[i-1]) {
                        enqueue(q, dp[j-hi*c[i-1]]+hi*v[i-1]);
                        hi++;
                    } else {
                        enqueue(q, dp[j-hi*c[i-1]]+hi*v[i-1]);
                        int max = q.peekFirst() - lo*v[i-1];
                        dequeue(q, dp[j-lo*c[i-1]]+lo*v[i-1]);
                        dp[j-lo*c[i-1]] = max;
                        lo++;
                        hi++;
                    }
                }
                while(!q.isEmpty()) {
                    int max = q.peekFirst() - lo*v[i-1];
                    dequeue(q, dp[j-lo*c[i-1]]+lo*v[i-1]);
                    dp[j-lo*c[i-1]] = max;
                    lo++;
                }
            }
        }
        return dp[C];
    }
    
    static void enqueue(Deque<Integer> q, int v) {
        while(!q.isEmpty()) {
            if(q.peekLast() >= v) {
                break;
            }
            q.pollLast();
        }
        q.offerLast(v);
    }
    
    static void dequeue(Deque<Integer> q, int v) {
        if(!q.isEmpty() && q.peekFirst() == v) {
            q.pollFirst();
        }
    }
}
