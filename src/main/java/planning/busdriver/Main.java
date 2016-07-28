package planning.busdriver;

import planning.busdriver.factory.ImageDriversFactory;
import planning.busdriver.factory.PlanFactory;

public class Main {
    public static void main(String[] args) {
        final int maxAttempts = 50;
        int attempts = 0;
        Plan initialPlan = createInitialPlan();
        System.out.println("Drivers decoded from the screenshot file: ");
        for(Driver driver : initialPlan.getDrivers()){
            System.out.println(driver);
        }
        Plan bestPlan;
        int highest = Integer.MIN_VALUE;
        while (attempts++ < maxAttempts) {
            initialPlan = createInitialPlan();
            System.out.println("planRound: " + attempts);
            Plan plan = new Planner(initialPlan).plan(-1);//todo pass the optimal
            if (plan != null && plan.getScore() > highest) {
                highest = plan.getScore();
                bestPlan = plan;
                bestPlan.print();
            }
        }

    }

    private static Plan createInitialPlan() {
        ImageDriversFactory.ImageProperties properties = ImageDriversFactory.ImageProperties.createStandardProperties(
                "planning/busdriver/shifts3.png");

        return PlanFactory.createPlanFromImage(properties);

    }
}
