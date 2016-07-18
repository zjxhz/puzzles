package planning.busdriver.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;

import java.util.Set;

/**
 * Created by wayne on 7/16/16.
 */
public class LongRestScorer implements Scorer{
    public int evaluate(Set<Line> lines, Set<Integer> days, Set<Driver> drivers){
        int sum = 0;
        for(Driver driver : drivers){
            sum += countLongRest(driver);
        }
        return sum;
    }

    private static int countLongRest(Driver driver) {
        int longRest = 0;
        int count = 0;
        for(int day = 1; day <= 14; day++){
            if(driver.hasRestedOn(day)){
                count++;
            } else {
                count = 0;
            }
            if(count == 3){
                longRest++;
            }
        }
        return longRest * 5;
    }
}
