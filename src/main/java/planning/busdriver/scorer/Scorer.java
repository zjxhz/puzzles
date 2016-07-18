package planning.busdriver.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;

import java.util.Set;

/**
 * Created by wayne on 7/16/16.
 */
public interface Scorer {
    int evaluate(Set<Line> lines, Set<Integer> days, Set<Driver> drivers);
}
