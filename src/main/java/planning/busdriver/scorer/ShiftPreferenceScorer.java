package planning.busdriver.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;
import planning.busdriver.Shift;

import java.util.Set;

/**
 * Created by wayne on 7/16/16.
 */
public class ShiftPreferenceScorer implements Scorer{
    @Override
    public int evaluate(Set<Line> lines, Set<Integer> days, Set<Driver> drivers) {
        int sum = 0;
        for(Driver driver : drivers){
            sum += countShiftPreferences(driver);
        }
        return sum;
    }

    private static int countShiftPreferences(Driver driver) {
        int shiftPreference = 0;
        for (int day = 1; day <= 14; day++) {
            if (driver.isPreferredShift(day, Shift.MORNING) && driver.hasMorningShift(day)) {
                shiftPreference += 3;
            }
            if (driver.isPreferredShift(day, Shift.LATE) && driver.hasLateShift(day)) {
                shiftPreference += 3;
            }
        }
        return shiftPreference;
    }
}
