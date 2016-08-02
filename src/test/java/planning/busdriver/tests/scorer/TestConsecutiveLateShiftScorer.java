package planning.busdriver.tests.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;
import planning.busdriver.scorer.ConsecutiveLateShiftScorer;
import planning.busdriver.scorer.EarlyAfterLateShiftsScorer;
import planning.busdriver.scorer.Scorer;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by wayne on 7/16/16.
 */
public class TestConsecutiveLateShiftScorer {
    private Line line1;
    private Driver driverA;
    private Set<Driver> drivers;
    Set<Line> lines;
    Set<Integer> days;
    Scorer scorer = new ConsecutiveLateShiftScorer();


    @Before
    public void setUp() {
        line1 = new Line("1");
        lines = new HashSet<>();
        lines.add(line1);
        driverA = new Driver("A", lines);
        drivers = new HashSet<>();
        drivers.add(driverA);
        days = new HashSet<>();
        days.add(1);
        days.add(2);
        days.add(3);
        days.add(4);
        days.add(5);
        line1.assignLateShift(1, driverA);
        line1.assignLateShift(2, driverA);
        line1.assignLateShift(3, driverA);
    }

    @Test
    public void test4ConsecutiveLateShifts(){
        line1.assignLateShift(4, driverA);
        assertEquals(-10, scorer.evaluate(lines, days, drivers));
    }

    @Test
    public void test5ConsecutiveLateShifts(){
        line1.assignLateShift(4, driverA);
        line1.assignLateShift(5, driverA);
        assertEquals(-20, scorer.evaluate(lines, days, drivers));
    }

    @Test
    public void testNoMoreThan3ConsecutiveLateShifts(){
        line1.assignLateShift(5, driverA);
        assertEquals(0, scorer.evaluate(lines, days, drivers));
    }
}
