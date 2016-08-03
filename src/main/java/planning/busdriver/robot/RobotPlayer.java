package planning.busdriver.robot;

import planning.busdriver.*;
import planning.busdriver.factory.ImageDriversFactory;
import planning.busdriver.factory.LinesFactory;
import planning.busdriver.factory.PlanFactory;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by huanze on 7/25/2016.
 */
public class RobotPlayer {
    private List<Driver> drivers;
    private List<Line> lines;
    Positions positions;
    Robot robot;


    public RobotPlayer(Positions positions, List<Driver> drivers, List<Line> lines) {
        this.positions = positions;
        this.drivers = drivers;
        this.lines = lines;
        this.positions = positions;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException("Failed to init a robot");
        }
    }

    public void play() {
        countdown();
        Collections.sort(drivers, new Comparator<Driver>() {
            @Override
            public int compare(Driver o1, Driver o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });

        Collections.sort(lines, new Comparator<Line>() {
            @Override
            public int compare(Line o1, Line o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });

        for (Line line : lines) {
            int lineIndex = lines.indexOf(line);
            for (int day = 1; day <= 14; day++) {
                for (int shiftIndex = 0; shiftIndex < 2; shiftIndex++) {
                    Shift shift = Shift.values()[shiftIndex];
                    int x = getLineX(lineIndex, day, shift);
                    int y = getLineY(lineIndex, day, shift);
                    click(x, y);
                    nap(100);
                    Driver driver = line.getShift(day, shift);
                    int driverIndex = drivers.indexOf(driver);
                    int dx = getDriverX(driverIndex, day, shift);
                    int dy = getDriverY(driverIndex, day, shift);
                    click(dx, dy);
                }
            }
            nap(200);
        }
    }

    private void countdown() {
        System.out.println("Counting down in 10s");
        nap(2000);
    }

    private void nap(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException("failed to nap?");
        }
    }

    private int getDriverY(int driverIndex, int day, Shift shift) {
        return positions.driverStartY + driverIndex * positions.driverHeight;
    }

    private int getDriverX(int driverIndex, int day, Shift shift) {
        int x = positions.driverStartX + positions.driverWidth * 2 * (day-1);
        if (shift == Shift.LATE) {
            x += positions.driverWidth;
        }
        return x;
    }

    private void click(int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        nap(200);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    private int getLineY(int lineIndex, int day, Shift shift) {
        return positions.lineStartY + lineIndex * positions.lineHeight;
    }

    private int getLineX(int lineIndex, int day, Shift shift) {
        int x = positions.lineStartX + positions.lineWidth * 2 * (day - 1);
        if (shift == Shift.LATE) {
            x += positions.lineWidth;
        }
        return x;
    }

    public static class Positions {
        private int lineStartX = 170;
        private int lineStartY = 125;
        private int lineWidth = 26;
        private int lineHeight = 26;
        private int driverStartX = lineStartX;
        private int driverStartY = 195;
        private int driverWidth = lineWidth;
        private int driverHeight = 26;

        public Positions(int lineStartX, int lineStartY, int lineWidth, int lineHeight,
                         int driverStartY, int driverHeight) {
            this.lineStartX = lineStartX;
            this.lineStartY = lineStartY;
            this.lineWidth = lineWidth;
            this.lineHeight = lineHeight;
            this.driverStartY = driverStartY;
            this.driverHeight = driverHeight;
        }
    }

    public static void main(String[] args) {
        Positions p = new Positions(170, 125, 26, 26, 195, 24);
        Positions p3 = new Positions(408, 138, 33, 30, 240, 37); //lineX, line Y, lineW, lineH, driverY, driverH


        Plan initialPlan = createInitialPlan();
        System.out.println(initialPlan);
        Plan bestPlan = Planner.createPlan(initialPlan, 131);

        new RobotPlayer(p, bestPlan.getDrivers(), bestPlan.getLines()).play();
    }

    private static Plan createInitialPlan() {
        ImageDriversFactory.ImageProperties properties = new ImageDriversFactory.ImageProperties(
                "planning/busdriver/shifts3.png", 408, 242, 331, 242, 352);//driverStartX, driverStartY, line1X, qualifiedLine1Y, line2X
//        ImageDriversFactory.ImageProperties properties = new ImageDriversFactory.ImageProperties(
//                "planning/busdriver/shiftsg.jpg", 413, 250, 330, 270, 352);
        return PlanFactory.createPlanFromImage(properties);

    }

}
