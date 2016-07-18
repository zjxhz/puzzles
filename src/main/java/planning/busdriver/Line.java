package planning.busdriver;

import planning.busdriver.exception.TooManyShiftsADayException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huanze on 7/15/2016.
 */
public class Line {
    private Map<Integer, Driver> morningShifts = new HashMap<>();
    private Map<Integer, Driver> lateShifts = new HashMap<>();

    private String number;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        return number.equals(line.number);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    public Line(String number) {
        this.number = number;

    }

    public void assignMorningShift(int day, Driver driver) {
        if(driver.hasLateShift(day)){
            throw new TooManyShiftsADayException();
        }
        Driver current = morningShifts.get(day);
        if(current != null && current != driver){
            current.removeMorningShift(this, day);
        }
        driver.assignMorningShift(this, day);
        morningShifts.put(day, driver);
    }

    public Driver getMorningShift(int day) {
        return morningShifts.get(day);
    }

    public Driver getLateShift(int day) {
        return  lateShifts.get(day);
    }

    public void assignLateShift(int day, Driver driver) {
        if(driver.hasMorningShift(day)){
            throw new TooManyShiftsADayException();
        }
        Driver current = lateShifts.get(day);
        if(current != null && current != driver){
            current.removeLateShift(this, day);
        }
        driver.assignLateShift(this, day);
        lateShifts.put(day, driver);
    }

    public String toString(){
        return "Line " + number;
    }

    public void assignShift(int day, Shift shift, Driver driver) {
        if(shift == Shift.MORNING){
            assignMorningShift(day, driver);
        } else {
            assignLateShift(day, driver);
        }
    }

    public void cancel(int day, Shift shift, Driver driver) {
        if(shift == Shift.MORNING){
            driver.removeMorningShift(this, day);
            morningShifts.remove(day);
        } else {
            driver.removeLateShift(this, day);
            lateShifts.remove(day);
        }
    }

    public Driver getShift(int day, Shift shift) {
        return shift == Shift.MORNING ? getMorningShift(day) : getLateShift(day);
    }
}
