package planning.busdriver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huanze on 7/25/2016.
 */
public class Plan {
    List<Line> lines;
    List<Driver> drivers;
    int score;

    public List<Line> getLines() {
        return lines;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public int getScore() {
        return score;
    }

    public Plan(List<Line> lines, List<Driver> drivers, int score) {
        this.lines = lines;
        this.drivers = drivers;
        this.score = score;
    }


    public void print() {
        System.out.printf("\n%-8s", " ");//-8 to align
        for (int i = 1; i <= 14; i++) {
            System.out.print("Day " + i + "\t");
        }
        System.out.println();

        for (Line line : lines) {
            System.out.printf("%-8s", line);//-8 to align
            for (int i = 1; i <= 14; i++) {
                Driver morningDriver = line.getMorningShift(i);
                Driver lateDriver = line.getLateShift(i);
                System.out.printf((morningDriver == null ? "?" : morningDriver.getId()) + " | " + (lateDriver == null ? "?" : lateDriver.getId()) + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public Plan duplicate(){
        List<Line> linesCopy = new ArrayList<>();
        List<Driver> driversCopy = new ArrayList<>();
        for (Driver driver : drivers) {
            driversCopy.add(driver.duplicate());
        }
        for (Line line : lines) {
            linesCopy.add(line.duplicate(driversCopy));
        }
        return new Plan(linesCopy, driversCopy, getScore());
    }
}
