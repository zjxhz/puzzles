package planning.busdriver.factory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wayne on 7/17/16.
 */
public class DaysFactory {
    public static List<Integer> createDays(int numberOfDays){
        List<Integer> days = new ArrayList<>();
        for(int i = 0; i < numberOfDays; i++){
            days.add(i + 1);
        }
        return days;
    }
}
