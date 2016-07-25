package planning.busdriver.factory;

import planning.busdriver.Driver;
import planning.busdriver.Line;
import planning.busdriver.Plan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huanze on 7/25/2016.
 */
public class PlanFactory {
    public static Plan createPlanFromTextFile(String resourceUri){
        List<Line> lines = LinesFactory.createLines();
        List<Driver> drivers = DriversFactory.create(resourceUri, lines);
        return new Plan(lines, drivers, -1);
    }

    public static Plan createPlanFromImage(ImageDriversFactory.ImageProperties properties){
        List<Line> lines =LinesFactory.createLines();
        List<Driver> drivers = ImageDriversFactory.create(properties, lines);
        return new Plan(lines, drivers, -1);
    }
}
