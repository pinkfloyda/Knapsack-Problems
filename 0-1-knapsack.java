import java.util.*;
import java.io.*;

// 1) Brute-force using DFS + Memoization
// 2) DP using 2D array + Optimization
// 3) DP using 1D array + Optimization

public class Main {

    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        // Read count and capacity
        String[] s = in.readLine().split(" ");
        int count = Integer.parseInt(s[0]);
        int capacity = Integer.parseInt(s[1]);
        int[] sizes = new int[count];
        int[] values = new int[count];

        // Read size and value for each item
        for(int i=0; i<count; i++) {
            s = in.readLine().split(" ");
            sizes[i] = Integer.parseInt(s[0]);
            values[i] = Integer.parseInt(s[1]);
        }

        int output = knapsackDP(count, capacity, sizes, values, true);
        System.out.println(output);
    }

    // The basic idea is for each item, decide if pick or not pick each item
    // It can be visualized into a decision tree, where the leaves are the enumerations of all combinations
    // Theoretically, just compare the values of all the combinations and pick the max value
    // In recursion, such comparison is done by collapsing (using Math.max) branches from leaves towards the root
    //
    // But the above is clearly O(2^N) where N is item count, actually the decision tree branch has overlapping
    // e.g. same capacity and iteration index, we can use memo to memorize it instead of going down the branch again
    // We only compute if no entry logged in memo, so at most we fill all entries of memo, which is capacity*N, so optimization
    // is O(C*N) where C is knapsack capacity and N is itemCount.
    //
    // To get a feel of the optimization, if there are 100 items to choose and capacity is 1,000,000
    // then 2^100 is 1267650600228229401496703205376 (31 digits), but 1,000,000 * 100 is only 100,000,000 (9 digits)
    // feel the difference!
    private static int knapsackRecursion(int count, int capacity, int[] sizes, int[] values) {
        int[][] memo = new int[capacity+1][count];
        for (int i=0; i<capacity+1; i++) {
            for (int j=0; j<count; j++) {
                memo[i][j] = -1;
            }
        }
        for (int i=0; i<count; i++) {
            memo[0][i] = 0; // for ZERO capacity there is nothing we can put, so initlize to be 0
        }
         // Count from end to start, can also count from start to end, but not straight-forward when converting to DP solution
        return knapsackRecursion(capacity, sizes, values, count-1, memo);
    }

    private static int knapsackRecursion(int capacity, int[] sizes, int[] values, int i, int[][] memo) {
        if (i < 0) {
            return 0;
        }

        if (memo != null && memo[capacity][i] >= 0) {
            return memo[capacity][i];
        }

        // The value if don't pick ith item
        int ans1 = knapsackRecursion(capacity, sizes, values, i-1, memo);
        // The value if pick ith item
        int ans2 = sizes[i] <= capacity ? (values[i] + knapsackRecursion(capacity-sizes[i], sizes, values, i-1, memo)) : 0;

        int ans = Math.max(ans1, ans2);

        if (memo != null) {
            memo[capacity][i] = ans;
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
    // which is a sub-problem, if say each entry is optimal, then entry(i,j) = max(entry(i-1,j), entry(i-1, j-c(i)) + w(i))
    // will also be optimal. Inducttively, it will lead to final optimal solution in entry(C,N).
    // Instead of DFS+Memoization way of thiking, this thought process is truely Dynamic Programming
    //
    // There is one optimization step, because we are only interested in the right-bottom corner of the table, that is dp[count][capacity]
    // To compute dp[count][capacity], it only lookup dp[count-1][capacity] and dp[count-1][capacity-lastC], dp[count-1][0..capacity-lastC-1] will not be used
    // Likewise to compute dp[count-1][capacity-lastC], it only lookup dp[count-2][capacity-lastC] and dp[count-2][capacity-lastC-2ndLastC] and
    // dp[count-2][0..capacity-lastC-2ndLastC-1] won't be used. So for each row i, when update, we can start from capacity of C-sum(i..N)
    // To visualize it, the table is updated in a some-what upper triangular fashion
    private static int knapsackDP(int count, int capacity, int[] sizes, int[] values, boolean optimize) {
        // initialize all to be 0s, especially for dp[0][j] (knapsack with ZERO capacity) and dp[i][0] (ZERO items to choose from)
        int[][] dp = new int[count+1][capacity+1];
        int remainingSum = 0;
        if (optimize) {
          for (int i=1; i<=count; i++) {
              remainingSum += sizes[i-1];
          }
        }
        for (int i=1; i<=count; i++) {
            int startCapacity = 1;
            if (optimize) {
                remainingSum -= sizes[i-1];
                startCapacity = capacity > remainingSum ? (capacity - remainingSum) : 1;
            }
            for (int j=startCapacity; j<=capacity; j++) {
                // The below code is very like the recursive version, but recursive call replaced with
                // dp array access. The memo of the recursive call is populated the same way as dp here

                // Don't pick item i-1 into knapsack
                int ans1 = dp[i-1][j];
                // Pick item i-1 into knapsack
                int ans2 = sizes[i-1] <= j ? (values[i-1] + dp[i-1][j-sizes[i-1]]) : 0;
                dp[i][j] = Math.max(ans1, ans2);
            }
        }
        return dp[count][capacity];
    }

    // Observe above that for each count dp[0..i][j], it only gets accessed by j+1
    // It will be clearer if we swap the capacity and count, where table's rows are counts and columns are capacity
    // then the outer for loop will be count and inner for loop will be capacity. During execution, each row only look up
    // data of previous row, so we can collapse the 2-D array into a rolling 1-D array which represents the current row (count)
    //
    // But there is another problem, when update the array for current count, it will lookup smaller capacity value (of previous count),
    // if we go from left-to-right to update the array, smaller capacity's value will be overriten
    // to avoid that, we need to update the array from right-to-left, which will preserve the smaller capacity's value (of previous count)
    // Hard to explain more clear in text, just visualize how the table/array is getting updated
    //
    // Similarly, the optimization is same as above
    private static int knapsackDPRolling(int count, int capacity, int[] sizes, int[] values, boolean optimize) {
        // initialize all to be 0s, especially for dp[0] (capacity is ZERO)
        int[] dp = new int[capacity+1];
        int remainingSum = 0;
        if (optimize) {
            for (int i=1; i<=count; i++) {
                remainingSum += sizes[i-1];
            }
        }
        for (int i=1; i<=count; i++) {
            int startCapacity = sizes[i-1]; // smaller capacity means guareent not pick item i-1, just leave it
            if (optimize) {
                remainingSum -= sizes[i-1];
                startCapacity = Math.max(sizes[i-1], capacity-remainingSum);
            }
            for (int j=capacity; j>=startCapacity; j--) {
                // Don't pick item i-1 into knapsack
                int ans1 = dp[j];
                // Pick item i-1 into knapsack
                int ans2 = values[i-1] + dp[j-sizes[i-1]];
                dp[j] = Math.max(ans1, ans2);
            }
        }
        return dp[capacity];
    }
}