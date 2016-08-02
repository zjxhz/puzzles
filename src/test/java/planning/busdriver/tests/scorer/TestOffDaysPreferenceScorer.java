package planning.busdriver.tests.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;
import planning.busdriver.scorer.OffDaysPreferenceScorer;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by wayne on 7/16/16.
 */
public class TestOffDaysPreferenceScorer{
    private Line line1;
    private Driver driverA;
    private Set<Driver> drivers;
    Set<Line> lineSet;
    OffDaysPreferenceScorer scorer = new OffDaysPreferenceScorer();
    Set<Integer> offDaysPreferences;

    @Before
    public void setUp() {
        line1 = new Line("1");
        lineSet = new HashSet<>();
        lineSet.add(line1);
        driverA = new Driver("A", lineSet);
        drivers = new HashSet<>();
        drivers.add(driverA);
        offDaysPreferences = new HashSet<>();
        driverA.setPreferredOffDays(offDaysPreferences);
    }

    @Test
    public void countOffDaysPreference(){
        for(int day = 1; day <= 14; day++){
            driverA.assignMorningShift(line1, day);
        }
        offDaysPreferences.add(1);
        assertEquals(0, scorer.evaluate(null, null, drivers));
        driverA.removeMorningShift(line1, 1);
        assertEquals(4, scorer.evaluate(null, null, drivers));

        offDaysPreferences.add(2);
        assertEquals(4, scorer.evaluate(null, null, drivers));
        driverA.removeMorningShift(line1, 2);
        assertEquals(8, scorer.evaluate(null, null, drivers));
    }


}
