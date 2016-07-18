package planning.busdriver.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;

import java.util.Set;

/**
 * Created by wayne on 7/16/16.
 */
public class ConsecutiveLateShiftScorer implements Scorer {
    @Override
    public int evaluate(Set<Line> lines, Set<Integer> days, Set<Driver> drivers) {
        int sum = 0;
        for(Driver driver : drivers){
            int count = 0;
            for(int day : days){
                if(driver.hasLateShift(day)){
                    count++;
                } else {
                    count = 0;
                }
                if(count >= 4){
                    sum -= 10;
                }
            }
        }

        return sum;
    }
}
