package planning.busdriver;

import planning.busdriver.exception.AssignmentException;
import planning.busdriver.exception.MissionImpossibleException;
import planning.busdriver.factory.DriversFactory;
import planning.busdriver.factory.LinesFactory;
import planning.busdriver.scorer.*;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by huanze on 7/15/2016.
 */
public class Main {

    static Stack<Stack<Assignment>> candidates = new Stack<>();
    static Assignment lastAssignment = null;
    private static List<Line> lines;
    private static List<Driver> drivers;
    private static int highestScore = Integer.MIN_VALUE;
    private static int smallestCandidatesSize = Integer.MAX_VALUE;


    private static void plan() {
        lines = LinesFactory.createLines();
        drivers = DriversFactory.create("planning/busdriver/shifts.txt", lines);
        for (int day = 1; day <= 14; day++) {
            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                for (int shiftIndex = 0; shiftIndex < 2; shiftIndex++) {
                    Line line = lines.get(lineIndex);
                    Shift shift = Shift.values()[shiftIndex];
                    Driver driver;
                    if ((driver = selectDriver(line, day, shift, drivers)) == null) {
                        if (candidates.isEmpty()) {
                            throw new MissionImpossibleException();
                        }
                        Stack<Assignment> lastCandidates;
                        while ((lastCandidates = candidates.peek()).isEmpty()) {
                            //todo check if candidates is empty
                            candidates.pop();
                        }
                        lastAssignment = lastCandidates.pop();
                        cancelAfter(lastAssignment);
                        lastAssignment.assign();
                        shiftIndex = lastAssignment.shift.ordinal();
                        lineIndex = lines.indexOf(lastAssignment.line);
                        day = lastAssignment.day;
                    } else {
                        line.assignShift(day, shift, driver);
                    }
                    if(day == 14 && lineIndex == lines.size() -1 && shiftIndex == 1){
                        //the end
                        printScore();
//                        startOver();
                        candidates.pop();//for the top ones we only need the one that give the highest point
                        Stack<Assignment> lastCandidates;//continue to pop for empty list until find one
                        while ((lastCandidates = candidates.peek()).isEmpty()) {
                            //todo check if candidates is empty
                            candidates.pop();
                        }
                        if(candidates.size() < smallestCandidatesSize){
                            smallestCandidatesSize = candidates.size();
                            System.out.println("Redo the calc with smaller candidate size: " + candidates.size());
                        }

                        lastAssignment = lastCandidates.pop();
                        cancelAfter(lastAssignment);
                        lastAssignment.assign();
                        shiftIndex = lastAssignment.shift.ordinal();
                        lineIndex = lines.indexOf(lastAssignment.line);
                        day = lastAssignment.day;
                    }
                }

            }
        }
//        printPlan();
//        printScore();
    }

    private static void cancelAfter(Assignment assignment) {
        if(assignment.shift == Shift.MORNING){
            Driver driver = assignment.line.getLateShift(assignment.day);
            if(driver != null){
                assignment.line.cancel(assignment.day, Shift.LATE, driver);
            }
        }
        for(int day = assignment.day; day <= 14; day++){
            for(Line line : lines){
                for(int shiftIndex = 0; shiftIndex < 2; shiftIndex++){
                    Shift shift = Shift.values()[shiftIndex];
                    if(isAfter(day, line, shift, assignment)){
                        Driver driver = line.getShift(day, shift);
                        if(driver != null){
                            line.cancel(day, shift, driver);
                        }
                    }
                }
            }

        }
    }

    private static boolean isAfter(int day, Line line, Shift shift, Assignment assignment) {
        if(day < assignment.day){
            return false;
        }
        if(day > assignment.day){
            return true;
        }
        //day now equals
        if(lines.indexOf(line) > lines.indexOf(assignment.line)){
            return true;
        }
        if(lines.indexOf(line) < lines.indexOf(assignment.line)){
            return false;
        }
        //line now equals
        return shift.ordinal() - assignment.shift.ordinal() > 0;
    }

    private static Set<Integer> initDays() {
        Set<Integer> days = new HashSet<>();
        for(int i = 1; i <= 14; i++){
            days.add(i);
        }
        return days;
    }

    private static void printScore() {
        TotalScorer scorer = new TotalScorer();
        scorer.addScorer(new ShiftPreferenceScorer());
        scorer.addScorer(new OffDaysPreferenceScorer());
        scorer.addScorer(new LongRestScorer());
        scorer.addScorer(new UnassignedShiftsScorer());
        scorer.addScorer(new EarlyAfterLateShiftsScorer());
        scorer.addScorer(new ConsecutiveLateShiftScorer());
        scorer.addScorer(new DeviatedTargetLateShiftsScorer());
        Set<Integer> days = initDays();
        Set<Line> lineSet = new HashSet<>();
        lineSet.addAll(lines);
        Set<Driver> driverSet = new HashSet<>();
        driverSet.addAll(drivers);
        int score =  scorer.evaluate(lineSet, days, driverSet);
        if(score > highestScore){
            highestScore = score;
            System.out.println("Higher Score found: " + highestScore);
            printPlan();
        }
    }



    public static void main(String[] args) throws FileNotFoundException {
        try {
            plan();
        } catch (AssignmentException ex) {
            System.err.println("Error in assignment: " + ex.getClass());
            printPlan();
        }
    }

    private static Assignment nextAssignment(Assignment current) {
        Assignment assignment = new Assignment();
        if (current.shift == Shift.MORNING) {//same day, same line
            assignment.shift = Shift.LATE;
            assignment.day = current.day;
            assignment.line = current.line;
        } else {
            assignment.shift = Shift.MORNING;
            int lineIndex = lines.indexOf(current.line);
            if (lineIndex < lines.size() - 1) { // same day, next line
                assignment.line = lines.get(lineIndex + 1);
                assignment.day = current.day;
            } else { //next day, first line
                assignment.line = lines.get(0);
                assignment.day = current.day + 1;
                if (assignment.day > 14) { //running out
                    return null;
                }
            }
        }
        return assignment;
    }

    private static void printPlan() {
        System.out.printf("%-8s"," ");//-8 to align
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

    private static Driver selectDriver(Line line, int day, Shift shift, List<Driver> drivers) {
        AssignmentEvaluator evaluator = new AssignmentEvaluator();
        List<Assignment> assignments = new ArrayList<>();
        for (Driver driver : drivers) {
            Assignment assignment = new Assignment();
            assignment.day = day;
            assignment.line = line;
            assignment.driver = driver;
            assignment.shift = shift;
            assignments.add(assignment);
        }
        Collections.sort(assignments, evaluator);
        Stack<Assignment> validOnes = filter(assignments);
        if (validOnes.isEmpty()) {
            return null;
        }
        lastAssignment = validOnes.pop();
        candidates.add(validOnes);
        return lastAssignment.driver;
    }

    private static Stack<Assignment> filter(List<Assignment> assignments) {
        Stack<Assignment> validOnes = new Stack<>();
        for (Assignment assignment : assignments) {
            if (AssignmentEvaluator.evaluate(assignment) != AssignmentEvaluator.ILLEGAL_ASSIGNMENT_VALUE) {
                validOnes.add(assignment);
            }
        }
        return validOnes;
    }
}
