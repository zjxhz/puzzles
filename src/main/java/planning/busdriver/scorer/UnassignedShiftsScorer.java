package planning.busdriver.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by wayne on 7/16/16.
 */
public class UnassignedShiftsScorer implements Scorer{


    @Override
    public int evaluate(Set<Line> lines, Set<Integer> days, Set<Driver> drivers) {
        int sum = 0;
        for(Line line : lines){
            for(int day : days){
                if(line.getMorningShift(day) == null){
                    sum -= 20;
                }
                if(line.getLateShift(day) == null){
                    sum -= 20;
                }
            }
        }
        return sum;
    }
}
