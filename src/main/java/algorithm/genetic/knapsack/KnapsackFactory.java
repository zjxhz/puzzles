package algorithm.genetic.knapsack;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by huanze on 8/31/2016.
 */
public class KnapsackFactory {
    public static Knapsack createKnapsack(String resource){
        //line 1: capacity optimal; rest lines: weight value for each item
        Scanner in = new Scanner(ClassLoader.getSystemResourceAsStream(resource));
        int bagCapacity = in.nextInt();
        int optimalResult = in.nextInt();
        List<Item> items = new ArrayList<>(bagCapacity);
        while(in.hasNextInt()){
            int weight = in.nextInt();
            int value = in.nextInt();
            items.add(new Item(weight, value));
        }
        return new Knapsack(bagCapacity, optimalResult, items);
    }
}
