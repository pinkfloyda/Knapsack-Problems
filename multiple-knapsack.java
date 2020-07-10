import java.util.*;
import java.io.*;

/*
Input format:

First line is two integers N and C, representing unique item count and knapsack capacity, seperated by space
Following N lines where each line is three integers si, vi and qi, representing ith item's size, value and quantity

Output format:
One line of one integer, which is the final result: the possible max value
*/

// 1) DP using 1D array
// 2) DP using 1D array, divide into Mi items
// 3) DP using 1D array, optimized by reducing to 01 knapsack with binary division

public class Main {

    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        // Read N and C
        String[] s = in.readLine().split(" ");
        int N = Integer.parseInt(s[0]); // unique item count
        int C = Integer.parseInt(s[1]); // knapsack capacity
        int[] ss = new int[N]; // size array for each item
        int[] vs = new int[N]; // value array for each item
        int[] qs = new int[N]; // quantity array for each item

        // Read size and value for each item
        for (int i=0; i<N; i++) {
            s = in.readLine().split(" ");
            ss[i] = Integer.parseInt(s[0]);
            vs[i] = Integer.parseInt(s[1]);
            qs[i] = Integer.parseInt(s[2]);
        }

        // Output result
        System.out.println(knapsackDPSlidingWindow(N, C, ss, vs, qs));
    }

    // F(i,c) = max{F(i-1,c-k*ci)+k*vi, 0<=k<=Mi}
    // Complexity is O(N*C*Mi)
    private static int knapsackDP(int N, int C, int[] ss, int[] vs, int[] qs) {
        int[] dp = new int[C+1];
        for (int i=0; i<N; i++) {
            for (int j=C; j>=ss[i]; j--) {
                for (int k=1, s=ss[i], v=vs[i]; s<=j && k<=qs[i]; s+=ss[i], v+=vs[i], k++) {
                    dp[j] = Math.max(dp[j], dp[j-s]+v);
                }
            }
        }
        return dp[C];
    }

    // Consider divide each item into qs[i] items, then apply 01 knapsack algorithm, the complexity is same as above
    private static int knapsackDPDivide(int N, int C, int[] ss, int[] vs, int[] qs) {
        int[] dp = new int[C+1];
        for (int i=0; i<N; i++) {
            for (int k=1, s=ss[i], v=vs[i]; k<=qs[i]; k++) {
                for (int j=C; j>=s; j--) {
                    dp[j] = Math.max(dp[j], dp[j-s]+v);
                }
            }
        }
        return dp[C];
    }

    // Can consider binary divide them into 1,2,4,...
    // But a tricky part is the possible max number can be represented cannot exceed Mi, otherwise
    // it can choose item i beyond Mi, which is wrong. So it must be of values
    // 1,2,2^2,...2^(k-1),Mi-2^k+1. As we know the max number can be represented is 1<=m<=2^k-1
    // So first of all, we must make 2^k-1 <= Mi, and for the remanining, we choose the final value to be Mi-2^k+1
    // In this way, we can represent number of range [1,Mi] (noted that every number between 2^k and Mi can be represented)
    //
    // Refer to below line, B-C is the value of Mi-2^k+1, which is the multiplier of the last item, clearly, by combining this value
    // With any value between A and B can yield values between B-C, which means this setup can represent any numbers between [1..Mi]
    // (1)            (2^k-1)          Mi    (2^(k+1)-1)
    //  A----------------B-------------C----------D-----------
    //
    // Complexity is O(N*C*log(Mi))
    private static int knapsackDPBinaryDivide(int N, int C, int[] ss, int[] vs, int[] qs) {
        int[] dp = new int[C+1];
        for (int i=0; i<N; i++) {
            int k=1;
            for (int s=ss[i], v=vs[i]; (int)Math.pow(2,k)-1<=qs[i]; s*=2, v*=2, k++) {
                for (int j=C; j>=s; j--) {
                    dp[j] = Math.max(dp[j], dp[j-s]+v);
                }
            }
            int m = qs[i]-(int)Math.pow(2,k-1)+1;
            for (int j=C; j>=m*ss[i]; j--) {
                dp[j] = Math.max(dp[j], dp[j-m*ss[i]]+m*vs[i] );
            }
        }
        return dp[C];
    }

    // Slight optimize by checking if can solve for complete knapsack for some items
    private static int knapsackDPBinaryDivideOptimize(int N, int C, int[] ss, int[] vs, int[] qs) {
        int[] dp = new int[C+1];
        for (int i=0; i<N; i++) {
            if (qs[i] * ss[i] >= C) { // Go with complete knapsack algorithm
                for (int j=ss[i]; j<=C; j++) {
                    dp[j] = Math.max(dp[j], dp[j-ss[i]] + vs[i]);
                }
            } else { // Go with multiple knapsack algorithm
                int k=1;
                for (int s=ss[i], v=vs[i]; (int)Math.pow(2,k)-1<=qs[i]; s*=2, v*=2, k++) {
                    for (int j=C; j>=s; j--) {
                        dp[j] = Math.max(dp[j], dp[j-s]+v);
                    }
                }
                int m = qs[i]-(int)Math.pow(2,k-1)+1;
                for (int j=C; j>=m*ss[i]; j--) {
                    dp[j] = Math.max(dp[j], dp[j-m*ss[i]]+m*vs[i] );
                }
            }
        }
        return dp[C];
    }

    // Maintain a strictly decreasing deque with maximum size of m
    // private static void dqAdd(Deque<Integer> dq, int[] dp, int m, int i) {
    //     if (dq.size() > 0 && dq.size() == m) {
    //         dq.removeFirst();
    //     }
    //     while(!dq.isEmpty() && dq.peekLast() <= dp[i]) {
    //         dq.removeLast();
    //     }
    //     dq.addLast(i);
    // }

    // For the DP solution, F(i,c) = max{F(i-1,c-k*ci)+k*vi,0<=k<=mi}, if we expand it in reverse order and also does the same for F(i,c+ci) and F(i,c+2*ci)
    // We can get the below results, which is aligned by same Fs, it is clearly to see that for each incremental of ci, it is a sliding window
    //
    // F(i,c) =      max{F(i-1,c-mi*ci)+mi*vi, F(i-1,c-(mi-1)*ci)+(mi-1)*vi, F(i-1,c-(mi-2)*ci)+(mi-2)*vi, ..., F(i-1,c-2ci)+2vi, F(i-1,c-ci)+vi,  F(i-1,c)                                   }
    // F(i,c+ci) =   max{                      F(i-1,c-(mi-1)*ci)+mi*vi,     F(i-1,c-(mi-2)*ci)+(mi-1)*vi, ...,                   F(i-1,c-ci)+2vi, F(i-1,c)+vi,  F(i-1,c+ci)                  }
    // F(i,c+2*ci) = max{                                                    F(i-1,c-(mi-2)*ci)+mi*vi, ...,                                        F(i-1,c)+2vi, F(i-1,c+ci)+vi, F(i-1,c+2*ci)}
    //
    // For a sliding window it is possible to get maximum F costing O(1), by using a strict decreasing monotonic-queue
    // e.g. for F(i,c), the window is of values starting from F(i-1,c-mi*ci)+mi*vi to F(i-1,c)
    // then for F(i,c+ci), F(i-1,c-mi*ci)+mi*vi should be popped out of tbe monotonic-queue and remaining values are of F(i-1,c-(mi-1)*ci)+(mi-1)*vi to F(i-1,c)
    // But the window values are not same as F(i,c+ci)'s components, it is less than vi, but it does not matter for ith item, vi is a constant.
    // So we need to add  F(i-1,c+ci)-vi into the front of window, extract the maximum value out of the window and then add back vi
    // Same for F(i,c+2*ci), need to add F(i-1,c+2*ci)-2*vi into the front of window, and add back 2*vi to the maxiumum value
    //
    // It is even more clear to start with 0: F(i,0)
    // F(i,0) = max{F(i-1,0)}
    // F(i,ci) = max{F(i-1,0)+vi, F(i-1,ci)}
    // F(i,2*ci) = max{F(i-1,0)+2*vi, F(i-1,ci)+vi, F(i-1,2*ci)}
    // ...
    // F(i,mi*ci)= max{F(i-1,0)+mi*vi,......F(i-1,mi*ci)}
    // F(i,(mi+1)*ci) = max{F(i-1,ci)+(mi+1)*vi,....,F(i-1,(mi+1)*ci)} (where F(i-1,0) poped out and F(i-1,(mi+1)*ci) added in)
    //
    // But what happens to F(i,c+1) through F(i,c+ci-1), actually we can maintain ci monotonic-queues to track sliding windows of
    // F(i,0),F(i,ci),....F(i,mi*ci)
    // F(i,1),F(i,1+ci),....F(i,1+mi*ci)
    // ....
    // F(i,ci-1),F(i,ci-1+ci),....F(i,ci-1+mi*ci)
    //
    // Because for each item, each capacity c entered or exit from the corresponding monotonic-queue once, so complexity is O(N*C)
    private static int knapsackDPSlidingWindow(int N, int C, int[] ss, int[] vs, int[] qs) {
        int[][] dp = new int[N+1][C+1];
        for (int i=1; i<=N; i++) {
            int s = ss[i-1], v = vs[i-1], q = qs[i-1];
            List<Deque<int[]>> dqs = new ArrayList<>();
            List<Integer> ws = new ArrayList<>(); // window start index
            List<Integer> we = new ArrayList<>(); // window end index
            for (int j=0, k=0; j<=C; j++, k=(k+1)%s) {
                if (k == dqs.size()) {
                    dqs.add(new LinkedList<>());
                    ws.add(j);
                    we.add(j);
                }
                Deque<int[]> dq = dqs.get(k);
                if ((we.get(k)-ws.get(k))/s == q) { // slide window already max out
                    if (dq.size() > 0 && dq.peekFirst()[0] == ws.get(k)) { // if window start already the max, will eject it
                        dq.removeFirst();
                    }
                    ws.set(k, ws.get(k)+s); // increment the window start
                }
                int m = (j-k)/s;
                // try to add the current index
                while (!dq.isEmpty() && dq.peekLast()[1] <= dp[i-1][j]-m*v) {
                    dq.removeLast();
                }
                dq.addLast(new int[]{j, dp[i-1][j]-m*v});
                we.set(k, j);
                dp[i][j] = dq.peekFirst()[1]+m*v;
            }
        }
        return dp[N][C];
    }
}