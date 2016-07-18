package planning.busdriver.tests.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;
import planning.busdriver.scorer.UnassignedShiftsScorer;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by wayne on 7/16/16.
 */
public class TestUnassignedShiftsScorer {
    private Line line1;
    private Driver driverA;
    private Set<Driver> drivers;
    Set<Line> lines;
    Set<Integer> days;
    UnassignedShiftsScorer scorer = new UnassignedShiftsScorer();

    @Before
    public void setUp() {
        line1 = new Line("1");
        lines = new HashSet<>();
        lines.add(line1);
        driverA = new Driver("A", lines);
        drivers = new HashSet<>();
        drivers.add(driverA);
        days = new HashSet<>();
    }

    @Test
    public void testEmptyDays(){
        assertEquals(0, scorer.evaluate(lines, days, drivers));
    }

    @Test
    public void testUnassignedMorningShift(){
        days.add(1);
        line1.assignLateShift(1, driverA);
        assertEquals(-20, scorer.evaluate(lines, days, drivers));
    }

    @Test
    public void testUnassignedLateShift(){
        days.add(1);
        line1.assignMorningShift(1, driverA);
        assertEquals(-20, scorer.evaluate(lines, days, drivers));
    }

    @Test
    public void testUnassignedShifts(){
        days.add(1);
        assertEquals(-40, scorer.evaluate(lines, days, drivers));
        days.add(2);
        assertEquals(-80, scorer.evaluate(lines, days, drivers));
    }

}
