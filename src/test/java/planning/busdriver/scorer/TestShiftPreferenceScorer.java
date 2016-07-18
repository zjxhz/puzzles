package planning.busdriver.tests.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;
import planning.busdriver.Shift;
import planning.busdriver.scorer.ShiftPreferenceScorer;
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
public class TestShiftPreferenceScorer {
    private Line line1;
    private Driver driverA;
    private Set<Driver> drivers;
    Set<Line> lineSet;
    ShiftPreferenceScorer scorer = new ShiftPreferenceScorer();
    Map<Integer, Shift> shiftPreferences;

    @Before
    public void setUp() {
        line1 = new Line("1");
        lineSet = new HashSet<>();
        lineSet.add(line1);
        driverA = new Driver("A", lineSet);
        drivers = new HashSet<>();
        drivers.add(driverA);
        shiftPreferences = new HashMap<>();
        driverA.setPreferredShifts(shiftPreferences);
    }



    @Test
    public void countShiftPreference(){
        shiftPreferences.put(1, Shift.MORNING);
        for(int day = 1; day <= 14; day++){
            driverA.assignMorningShift(line1, day);
        }
        assertEquals(3, scorer.evaluate(null, null, drivers));

        shiftPreferences.put(3, Shift.LATE);//no effect
        assertEquals(3, scorer.evaluate(null, null, drivers));

        shiftPreferences.put(5, Shift.MORNING);
        assertEquals(6, scorer.evaluate(null, null, drivers));
    }
}
