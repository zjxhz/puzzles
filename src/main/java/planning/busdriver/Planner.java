package planning.busdriver;

import planning.busdriver.exception.MissionImpossibleException;
import planning.busdriver.factory.ImageDriversFactory;
import planning.busdriver.factory.LinesFactory;
import planning.busdriver.scorer.*;

import java.util.*;

/**
 * Created by wayne on 7/23/16.
 */
public class Planner {
    private Stack<Stack<Assignment>> candidates = new Stack<>();
    private Assignment lastAssignment = null;
    private List<Line> lines;
    private List<Driver> drivers;
    private List<Line> storedLines;
    private List<Driver> storedDrivers;
    private int highestScore = Integer.MIN_VALUE;
    private int highestScorePerPlan;
    private TotalScorer scorer;
    private static int MAX_REPLAN_ATTEMPTS = 100;
    private static int MAX_PLAN_STEPS = 2000000;
    private static int MAX_REPLAN_STEPS = 500000;
    private static int REPLAN_MAX_GAP = 4;
    private Plan bestPlan;

    public Planner(Plan initialPlan){
        init(initialPlan);
    }

    public Plan plan(int highest) {
        int totalAttempts = 0;
        highestScorePerPlan = Integer.MIN_VALUE;
        highestScore = highest;
        for (int day = 1; day <= 14; day++) {
            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                for (int shiftIndex = 0; shiftIndex < 2; shiftIndex++) {
                    if(++totalAttempts > MAX_PLAN_STEPS){
                        System.out.println("Too many attempts before getting a possible solution, try another one...");
                        return null;
                    };

                    Line line = lines.get(lineIndex);
                    Shift shift = Shift.values()[shiftIndex];
                    Driver driver;
                    if ((driver = selectRandomDriver(line, day, shift, drivers)) == null) {
                        lastAssignment = findLastAssignment();
                        if (lastAssignment == null) {
                            throw new MissionImpossibleException();
                        }
                        cancelAfter(lastAssignment, 14);
                        lastAssignment.assign();
                        shiftIndex = lastAssignment.shift.ordinal();
                        lineIndex = lines.indexOf(lastAssignment.line);
                        day = lastAssignment.day;
                    } else {
                        line.assignShift(day, shift, driver);
                    }
                    if (day == 14 && lineIndex == lines.size() - 1 && shiftIndex == 1) {
                        printScore();
                        int replanAttempts = 0;
                        while (true) {
                            int from = (int) (14 * Math.random());//1 - 13
                            int to = from + 1 + (int) (Math.random() * REPLAN_MAX_GAP); // gap is 1-4
                            to = to > 14 ? 14 : to;
                            replan(from, to);
                            if(replanAttempts++ > MAX_REPLAN_ATTEMPTS){
                                System.out.println("Highest Score this round: " + highestScorePerPlan);
                                return bestPlan;
                            }

                        }
                    }

                }
            }
        }
        return bestPlan;
    }


    private void replan(int from, int to) {
        candidates = new Stack<>();
        duplicateLinesAndDrivers();
        cancelAfter(from, to);
        int totalAttempts = 0;
        for (int day = from; day <= to; day++) {
            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                for (int shiftIndex = 0; shiftIndex < 2; shiftIndex++) {
                    if(++totalAttempts > MAX_REPLAN_STEPS){
                        System.out.println("Too many REPLAN attempts before getting a possible solution, try another one...");
                        return;
                    };
                    Line line = lines.get(lineIndex);
                    Shift shift = Shift.values()[shiftIndex];
                    Driver driver;
                    if ((driver = selectBestDriver(line, day, shift, drivers)) == null) {
                        lastAssignment = findLastAssignment();
                        if (lastAssignment == null) {
                            restoreLinesAndDrivers();
                            return;
                        }
                        cancelAfter(lastAssignment, to);
                        lastAssignment.assign();
                        shiftIndex = lastAssignment.shift.ordinal();
                        lineIndex = lines.indexOf(lastAssignment.line);
                        day = lastAssignment.day;
                    } else {
                        line.assignShift(day, shift, driver);
                    }
                    if (day == to && lineIndex == lines.size() - 1 && shiftIndex == 1) {
                        printScore();
                        candidates.pop();
                        lastAssignment = findLastAssignment();
                        if (lastAssignment == null) {
                            restoreLinesAndDrivers();
                            return;
                        }
                        cancelAfter(lastAssignment, to);
                        lastAssignment.assign();
                        shiftIndex = lastAssignment.shift.ordinal();
                        lineIndex = lines.indexOf(lastAssignment.line);
                        day = lastAssignment.day;
                    }
                }
            }

        }
        assert false;
    }

    private void restoreLinesAndDrivers() {
        lines = storedLines;
        drivers = storedDrivers;
    }

    private Assignment findLastAssignment() {
        Stack<Assignment> lastCandidates = null;
        while (!candidates.isEmpty() && (lastCandidates = candidates.pop()).isEmpty()) ;
        if (lastCandidates != null && !lastCandidates.isEmpty()) {
            return lastCandidates.pop();
        }
        return null;
    }

    private void cancelAfter(int fromDay, int toDay) {
        for (int day = fromDay; day <= toDay; day++) {
            for (Line line : lines) {
                for (int shiftIndex = 0; shiftIndex < 2; shiftIndex++) {
                    Shift shift = Shift.values()[shiftIndex];
                    Driver driver = line.getShift(day, shift);
                    if (driver != null) {
                        line.cancel(day, shift, driver);
                    }
                }
            }

        }
    }

    private void cancelAfter(Assignment assignment, int toDay) {
        for (int day = assignment.day; day <= toDay; day++) {
            for (Line line : lines) {
                for (int shiftIndex = 0; shiftIndex < 2; shiftIndex++) {
                    Shift shift = Shift.values()[shiftIndex];
                    if (isAfter(day, line, shift, assignment)) {
                        Driver driver = line.getShift(day, shift);
                        if (driver != null) {
                            line.cancel(day, shift, driver);
                        }
                    }
                }
            }

        }
    }

    private boolean isAfter(int day, Line line, Shift shift, Assignment assignment) {
        if (day < assignment.day) {
            return false;
        }
        if (day > assignment.day) {
            return true;
        }
        //day now equals
        if (lines.indexOf(line) > lines.indexOf(assignment.line)) {
            return true;
        }
        if (lines.indexOf(line) < lines.indexOf(assignment.line)) {
            return false;
        }
        //line now equals
        return shift.ordinal() - assignment.shift.ordinal() > 0;
    }

    private Set<Integer> initDays() {
        Set<Integer> days = new HashSet<>();
        for (int i = 1; i <= 14; i++) {
            days.add(i);
        }
        return days;
    }

    private void printScore() {
        int score = getScore();
        if (score > highestScore) {
            duplicateLinesAndDrivers();
            highestScore = score;
            System.out.print(highestScore + " ");
        }
        if(score > highestScorePerPlan){
            highestScorePerPlan = score;
        }
    }

    private void duplicateLinesAndDrivers() {
        storedLines = new ArrayList<>();
        storedDrivers = new ArrayList<>();
        for(Driver driver : drivers){
            storedDrivers.add(driver.duplicate());
        }
        for(Line line : lines){
            storedLines.add(line.duplicate(storedDrivers));
        }
        bestPlan = new Plan(storedLines, storedDrivers,  getScore());
    }

    private int getScore() {
        Set<Integer> days = initDays();
        Set<Line> lineSet = new HashSet<>();
        lineSet.addAll(lines);
        Set<Driver> driverSet = new HashSet<>();
        driverSet.addAll(drivers);
        return scorer.evaluate(lineSet, days, driverSet);
    }




    private void init(Plan initialPlan) {
        lines = initialPlan.getLines();
        drivers = initialPlan.getDrivers();
        scorer = new TotalScorer();
        scorer.addScorer(new ShiftPreferenceScorer());
        scorer.addScorer(new OffDaysPreferenceScorer());
        scorer.addScorer(new LongRestScorer());
        scorer.addScorer(new UnassignedShiftsScorer());
        scorer.addScorer(new EarlyAfterLateShiftsScorer());
        scorer.addScorer(new ConsecutiveLateShiftScorer());
        scorer.addScorer(new DeviatedTargetLateShiftsScorer());
    }

    private Driver selectRandomDriver(Line line, int day, Shift shift, List<Driver> drivers) {
        Stack<Assignment> validOnes = findValidAssignments(line, day, shift, drivers);
        if (validOnes.isEmpty()) {
            return null;
        }
        lastAssignment = validOnes.remove(new Random().nextInt(validOnes.size()));
        candidates.add(validOnes);
        return lastAssignment.driver;
    }

    private Driver selectBestDriver(Line line, int day, Shift shift, List<Driver> drivers) {
        Stack<Assignment> validOnes = findValidAssignments(line, day, shift, drivers);
        if (validOnes.isEmpty()) {
            return null;
        }
        Collections.sort(validOnes, new AssignmentEvaluator());
        lastAssignment = validOnes.pop();
        candidates.add(validOnes);
        return lastAssignment.driver;
    }

    private Stack<Assignment> findValidAssignments(Line line, int day, Shift shift, List<Driver> drivers) {
        List<Assignment> assignments = new ArrayList<>();
        for (Driver driver : drivers) {
            Assignment assignment = new Assignment();
            assignment.day = day;
            assignment.line = line;
            assignment.driver = driver;
            assignment.shift = shift;
            assignments.add(assignment);
        }
        return filter(assignments);
    }

    private Stack<Assignment> filter(List<Assignment> assignments) {
        Stack<Assignment> validOnes = new Stack<>();
        for (Assignment assignment : assignments) {
            if (AssignmentEvaluator.evaluate(assignment) != AssignmentEvaluator.ILLEGAL_ASSIGNMENT_VALUE) {
                validOnes.add(assignment);
            }
        }
        return validOnes;
    }
}
