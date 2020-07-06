import java.util.*;
import java.io.*;

/*
Input format:

First line is two integers N and C, representing unique item count and knapsack capacity, seperated by space
Following N lines where each line is two integers si and vi, representing ith item's size and value

Output format:
One line of one integer, which is the final result: the possible max value
*/

// 1) Brute-force using DFS + Memoization
// 2) DP using 2D array + Optimization
// 3) DP using 1D array + Optimization

public class Main {

    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        // Read N and C
        String[] s = in.readLine().split(" ");
        int N = Integer.parseInt(s[0]); // unique item count
        int C = Integer.parseInt(s[1]); // knapsack capacity
        int[] ss = new int[N]; // size array for each item
        int[] vs = new int[N]; // value array for each item

        // Read size and value for each item
        for(int i=0; i<N; i++) {
            s = in.readLine().split(" ");
            ss[i] = Integer.parseInt(s[0]);
            vs[i] = Integer.parseInt(s[1]);
        }

        // Output result
        System.out.println(knapsackDP(N, C, ss, vs, true));
    }

    // The basic idea is for each item, decide if pick or not pick each item
    // It can be visualized into a decision tree, where the leaves are the enumerations of all combinations
    // Theoretically, just compare the values of all the combinations and pick the max value
    // In recursion, such comparison is done by collapsing (using Math.max) branches from leaves towards the root
    //
    // But the above is clearly O(2^N) where N is item count, actually the decision tree branch has overlapping
    // e.g. same capacity and iteration index, we can use memo to memorize it instead of going down the branch again
    // We only compute if no entry logged in memo, so at most we fill all entries of memo, which is of size N*C, so
    // it is O(N*C) where N is item count and C is knapsack capacity
    //
    // To get a feel of the optimization, if there are 100 items to choose and capacity is 1,000,000
    // then 2^100 is 1267650600228229401496703205376 (31 digits), but 1,000,000 * 100 is only 100,000,000 (9 digits)
    // feel the difference!
    private static int knapsackRecursion(int N, int C, int[] ss, int[] vs, boolean optimize) {
        int[][] memo = null;

        if (optimize) {
            memo = new int[N][C+1];
            for (int i=0; i<N; i++) {
                for (int j=0; j<C+1; j++) {
                    memo[i][j] = -1; // -1 means no value
                }
            }
            for (int i=0; i<N; i++) {
                memo[i][0] = 0; // for ZERO capacity there is nothing we can put, so initialize to be 0
            }
        }

        // Count from end to start, can also count from start to end, but not straight-forward when converting to DP solution
        return knapsackRecursion(ss, vs, N-1, C, memo);
    }

    private static int knapsackRecursion(int[] ss, int[] vs, int i, int c, int[][] memo) {
        if (i < 0) {
            return 0;
        }

        if (memo != null && memo[i][c] >= 0) {
            return memo[i][c];
        }

        // The value if don't pick ith item
        int ans1 = knapsackRecursion(ss, vs, i-1, c, memo);

        // The value if pick ith item
        int ans2 = ss[i] <= c ? (vs[i] + knapsackRecursion(ss, vs, i-1, c-ss[i], memo)) : 0;

        int ans = Math.max(ans1, ans2);

        if (memo != null) {
            memo[i][c] = ans;
        }

        return ans;
    }

    // It can be seen from recursive solution above, the big problem is composed of sub smaller problems in iterms of
    // capacity and item index and filling the memo is like filling a table where columns are items whose index increase
    // from left to right and rows are capacity which increases from top to bottom
    // The table is filled with order from left to right and top to bottom
    //
    // Actually the above table can come to mind directly or naturally (without thiking of sub-problem in recursive way).
    // Each entry (i,j) of the table represents the maximum value of trying to put (first) i items into a knapsack with capacity j,
    // which is a sub-problem, if say each entry is optimal, then entry(i,j) = max(entry(i-1,j), entry(i-1, j-c(i)) + v(i))
    // will also be optimal. Inducttively, it will lead to final optimal solution in entry(N,C).
    // Instead of DFS+Memoization way of thiking, this thought process is truely Dynamic Programming
    //
    // There is one optimization step, because we are only interested in the right-bottom corner of the table, that is dp[N][C]
    // To compute dp[N][C], it only lookup dp[N-1][C] and dp[N-1][C-c(N-1)], dp[N-1][0..C-c(N-1)) will not be used
    // Likewise to compute dp[N-1][C-c(N-1)], it only lookup dp[N-2][C-c(N-1)] and dp[N-2][C-c(N-1)-c(N-2)] and
    // dp[N-2][0..C-c(N-1)-c(N-2)) won't be used. So for each row i, when update, we can start from capacity of C-sum(i..N)
    // To visualize it, the table is updated in a some-what upper triangular fashion
    private static int knapsackDP(int N, int C, int[] ss, int[] vs, boolean optimize) {
        // initialize all to be 0s, especially for dp[i][0] (knapsack with ZERO capacity) and dp[0][j] (ZERO items to choose from)
        int[][] dp = new int[N+1][C+1];
        int remainingSum = 0;
        if (optimize) {
            for (int i=1; i<=N; i++) {
                remainingSum += ss[i-1];
            }
        }
        for (int i=1; i<=N; i++) {
            int startCapacity = 1;
            if (optimize) {
                remainingSum -= ss[i-1];
                startCapacity = C > remainingSum ? (C - remainingSum) : 1;
            }
            for (int j=startCapacity; j<=C; j++) {
                // The below code is very like the recursive version, but recursive call replaced with
                // dp array access. The memo of the recursive call is populated the same way as dp here

                // Don't pick item i-1 into knapsack
                int ans1 = dp[i-1][j];
                // Pick item i-1 into knapsack
                int ans2 = ss[i-1] <= j ? (vs[i-1] + dp[i-1][j-ss[i-1]]) : 0;
                dp[i][j] = Math.max(ans1, ans2);
            }
        }
        return dp[N][C];
    }

    // Observe above that for each count dp[i][0..j], it only gets accessed by i+1
    // During execution, each row only look up data of previous row, so we can collapse
    // the 2-D array into a rolling 1-D array which represents the current row (count)
    //
    // But there is another problem, when update the array for current count (i), it will lookup smaller capacity value (of i-1),
    // if we go from left-to-right to update the array, smaller capacity (of i-1)'s value will be overriten
    // to avoid that, we need to update the array from right-to-left, which will preserve the smaller capacity's value (of i-1)
    //
    // Similarly, the optimization is same as above
    private static int knapsackDPRolling(int N, int C, int[] ss, int[] vs, boolean optimize) {
        // initialize all to be 0s, especially for dp[0] (capacity is ZERO)
        int[] dp = new int[C+1];
        int remainingSum = 0;
        if (optimize) {
            for (int i=1; i<=N; i++) {
                remainingSum += ss[i-1];
            }
        }
        for (int i=1; i<=N; i++) {
            int startCapacity = ss[i-1]; // smaller capacity means guareent not pick item i-1, just leave it
            if (optimize) {
                remainingSum -= ss[i-1];
                startCapacity = Math.max(ss[i-1], C-remainingSum);
            }
            for (int j=C; j>=startCapacity; j--) {
                // Don't pick item i-1 into knapsack
                int ans1 = dp[j];
                // Pick item i-1 into knapsack
                int ans2 = vs[i-1] + dp[j-ss[i-1]];
                dp[j] = Math.max(ans1, ans2);
            }
        }
        return dp[C];
    }
}