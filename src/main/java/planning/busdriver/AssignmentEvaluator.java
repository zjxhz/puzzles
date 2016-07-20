package planning.busdriver;

import java.util.Comparator;

/**
 * Created by huanze on 7/15/2016.
 */
public class AssignmentEvaluator implements Comparator<Assignment> {
    public static int ILLEGAL_ASSIGNMENT_VALUE = -9999;
    public static int evaluate(Line line, int day, Shift shift, Driver driver) {
        if (!driver.canAssign(line, day, shift)) {
            return ILLEGAL_ASSIGNMENT_VALUE;
        }
        int value = 0;
        if (driver.getPreferredOffDays().contains(day)) {
            value -= 3;
        }
        if (driver.isPreferredShift(day, shift)) {
            value += 4;
        }
        if (shift == Shift.LATE && driver.getLateShiftCount() >= 4) {
            return ILLEGAL_ASSIGNMENT_VALUE;
//            value -=  (driver.getLateShiftCount() - 3) * 8; todo is this too strict?
        }
        if (shift == Shift.MORNING && driver.hasLateShift(day - 1)) {
            return ILLEGAL_ASSIGNMENT_VALUE;
            //value -= 30; //todo is this too strict?
        }
        if (shift == Shift.LATE && driver.hasMorningShift(day + 1)) {//happens in replanning
            return ILLEGAL_ASSIGNMENT_VALUE;
            //value -= 30; //todo is this too strict?
        }
        if (shift == Shift.LATE && getConsecutiveLateShiftBefore(day, driver) >= 3) {
//            value -= 10 * (getConsecutiveLateShiftBefore(day, driver) - 3 + 1);
            return ILLEGAL_ASSIGNMENT_VALUE; //todo is this too strict?
        }
        if (hasRestedForTwoDays(driver, day)) {
            value -= 5;
        }

        return value;
    }

    private static boolean hasRestedForTwoDays(Driver driver, int day) {
        if (day < 3) {
            return false;
        }
        return driver.hasRestedOn(day - 1) && driver.hasRestedOn(day - 2);
    }

    private static int getConsecutiveLateShiftBefore(int day, Driver driver) {
        int count = 0;
        for (int i = day - 1; i > 0; i--) {
            if (driver.hasLateShift(i)) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    @Override
    public int compare(Assignment o1, Assignment o2) {
        int result = evaluate(o1.line, o1.day, o1.shift, o1.driver) - evaluate(o2.line, o2.day, o2.shift, o2.driver);
        if(result == 0){
            //the more late shifts, the less likely to be assigned, thus has smaller value
            result = o2.driver.getLateShiftCount() - o1.driver.getLateShiftCount();
        }
        return result == 0 ? o1.driver.compareTo(o2.driver) : result;
    }

    public static int evaluate(Assignment assignment) {
        return evaluate(assignment.line, assignment.day, assignment.shift, assignment.driver);
    }
}
