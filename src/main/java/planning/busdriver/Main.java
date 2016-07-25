package planning.busdriver;

import planning.busdriver.factory.ImageDriversFactory;
import planning.busdriver.factory.PlanFactory;

public class Main {
    public static void main(String[] args) {
        final int maxAttempts = 50;
        int attempts = 0;
        Plan initialPlan = createInitialPlan();
        System.out.println(initialPlan.getDrivers());
        Plan bestPlan;
        int highest = Integer.MIN_VALUE;
        while (attempts++ < maxAttempts){
            initialPlan = createInitialPlan();
            System.out.println("planRound: " + attempts);
            Plan plan = new Planner(initialPlan).plan(-1);
            if(plan != null && plan.getScore() > highest){
                highest = plan.getScore();
                bestPlan = plan;
                bestPlan.print();
            }
        }

    }

    private static Plan createInitialPlan() {
        ImageDriversFactory.ImageProperties properties = new ImageDriversFactory.ImageProperties(
                "planning/busdriver/shifts3.png", 408, 242, 331, 242, 352);//driverStartX, driverStartY, line1X, qualifiedLine1Y, line2X
//        ImageDriversFactory.ImageProperties properties = new ImageDriversFactory.ImageProperties(
//                "planning/busdriver/shiftsg.jpg", 413, 250, 330, 270, 352);
        return PlanFactory.createPlanFromImage(properties);

    }
}
