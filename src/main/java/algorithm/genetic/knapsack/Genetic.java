package algorithm.genetic.knapsack;

import java.util.*;

/**
 * Created by huanze on 7/19/2016.
 */
public class Genetic {
    private int CHROME_LENGTH = 200;//20;
    private int GENE_LENGTH = 5;//todo this should be rather calculated
    private int POPULATION_SIZE = 100;
    private int MAX_ROUNDS = 100;
    private float CROSSOVER_RATE = 0.7f;
    private double MUTATION_RATE = 0.001;
    private List<BitSet> population = new ArrayList<>(POPULATION_SIZE);
    private double fittest = 0;
    private int round = 0;
    private Knapsack knapsack;

    public Genetic() {
        initTestData("algorithm/genetic/knapsack/testcase1.txt");
    }

    private void initTestData(String resource) {
        knapsack = KnapsackFactory.createKnapsack(resource);
        System.out.printf("Capacity: %s, Optimal: %s \n", knapsack.getCapacity(), knapsack.getOptimal());
    }

    public static void main(String[] args) {
        new Genetic().pack();
    }

    public void pack() {
        initPopulation();

        for(round = 0; round < MAX_ROUNDS; round++){
            List<BitSet> newGeneration = new ArrayList<>(POPULATION_SIZE);
            int count = 0;
            float totalFitness = calcTotalFitness();
            while(count < POPULATION_SIZE){
//                float totalFitness = calcTotalFitness();
                BitSet offspring1 = select(totalFitness);
                BitSet offspring2 = select(totalFitness);
                crossover(offspring1, offspring2);
                mutate(offspring1);
                mutate(offspring2);
                newGeneration.add(offspring1);
                newGeneration.add(offspring2);
                count += 2;
            }
            population = newGeneration;
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

    private void crossover(BitSet offspring1, BitSet offspring2) {
        int start = (int) (CROSSOVER_RATE * CHROME_LENGTH);
        for(int i = start; i < CHROME_LENGTH; i++){
            boolean temp = offspring1.get(i);
            offspring1.set(i, offspring2.get(i));
            offspring2.set(i, temp);
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
        return population.get(population.size() - 1);
    }

    private float calcTotalFitness() {
        float total = 0;
        for(BitSet chrome : population){
            total += calcFitness(chrome);
        }
        return total;
    }

    private float calcFitness(BitSet chrome) {
        int weight = calcWeight(chrome);
        int value = calcValue(chrome);
        int denominator = weight <= knapsack.getCapacity() ? 1 : (weight - knapsack.getCapacity() + 1);
        float fitness =  1.0f * value / denominator;
        if (fitness > fittest) {
            System.out.println("Fittest so far: " + fitness
                    + ", weight: " + weight + ", value: " + value + ", ratio: " + value * 1.0 / knapsack.getOptimal()
                    + " at round " + round);
            System.out.println("Items are: " + decode(chrome));
            fittest = fitness;
        }
        return fitness;
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

    private Set<Item> decode(BitSet chrome) {
        Set<Item> items = new HashSet<>();
        for (int i = 0; i < CHROME_LENGTH; i += GENE_LENGTH) {
            BitSet bs = chrome.get(i, i + GENE_LENGTH);
            int index = toInteger(bs);
            Item item = decode(index);
            if(item != null){
                items.add(item);
            }
        }
//        return removeItemsIfWeightExceeds(items); //is this needed?
        return items;
    }

    private Set<Item> removeItemsIfWeightExceeds(Set<Item> items) {
        Set<Item> result = new HashSet<>();
        int weight = 0;
        for(Item item : items){
            if(item.weight + weight > knapsack.getCapacity()){
                break;
            }
            else{
                weight += item.weight;
                result.add(item);
            }
        }
        return result;

    }

    public int toInteger(BitSet bits) {
        int value = 0;
        for (int i = 0; i < bits.length(); ++i) {
            int actual = i;
            value += bits.get(i) ? (1L << actual) : 0L;
        }
        return value;
    }



    private Item decode(int b) {
        if (b >= knapsack.getItems().size()) {
            return null;
        }
        return knapsack.getItems().get(b);
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

