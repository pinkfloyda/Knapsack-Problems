import java.util.*;
import java.io.*;

/*
Input format:

First line is two integers N and C, representing unique item group count and knapsack capacity, seperated by space
The each of following N lines got three intergers si, vi and di, representing size, value and depent item index (1<=di<=N, -1 means no dependency)

Output format:
One line of one integer, which is the final result: the possible max value
*/

public class Main {

    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        // Read N and C
        String[] s = in.readLine().split(" ");
        int N = Integer.parseInt(s[0]); // unique group count
        int C = Integer.parseInt(s[1]); // knapsack capacity

        int[] ss = new int[N];
        int[] vs = new int[N];

        List<Integer>[] tree = new List[N+1]; // index is item's index + 1, tree[0] is root, value is List of children item index
        Arrays.setAll(tree, e -> new ArrayList<>());

        // Read size and value for each item
        for (int i=0; i<N; i++) {
            s = in.readLine().split(" ");
            ss[i] = Integer.parseInt(s[0]);
            vs[i] = Integer.parseInt(s[1]);
            int di = Integer.parseInt(s[2]);
            tree[di < 0 ? 0 : di].add(i);
        }

        // Output result
        System.out.println(knapsackDPRecursive(N, C, ss, vs, tree));
    }

    private static int knapsackDPRecursive(int N, int C, int[] ss, int[] vs, List<Integer>[] tree) {
        int[][] memo = new int[N+1][C+1];
        for (int i=0; i<N+1; i++) {
            for (int j=0; j<C+1; j++) {
                memo[i][j] = -1;
            }
        }
        return knapsackDPRecursive(C, ss, vs, 0, tree, memo);
    }

    // the max value after chosen root
    private static int knapsackDPRecursive(int C, int[] ss, int[] vs, int root, List<Integer>[] tree, int[][] memo) {
        if (memo != null && memo[root][C] >= 0) {
            return memo[root][C];
        }

        List<Integer> children = tree[root];
        int N = children.size();
        int[][] values = new int[N][C+1]; // the max value for each child and each capacity
        for (int i=0; i<N; i++) {
            int s = ss[children.get(i)], v = vs[children.get(i)];
            for (int j=s; j<=C; j++) {
                values[i][j] = knapsackDPRecursive(j-s, ss, vs, children.get(i)+1, tree, memo) + v;
            }
        }

        // Now it can think of there are N groups of items and each group there are C items to choose from
        // the jth item within ith group has size of j and value of values[i][j]
        // So can solve using group-knapsack way
        int[] dp = new int[C+1];
        for (int i=0; i<N; i++) {
            for (int j=C; j>=1; j--) {
                for (int k=1; k<=j; k++) { // each group got C items
                    dp[j] = Math.max(dp[j], dp[j-k]+values[i][k]);
                }
            }
        }

        memo[root][C] = dp[C];

        return dp[C];
    }
}