package planning.busdriver;

import planning.busdriver.exception.MissionImpossibleException;
import planning.busdriver.scorer.*;

import java.util.*;

/**
 * Planner creates plans by assigning shifts to the drivers.
 */
public class Planner {
    private Stack<Stack<Assignment>> candidates = new Stack<>();
    private Assignment lastAssignment = null;
    private List<Line> lines;
    private List<Driver> drivers;
    private int highestScore = Integer.MIN_VALUE;
    private TotalScorer scorer;
    private static int MAX_TOTAL_ATTEMPTS = 50;
    private static int MAX_OPTIMIZING_ATTEMPTS = 100;
    private static int MAX_ATTEMPTS_PER_PLANNING = 2000000; //avoid trying too many steps to generate a plan
    private static int OPTIMIZING_GAP = 4;
    private Plan bestPlan;
    private int target;

    /**
     * Constructs a planner with an initial plan.
     *
     * @param initialPlan Plan with information about drivers, lines without any shifts assigned.
     */
    public Planner(Plan initialPlan) {
        init(initialPlan);
    }

    /**
     * Plan towards the target.
     *
     * @param target Target score to achieve.
     * @return Plan with shifts of lines assigned to drivers
     */
    private Plan plan(int target) {
        this.target = target;
        createFirstPlan();
        optimize();
        return bestPlan;
    }

    private void optimize() {
        int optimizingRound = 0;
        Random random = new Random();
        boolean targetReached = false;
        while (optimizingRound++ < MAX_OPTIMIZING_ATTEMPTS && !targetReached) {
            int from = random.nextInt(13) + 1;
            int to = from + 1 + random.nextInt(OPTIMIZING_GAP);
            to = to > 14 ? 14 : to;
            targetReached = optimise(from, to);
        }
    }

    /**
     * Creates the first plan to be optimized later.
     */
    private void createFirstPlan() {
        plan(firstAssignmentOnDay(1), 14, false);
        storePlan();
    }

    /**
     * Plan starting on a given assignment to a day. Argument restore indicates when the old plan needs to be restored
     * when planning is impossible to proceed.
     *
     * @Param fromAssignment Start from the assignment which has day, line and shift
     * @Param toDay To this day.
     * @Param restore Restores from the best plan when it is impossible to proceed if true,
     * or throw an MissionImpossibleException
     */
    private void plan(Assignment fromAssignment, int toDay, boolean restore) {
        int totalAttempts = 0;
        Assignment assignment = fromAssignment;
        do {
            Driver driver = selectDriver(assignment);
            if (driver != null) {
                assignment.driver = driver;
                assignment.assign();
            } else {
                lastAssignment = findLastAssignment();
                if (lastAssignment == null) {
                    if (restore) {
                        restorePlan();
                        return;
                    } else {
                        throw new MissionImpossibleException();
                    }

                }
                cancelAfter(lastAssignment, toDay);
                lastAssignment.assign();
            }
            assignment = lastAssignment;
            if (++totalAttempts > MAX_ATTEMPTS_PER_PLANNING) {
                System.out.println("Interrupted because of too many attempts");
                return;
            }
        } while ((assignment = nextAssignment(assignment, toDay)) != null);
    }

    private Driver selectDriver(Assignment assignment) {
        Stack<Assignment> validOnes = findValidAssignments(assignment);
        if (validOnes.isEmpty()) {
            return null;
        }
        lastAssignment = validOnes.remove(new Random().nextInt(validOnes.size()));
        candidates.add(validOnes);
        return lastAssignment.driver;
    }

    private Stack<Assignment> findValidAssignments(Assignment assignment) {
        return findValidAssignments(assignment.line, assignment.day, assignment.shift, drivers);
    }

    /**
     * Optimizing the plan by brute forcing all possibilities on a given day range
     *
     * @param from From day.
     * @param to To day.
     */
    private boolean optimise(int from, int to) {
        candidates = new Stack<>();
        storePlan();
        cancelAfter(from, to);

        Assignment assignment = firstAssignmentOnDay(from);
        while (assignment != null) {
            plan(assignment, to, true);
            if(storeBetterPlan() >= target){
                return true;
            }

            if (!candidates.isEmpty()) {
                candidates.pop();
            }
            lastAssignment = findLastAssignment();
            if (lastAssignment == null) {
                restorePlan();
                return false;
            }
            cancelAfter(lastAssignment, to);
            lastAssignment.assign();
            assignment = nextAssignment(lastAssignment);
        }
        return false;
    }

    private Assignment firstAssignmentOnDay(int from) {
        return new Assignment(lines.get(0), from, Shift.MORNING, null);
    }

    private void restorePlan() {
        lines = bestPlan.getLines();
        drivers = bestPlan.getDrivers();
    }

    private Assignment findLastAssignment() {
        Stack<Assignment> lastCandidates = null;
        while (!candidates.isEmpty() && (lastCandidates = candidates.pop()).isEmpty()) ;
        if (lastCandidates != null && !lastCandidates.isEmpty()) {
            return lastCandidates.pop();
        }
        return null;
    }

    private Assignment nextAssignment(Assignment assignment) {
        Assignment next = new Assignment();
        int lineIndex = lines.indexOf(assignment.line);
        if (assignment.shift == Shift.MORNING) {
            next.day = assignment.day;
            next.line = assignment.line;
            next.shift = Shift.LATE;
        } else if (lineIndex < 2) {
            next.day = assignment.day;
            next.line = lines.get(lineIndex + 1);
            next.shift = Shift.MORNING;
        } else {
            next.day = assignment.day + 1;
            next.line = lines.get(0);
            next.shift = Shift.MORNING;
        }
        return next;
    }

    /**
     * Returns next assignment of the given assignment, or null if it is after the given day
     *
     * @param assignment Assignment
     * @param day        No late than this day
     * @return Next assignment of the given assignment, or null if it is after the given day
     */
    private Assignment nextAssignment(Assignment assignment, int day) {
        Assignment next = nextAssignment(assignment);
        if (next.day > day) {
            return null;
        }
        return next;
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

    private int storeBetterPlan() {
        int score = getScore();
        if (score > highestScore) {
            storePlan();
            highestScore = score;
            System.out.print(highestScore + " ");
        }
        return score;
    }

    private void storePlan() {
        List<Line> linesCopy = new ArrayList<>();
        List<Driver> driversCopy = new ArrayList<>();
        for (Driver driver : drivers) {
            driversCopy.add(driver.duplicate());
        }
        for (Line line : lines) {
            linesCopy.add(line.duplicate(driversCopy));
        }
        bestPlan = new Plan(linesCopy, driversCopy, getScore());
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

    private Driver selectDriver(Line line, int day, Shift shift, List<Driver> drivers) {
        Stack<Assignment> validOnes = findValidAssignments(line, day, shift, drivers);
        if (validOnes.isEmpty()) {
            return null;
        }
        lastAssignment = validOnes.remove(new Random().nextInt(validOnes.size()));
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

    public static Plan createPlan(Plan initialPlan, int target){
        Plan bestPlan = null;
        int attempts = 0;
        int highestScore = Integer.MIN_VALUE;
        while (attempts++ < MAX_TOTAL_ATTEMPTS) {
            System.out.println("\nPlanning round : " + attempts);
            Plan plan = new Planner(initialPlan.duplicate()).plan(target);
            int score = plan.getScore();
            if(score > highestScore){
                highestScore = score;
                bestPlan = plan;
                bestPlan.print();
            }
            if (plan.getScore() >= target) {
                break;
            }
        }
        return bestPlan;
    }
}
