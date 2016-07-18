package planning.busdriver.factory;

import planning.busdriver.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wayne on 7/17/16.
 */
public class LinesFactory {
    public static List<Line> createLines(){
        List<Line> lines = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Line line = new Line(String.valueOf(i));
            lines.add(line);
        }
        return lines;
    }
}
