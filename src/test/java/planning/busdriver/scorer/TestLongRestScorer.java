package planning.busdriver.tests.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;
import planning.busdriver.Shift;
import planning.busdriver.scorer.LongRestScorer;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by wayne on 7/16/16.
 */
public class TestLongRestScorer {
    private Line line1;
    private Driver driverA;
    private Set<Driver> drivers;
    Set<Line> lineSet;
    LongRestScorer lrScorer = new LongRestScorer();

    @Before
    public void setUp() {
        line1 = new Line("1");
        lineSet = new HashSet<>();
        lineSet.add(line1);
        driverA = new Driver("A", lineSet);
        drivers = new HashSet<>();
        drivers.add(driverA);
    }

    @Test
    public void countLongRest(){
        assertEquals(5, lrScorer.evaluate(null, null, drivers));
    }

    @Test
    public void countLongRests(){
        driverA.assignMorningShift(line1, 7);
        assertEquals(10, lrScorer.evaluate(null, null, drivers));
    }

    @Test
    public void countMoreLongRests(){
        driverA.assignMorningShift(line1, 4);//1-3
        driverA.assignMorningShift(line1, 8);//5-7
        assertEquals(15, lrScorer.evaluate(null, null, drivers));
    }
}
