package planning.busdriver.tests;

import planning.busdriver.*;
import planning.busdriver.exception.AssignmentOnOffDaysException;
import planning.busdriver.exception.TooManyShiftsADayException;
import planning.busdriver.exception.UnqualifiedDriverException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by huanze on 7/15/2016.
 */
public class TestDriver {
    private Line line1;
    private Line line2;
    private Driver driverA;
    private Driver driverB;

    @Before
    public void setUp() {
        line1 = new Line("1");
        line2 = new Line("2");
        Set<Integer> offDays = new HashSet<>();
        offDays.add(7);
        offDays.add(8);
        Set<Line> lineSet = new HashSet<>();
        lineSet.add(line1);
        Set<Line> lineSet2 = new HashSet<>();
        lineSet2.add(line1);
        lineSet2.add(line2);
        driverA = new Driver("A", lineSet, offDays);
        driverB = new Driver("B", lineSet2);
    }

    @Test
    public void setMorningShift() {
        assertFalse(driverA.hasMorningShift(1));
        line1.assignMorningShift(1, driverA);
        assertEquals(driverA, line1.getMorningShift(1));
    }

    @Test
    public void switchMorningShift() {
        line1.assignMorningShift(1, driverA);
        assertTrue(driverA.hasMorningShift(1));
        assertFalse(driverB.hasMorningShift(1));
        line1.assignMorningShift(1, driverB);
        assertFalse(driverA.hasMorningShift(1));
        assertEquals(driverB, line1.getMorningShift(1));
    }

    @Test
    public void driverShouldNotHaveTwoShiftsOnSameDay() {
        line1.assignMorningShift(1, driverA);
        try {
            line1.assignLateShift(1, driverA);
            fail("A driver can only do one shift per day; early or late.");
        } catch (TooManyShiftsADayException e) {
            //expected
        }
    }

    @Test
    public void driverShouldNotHaveTwoShiftsOnSameDay2() {
        line1.assignLateShift(1, driverA);
        try {
            line1.assignMorningShift(1, driverA);
            fail("A driver can only do one shift per day; early or late.");
        } catch (TooManyShiftsADayException e) {
            //expected
        }
    }

    @Test
    public void driverShouldNotHaveTwoShiftsOnSameDay3() {
        line1.assignLateShift(1, driverB);
        try {
            line2.assignMorningShift(1, driverB);
            fail("A driver can only do one shift per day; early or late.");
        } catch (TooManyShiftsADayException e) {
            //expected
        }
    }

    @Test
    public void driverShouldNotHaveTwoShiftsOnSameDay4() {
        line2.assignLateShift(1, driverB);
        try {
            line1.assignMorningShift(1, driverB);
            fail("A driver can only do one shift per day; early or late.");
        } catch (TooManyShiftsADayException e) {
            //expected
        }
    }


    @Test
    public void unqualifiedDriverOnMorningShift() {
        line2.assignMorningShift(1, driverB);
        assertTrue(driverB.hasMorningShift(1));
        try {
            line2.assignMorningShift(1, driverA);
            fail("A unqualified driver should not be assigned.");
        } catch (UnqualifiedDriverException ex){
            //expected
        }
    }

    @Test
    public void unqualifiedDriverOnLateShift() {
        line2.assignLateShift(1, driverB);
        assertTrue(driverB.hasLateShift(1));
        try {
            line2.assignLateShift(1, driverA);
            fail("A unqualified driver should not be assigned.");
        } catch (UnqualifiedDriverException ex){
            //expected
        }
    }

    @Test
    public void shouldNotAssignMorningShiftOnOffDays(){
        try {
            line1.assignMorningShift(7, driverA);
            fail("You can’t assign shifts on days off.");
        } catch (AssignmentOnOffDaysException ex){
            //expected
        }
    }

    @Test
    public void shouldNotAssignLateShiftOnOffDays() {
        try {
            line1.assignLateShift(8, driverA);
            fail("You can’t assign shifts on days off.");
        } catch (AssignmentOnOffDaysException ex) {
            //expected
        }
    }
}
