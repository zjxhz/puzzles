package planning.busdriver.tests.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;
import planning.busdriver.scorer.EarlyAfterLateShiftsScorer;
import planning.busdriver.scorer.UnassignedShiftsScorer;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by wayne on 7/16/16.
 */
public class TestEarlyAfterLateShiftsScorer {
    private Line line1;
    private Line line2;
    private Driver driverA;
    private Set<Driver> drivers;
    Set<Line> lines;
    Set<Integer> days;
    EarlyAfterLateShiftsScorer scorer = new EarlyAfterLateShiftsScorer();


    @Before
    public void setUp() {
        line1 = new Line("1");
        line2 = new Line("2");
        lines = new HashSet<>();
        lines.add(line1);
        lines.add(line2);
        driverA = new Driver("A", lines);
        drivers = new HashSet<>();
        drivers.add(driverA);
        days = new HashSet<>();
        days.add(1);
        days.add(2);
    }

    @Test
    public void testEarlyAfterLateShift(){
        line1.assignLateShift(1, driverA);
        line1.assignMorningShift(2, driverA);
        assertEquals(-30, scorer.evaluate(lines, days, drivers));

    }

    @Test
    public void testEarlyAfterLateShiftFromDifferentLines(){
        line1.assignLateShift(1, driverA);
        line2.assignMorningShift(2, driverA);
        assertEquals(-30, scorer.evaluate(lines, days, drivers));

    }

    @Test
    public void testEarlyAfterEarlyShift(){
        line1.assignMorningShift(1, driverA);
        line1.assignMorningShift(2, driverA);
        assertEquals(0, scorer.evaluate(lines, days, drivers));
    }

    @Test
    public void testLateAfterLateShift(){
        line1.assignLateShift(1, driverA);
        line1.assignLateShift(2, driverA);
        assertEquals(0, scorer.evaluate(lines, days, drivers));
    }

}
