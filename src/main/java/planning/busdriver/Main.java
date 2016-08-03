package planning.busdriver;

import planning.busdriver.factory.ImageDriversFactory;
import planning.busdriver.factory.PlanFactory;

public class Main {
    public static void main(String[] args) {
        Plan initialPlan = createInitialPlan();
        System.out.println("Drivers decoded from the screenshot file: ");
        for(Driver driver : initialPlan.getDrivers()){
            System.out.println(driver);
        }
        Planner.createPlan(initialPlan, 154);
    }

    private static Plan createInitialPlan() {
        ImageDriversFactory.ImageProperties properties = ImageDriversFactory.ImageProperties.createStandardProperties(
                "planning/busdriver/shifts3.png");//optimal = 154, 85% = 131

        return PlanFactory.createPlanFromImage(properties);

    }
}
