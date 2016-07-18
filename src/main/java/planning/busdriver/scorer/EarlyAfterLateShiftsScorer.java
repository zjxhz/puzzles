package planning.busdriver.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;

import java.util.Set;

/**
 * Created by wayne on 7/16/16.
 */
public class EarlyAfterLateShiftsScorer implements Scorer{
    public int evaluate(Set<Line> lines, Set<Integer> days, Set<Driver> drivers) {
        int sum = 0;
        for(Driver driver : drivers){
            for(int day : days){
                if(driver.hasMorningShift(day) && driver.hasLateShift(day - 1)){
                    sum -= 30;
                }
            }
        }
        return sum;
    }
}
