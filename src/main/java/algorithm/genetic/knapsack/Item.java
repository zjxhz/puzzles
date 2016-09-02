package algorithm.genetic.knapsack;

/**
 * Created by huanze on 8/31/2016.
 */
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

    @Override
    public String toString() {
        return "Item{" +
                "weight=" + weight +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (weight != item.weight) return false;
        return value == item.value;

    }

    @Override
    public int hashCode() {
        int result = weight;
        result = 31 * result + value;
        return result;
    }
}
