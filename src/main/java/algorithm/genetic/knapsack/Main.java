package algorithm.genetic.knapsack;

import java.util.*;

/**
 * Created by huanze on 7/19/2016.
 */
public class Main {
    private int CHROME_LENGTH = 20;
    private int POPULATION_SIZE = 100;
    private int MAX_ROUNDS = 1000;
    private int BAG_CAPACITY = 15;
    private double MUTATION_RATE = 0.001;
    private List<BitSet> population = new ArrayList<>(POPULATION_SIZE);
    private BitSet bestChrome;

    private int largestValue = Integer.MIN_VALUE;

    public List<Item> items;

    public Main() {
        items = new ArrayList<>();
        items.add(new Item(12, 4));
        items.add(new Item(1, 2));
        items.add(new Item(4, 10));
        items.add(new Item(1, 1));
        items.add(new Item(2, 2));
    }

    public static void main(String[] args) {
        BitSet bs = new BitSet();
        bs.set(4);
        System.out.println(bs.toByteArray()[0]);
    }

    public void pack() {
        initPopulation();

        for(int i = 0; i < MAX_ROUNDS; i++){
            List<BitSet> newGeneration = new ArrayList<>(POPULATION_SIZE);
            int count = 0;
            while(count < POPULATION_SIZE){
                float totalFitness = calcTotalFitness();
                BitSet offspring1 = select(totalFitness);
                BitSet offspring2 = select(totalFitness);
                crossover(offspring1, offspring2);
                mutate(offspring1);
                mutate(offspring2);
                newGeneration.add(offspring1);
                newGeneration.add(offspring2);
                count += 2;
            }
            for(int j = 0; j < POPULATION_SIZE; j++){

            }


//            population

        }
    }

    private void mutate(BitSet offspring) {
        Random r = new Random();
        for(int i = 0; i < CHROME_LENGTH; i++){
            if(r.nextDouble() < MUTATION_RATE){
                offspring.flip(i);
            }
        }
    }

    private void crossover(BitSet father, BitSet mother) {
        int start = (int) (Math.random() * CHROME_LENGTH);
        for(int i = start; i < CHROME_LENGTH; i++){
            boolean temp = father.get(i);
            father.set(i, mother.get(i));
            mother.set(i, temp);
        }
    }

    private BitSet select(float totalFitness) {
        double randomFitness = Math.random() * totalFitness;
        double sum = 0;
        for(BitSet chrome : population){
            sum += calcFitness(chrome);
            if(sum > randomFitness){
                return chrome;
            }
        }
        return null;
    }

    private float calcTotalFitness() {
        float total = 0;
        for(BitSet chrome : population){
            total += calcFitness(chrome);
        }
        return total;
    }

//    private BitSet encode(Item item){
//        BitSet bitSet = BitSet.valueOf({1});
//        bitSet.
//    }

    private float calcFitness(BitSet chrome) {
        int weight = calcWeight(chrome);
        int value = calcValue(chrome);
//        if (weight <= 15 && value > largestValue) {
//            System.out.println("Largest value so far: " + value + ", weight: " + weight);
//            System.out.println("Items are: " + decode(chrome));
//            largestValue = value;
//        }
        int denominator = weight <= 15 ? (16 - weight) : (weight - 10);
        return value / denominator;
    }

    private int calcWeight(BitSet chrome) {
        int weight = 0;
        for (Item i : decode(chrome)) {
            weight += i.weight;
        }
        return weight;
    }

    private int calcValue(BitSet chrome) {
        int value = 0;
        for (Item i : decode(chrome)) {
            value += i.value;
        }
        return value;
    }

    private List<Item> decode(BitSet chrome) {
        List<Item> items = new ArrayList<>();
        Set<Byte> generatedIndexes = new HashSet<>();
        for (int i = 0; i < CHROME_LENGTH; i += 4) {
            BitSet bs = chrome.get(i, i + 4);
            byte index = bs.toByteArray()[0];
            Item item = decode(index);
            if (items.contains(index) || item == null) {
                continue;
            }
            generatedIndexes.add(index);
        }
//        removeItemsIfWeightExceeds(items); //is this needed?
        return items;
    }

//    private void removeItemsIfWeightExceeds(List<Item> items) {
//        int weight = 0;
//        for(Item i : items){
//            weight += i.weight;
//        }
//        if(weight)
//    }

    private Item decode(byte b) {
        if (b >= items.size()) {
            return null;
        }
        return items.get(b);
    }

    private void initPopulation() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(generateChrom());
        }
    }

    private BitSet generateChrom() {
        BitSet chrome = new BitSet(CHROME_LENGTH);
        Random r = new Random();
        for (int i = 0; i < CHROME_LENGTH; i++) {
            chrome.set(i, r.nextBoolean());
        }
        return chrome;
    }
}

class Item implements Comparable<Item> {
    int weight;
    int value;

    public Item(int weight, int value) {
        this.weight = weight;
        this.value = value;
    }

    @Override
    public int compareTo(Item o) {
        return weight - o.weight;
    }

//    public BitSet encode{
//
//    }
}