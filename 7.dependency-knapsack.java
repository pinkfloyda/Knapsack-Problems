/* Dependency-Knapsack Problem
 *
 * You have a knapsack with budget C and N items can be picked into knapsack,
 * each (i-th) item has cost ci, value vi and can only be picked once. But certain
 * items may depend on others, e.g. if item i depend on item j, pick item i must also 
 * pick item j. There can be multiple items depend on one item, but each item can only
 * only depend on one item.
 * What is the max value you can get without exceeding budget?
 * 
 * Input:
 * First line has two integers: N and C, seperated by space
 * Then followed by next N lines representing items:
 *   each line has three integers: item's cost, value and another item index it depends on, sperated by space
 *     if the depending item's index is -1, it means it does not depend on any items
 *
 * 
 * Output:
 * One line with one integer: max value
 *
 * Data Range:
 * 1 ≤ N,C ≤ 100
 * 1 ≤ ci,vi ≤ 100
 * The data ensures the items form a single depdency tree where the root does not depend on any items.
 *
 * Sample Input:
 *   5 7
 *   2 3 -1
 *   2 2 1
 *   3 5 1
 *   4 7 2
 *   3 6 2
 * Sample Output:
 *   11
 */

import java.util.*;

public class Main {
    
    static int N;
    static int C;
    static int[] c;
    static int[] v;
    static List<Integer>[] children;
    static int root;
    static int[][] dp; // dp[i][j] the max value of picking ith item (including its dependent items) not execeeding cost j

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        N = sc.nextInt();
        C = sc.nextInt();
        
        c = new int[N];
        v = new int[N];
        children = (List<Integer>[])new List[N];
        for(int i=0; i<N; i++) {
            children[i] = new ArrayList<>();
        }
        dp = new int[N][C+1];

        for(int i=0; i<N; i++) {
            c[i] = sc.nextInt();
            v[i] = sc.nextInt();
            int dep = sc.nextInt();
            if(dep < 0) {
                root = i;
            } else {
                children[dep-1].add(i);
            }
        }
        
        knapsack(root);
        
        System.out.println(dp[root][C]);
    }
    
    /* The idea is similar to group knapsack problem
     * Starting from root of the depdency tree, we can treat child as a group
     * and for each group, decide how we select child (including its children) items
     * There could be many combinations of select child and its children, but if multiple
     * combinations have same cost, why not select the one with highest value? 
     * 
     * In this way, we can image the group has items with cost 0 to C, the cost is the total
     * cost of choosing items starting with the child group, the value is the max value of all
     * choosing strategies starting with the child group having the same cost.
     * 
     * So the dp status can be dp[i][j]: the max value of choosing items starting 
     * from i-th item without exceeding cost j. Noted that this dp array is a rolling array 
     * updated recursivly using group-knapsack algorithm, if not use rolling array, dp status should be
     * dp[i][j][k]: the max value of using ith item as root, choosing
     * first j groups without excceeding cost k. Not every tree will have the same number of
     * children, so the above dp array is quite hard to create and maintain. Probably need to create a
     * temp 2D array for each recurision and assign values back to global dp[i][j]. So here shows
     * the powerfulness and expressiveness of using a rolling array.
     *
     * As child may also have its children and we need to go down the tree recursively and 
     * update the dp table recursively. e.g item A got B C D as children, B got E F G as children, so in
     * order to apply group knapsack algorithm to A's children, need to apply group knapsack algorithm to B's
     * children first, then B will have the max values for all costs from 1 to C (or more precisely C-c[A]), which
     * unblocks the group knapsack process for A.
     *
     * The algorithm also applied to mulitple trees, first do a group knapsack algorithm for all the roots as groups
     * then recursively go down from each root to build the global dp table.
     */
    static void knapsack(int root) {
        for(int j=c[root]; j<=C; j++) {
            dp[root][j] = v[root];
        }
        for(int i=0; i<children[root].size(); i++) { // n groups of items
            int child = children[root].get(i);
            knapsack(child);
            for(int j=C; j>=c[root]; j--) { // the cost stop at c[root] as we need to reserve the root's cost
                for(int k=1; k<=j-c[root]; k++) { // need stop at j-c[root] 
                    dp[root][j] = Math.max(dp[root][j], dp[root][j-k] + dp[child][k]);
                }
            }
        }
    }
}
