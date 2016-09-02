package algorithm.genetic.knapsack;

/**
 * Created by huanze on 8/31/2016.
 */
public class DP {
    private static int topDownCount = 0;
    private static int bottomUpCount = 0;
    public static void main(String[] args) {
        Knapsack knapsack = KnapsackFactory.createKnapsack("algorithm/genetic/knapsack/testcase2.txt");
        System.out.println(findOptimalTopDown(knapsack));
        System.out.println(findOptimalBottomUp(knapsack));
    }

    private static int findOptimalBottomUp(Knapsack knapsack) {
        int n = knapsack.getItems().size();
        int w = knapsack.getCapacity();
        int lightest = knapsack.getLightestWeight();
        int [][] A = new int[n][w + 1];
        for(int i = 0; i < n; i++){
            for(int x = lightest; x <= w; x++){
                bottomUpCount++;
                if(x == 0){
                    A[i][x] = 0;
                    continue;
                }
                int Wi = knapsack.getItems().get(i).weight;
                int Vi = knapsack.getItems().get(i).value;
                if(i == 0){ //includes only the 0th item
                    if(x >= Wi){
                        A[i][x] = Vi;
                    }
                    continue;
                }

                if(Wi > x){//not possible to include ith item as its weight is too large
                    A[i][x] = A[i - 1][x];
                } else {
                    A[i][x] = Math.max( A[i - 1][x], A[i-1][x-Wi] + Vi);
                }
            }
        }
        System.out.println(bottomUpCount);
        return A[n-1][w-1];
    }

    private static int findOptimalTopDown(Knapsack knapsack) {
        int n = knapsack.getItems().size();
        int w = knapsack.getCapacity();
        int optimal = findOptimalTopDown(knapsack, n, w);
        System.out.println(topDownCount);
        return optimal;
    }

    private static int findOptimalTopDown(Knapsack knapsack, int n, int w) {
        topDownCount++;
        if(n == 0 || w <= 0){
            return 0;
        }
        Item last = knapsack.getItems().get(n - 1);
        int withoutLast = findOptimalTopDown(knapsack, n - 1, w);
        int withLast = w - last.weight > 0 ? findOptimalTopDown(knapsack, n - 1, w - last.weight) + last.value : -1;
        return Math.max(withoutLast, withLast);
    }
}
