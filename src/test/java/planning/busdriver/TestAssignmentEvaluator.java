package planning.busdriver.tests;

import planning.busdriver.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by huanze on 7/15/2016.
 */
public class TestAssignmentEvaluator {
    private Line line1;
    private Driver driverA;
    AssignmentEvaluator evaluator = new AssignmentEvaluator();
    Set<Line> lineSet;

    @Before
    public void setUp() {
        line1 = new Line("1");
        lineSet = new HashSet<>();
        lineSet.add(line1);
        driverA = new Driver("A", lineSet);
    }

    @Test
    public void respectOffDaysPreference() {
        assertEquals(0, AssignmentEvaluator.evaluate(line1, 1, Shift.WHATEVER, driverA));
        Set<Integer> preferredOffDays = new HashSet<>();
        preferredOffDays.add(1);
        driverA.setPreferredOffDays(preferredOffDays);
        assertEquals(-3, AssignmentEvaluator.evaluate(line1, 1, Shift.WHATEVER, driverA));
    }

    @Test
    public void respectShiftPreference() {
        assertEquals(0, AssignmentEvaluator.evaluate(line1, 1, Shift.MORNING, driverA));
        Map<Integer, Shift> preferredShifts = new HashMap<>();
        preferredShifts.put(1, Shift.MORNING);
        driverA.setPreferredShifts(preferredShifts);
        assertEquals(4, AssignmentEvaluator.evaluate(line1, 1, Shift.MORNING, driverA));
    }

    //the more late shifts one has, the less he should be asggined late shifts.
    @Test
    public void tooManyLateShift() {
        assertEquals(0, AssignmentEvaluator.evaluate(line1, 1, Shift.WHATEVER, driverA));
        line1.assignLateShift(1, driverA);
        line1.assignLateShift(3, driverA);
        line1.assignLateShift(5, driverA);
        line1.assignLateShift(7, driverA);
        assertEquals(AssignmentEvaluator.ILLEGAL_ASSIGNMENT_VALUE, AssignmentEvaluator.evaluate(line1, 9, Shift.LATE, driverA));
    }

    @Test
    public void lateShiftFollowedImmediatelyByEarlyShift() {
        assertEquals(0, AssignmentEvaluator.evaluate(line1, 1, Shift.WHATEVER, driverA));
        line1.assignLateShift(1, driverA);
        assertEquals(AssignmentEvaluator.ILLEGAL_ASSIGNMENT_VALUE, AssignmentEvaluator.evaluate(line1, 2, Shift.MORNING, driverA));
    }

    @Test
    public void consecutiveLateShifts() {
        assertEquals(0, AssignmentEvaluator.evaluate(line1, 1, Shift.WHATEVER, driverA));
        line1.assignLateShift(1, driverA);
        line1.assignLateShift(2, driverA);
        line1.assignLateShift(3, driverA);
        assertEquals(AssignmentEvaluator.ILLEGAL_ASSIGNMENT_VALUE, AssignmentEvaluator.evaluate(line1, 4, Shift.LATE, driverA));
//        line1.assignLateShift(4, driverA);
        //20+8. 5 consecutive late shifts
//        assertEquals(-28, AssignmentEvaluator.evaluate(line1, 5, Shift.LATE, driverA));
    }

    @Test
    public void rewardLongRest() {
        assertEquals(0, AssignmentEvaluator.evaluate(line1, 1, Shift.WHATEVER, driverA));
        line1.assignLateShift(1, driverA);
        assertEquals(0, AssignmentEvaluator.evaluate(line1, 3, Shift.LATE, driverA));
        assertEquals(-5, AssignmentEvaluator.evaluate(line1, 4, Shift.LATE, driverA));
    }

    @Test
    public void illegalAssignment() {
        Line line2 = new Line("2");
        assertEquals("Assigning an unqualified driver is a bad idea",
                AssignmentEvaluator.ILLEGAL_ASSIGNMENT_VALUE, AssignmentEvaluator.evaluate(line2, 1, Shift.WHATEVER, driverA));
        Set<Integer> offDays = new HashSet<>();


        offDays.add(1);
        Driver driverB = new Driver("B", lineSet, offDays);
        assertEquals("Assigning on off days is not a good plan.",
                AssignmentEvaluator.ILLEGAL_ASSIGNMENT_VALUE, AssignmentEvaluator.evaluate(line1, 1, Shift.WHATEVER, driverB));


        driverA.assignMorningShift(line1, 1);
        assertEquals("Assigning too many shifts",
                AssignmentEvaluator.ILLEGAL_ASSIGNMENT_VALUE, AssignmentEvaluator.evaluate(line1, 1, Shift.LATE, driverA));
        lineSet.add(line2);
        assertEquals("Assigning too many shifts",
                AssignmentEvaluator.ILLEGAL_ASSIGNMENT_VALUE, AssignmentEvaluator.evaluate(line2, 1, Shift.LATE, driverA));

    }

    @Test
    public void driversWithLessLinesFirst(){
        Line line2 = new Line("2");
        Set<Line> lineSet2 = new HashSet<>();
        lineSet2.add(line1);
        lineSet2.add(line2);
        Driver driverB = new Driver("B", lineSet2);
        Assignment assignment1 = new Assignment(line1, 1, Shift.MORNING, driverA);
        Assignment assignment2 = new Assignment(line1, 1, Shift.MORNING, driverB);
        assertTrue(new AssignmentEvaluator().compare(assignment1, assignment2) > 0 );
    }

    @Test
    public void driversWithSmallerIdFirst(){
        Driver driverB = new Driver("B", lineSet);
        Assignment assignment1 = new Assignment(line1, 1, Shift.MORNING, driverA);
        Assignment assignment2 = new Assignment(line1, 1, Shift.MORNING, driverB);
        assertTrue(new AssignmentEvaluator().compare(assignment1, assignment2) > 0 );
    }

    @Test
    public void driversHasLessLateShiftsFirst(){
        Driver driverB = new Driver("B", lineSet);
        driverA.assignLateShift(line1, 1);
        Assignment assignment1 = new Assignment(line1, 1, Shift.LATE, driverA);
        Assignment assignment2 = new Assignment(line1, 1, Shift.LATE, driverB);
        assertTrue(new AssignmentEvaluator().compare(assignment1, assignment2) < 0 );
    }


}
