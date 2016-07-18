package planning.busdriver.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by wayne on 7/16/16.
 */
public class TotalScorer implements Scorer{

    List<Scorer> scorers = new ArrayList<>();
    @Override
    public int evaluate(Set<Line> lines, Set<Integer> days, Set<Driver> drivers) {
        int sum = 0;
        for(Scorer scorer : scorers){
            int val = scorer.evaluate(lines, days, drivers);
            System.out.println(scorer.getClass() + ": " + val);
            sum += val;
        }
        return sum;
    }

    public void addScorer(Scorer scorer) {
        scorers.add(scorer);
    }
}
