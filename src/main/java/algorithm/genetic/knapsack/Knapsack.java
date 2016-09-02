package algorithm.genetic.knapsack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by huanze on 8/31/2016.
 */
public class Knapsack {
    private int capacity;
    private int optimal;
    private List<Item> items;
    public Knapsack(int capacity, int optimal, List<Item> items) {
        this.capacity = capacity;
        this.optimal = optimal;
        this.items = items;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getOptimal() {
        return optimal;
    }

    public List<Item> getItems() {
        return items;
    }

    public int getLightestWeight(){
        List<Item> copy = new ArrayList<>(items);
        Collections.sort(copy);
        return copy.get(0).weight;
    }
}
