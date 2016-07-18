package planning.busdriver.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;

import java.util.Set;

/**
 * Created by wayne on 7/16/16.
 */
public class DeviatedTargetLateShiftsScorer implements Scorer {
    @Override
    public int evaluate(Set<Line> lines, Set<Integer> days, Set<Driver> drivers) {
        int sum = 0;
        for(Driver driver : drivers){
            int count = 0;
            for(int day : days){
                if(driver.hasLateShift(day)){
                    count++;
                }
            }
            sum -= 8 * Math.abs(count - 4);
//            System.out.println(driver.getId() + " late shifts: " + count);
        }
        return sum;
    }
}
