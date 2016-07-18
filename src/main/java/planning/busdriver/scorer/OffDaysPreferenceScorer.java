package planning.busdriver.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;

import java.util.Set;

/**
 * Created by wayne on 7/16/16.
 */
public class OffDaysPreferenceScorer implements Scorer{
    @Override
    public int evaluate(Set<Line> lines, Set<Integer> days, Set<Driver> drivers) {
        int sum = 0;
        for(Driver driver : drivers){
            sum += countPreferredOffDays(driver);
        }
        return sum;
    }

    private static int countPreferredOffDays(Driver driver) {
        int offPreference = 0;
        for(int day = 1; day <= 14; day++) {
            if (driver.getPreferredOffDays().contains(day) && driver.hasRestedOn(day)) {
                offPreference += 4;
            }
        }
        return offPreference;
    }
}
