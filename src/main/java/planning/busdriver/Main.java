package planning.busdriver;

public class Main {
    public static void main(String[] args) {
        int planRound = 0;
        int highest = Integer.MIN_VALUE;
        while (true){
            System.out.println("planRound: " + planRound++);
            int value = new Planner("planning/busdriver/shifts1.txt").plan(-1);
            if(value > highest){
                highest = value;
                System.out.println("highest value so far: " + highest);
            }
        }
    }
}
