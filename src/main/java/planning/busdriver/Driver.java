package planning.busdriver;

import planning.busdriver.exception.AssignmentOnOffDaysException;
import planning.busdriver.exception.UnqualifiedDriverException;

import java.util.*;

/**
 * Created by huanze on 7/15/2016.
 */
public class Driver implements Comparable<Driver> {
    private String id;
    private Map<Line, Set<Integer>> morningShifts = new HashMap<>();
    private Map<Line, Set<Integer>> lateShifts = new HashMap<>();
    private Set<Line> qualifiedLines = new HashSet<>();
    private Set<Integer> offDays = new HashSet<>();
    private Map<Integer, Shift> preferredShifts;
    private Set<Integer> preferredOffDays = new HashSet<>();

    public Set<Integer> getPreferredOffDays() {
        return preferredOffDays;
    }

    public void setPreferredOffDays(Set<Integer> preferredOffDays) {
        this.preferredOffDays = preferredOffDays;
    }

    public Driver(String id) {
        this(id, new HashSet<Line>());
    }

    public String getId() {
        return this.id;
    }

    public Driver(String id, Set<Line> qualifiedLines) {
        this(id, qualifiedLines, new HashSet<Integer>());
    }

    public Driver(String id, Set<Line> qualifiedLines, Set<Integer> offDays) {
        this.qualifiedLines = qualifiedLines;
        this.id = id;
        this.offDays = offDays;
    }

    public boolean hasMorningShift(int day) {
        return hasShift(morningShifts, day);
    }

    private boolean hasShift(Map<Line, Set<Integer>> shiftMap, int day) {
        for (Line line : shiftMap.keySet()) {
            Set<Integer> shifts = shiftMap.get(line);
            if (shifts != null && shifts.contains(day)) {
                return true;
            }
        }
        return false;


    }

    public void assignMorningShift(Line line, int day) {
        assignShift(morningShifts, line, day);
    }

    private void assignShift(Map<Line, Set<Integer>> shiftMap, Line line, int day) {
        if (!isQualified(line)) {
            throw new UnqualifiedDriverException();
        }
        if (isOffDay(day)) {
            throw new AssignmentOnOffDaysException();
        }
        Set<Integer> shifts = shiftMap.get(line);
        if (shifts == null) {
            shifts = new HashSet<>();
            shiftMap.put(line, shifts);
        }
        shifts.add(day);
    }

    private boolean isOffDay(int day) {
        return offDays.contains(day);
    }

    private boolean isQualified(Line line) {
        return qualifiedLines.contains(line);
    }


    public void removeMorningShift(Line line, int day) {
        Set<Integer> shifts = morningShifts.get(line);
        shifts.remove(day);
    }

    public boolean hasLateShift(int day) {
        return hasShift(lateShifts, day);
    }

    public void assignLateShift(Line line, int day) {
        assignShift(lateShifts, line, day);
    }

    public void removeLateShift(Line line, int day) {
        Set<Integer> shifts = lateShifts.get(line);
        shifts.remove(day);
    }

    public void setPreferredShifts(Map<Integer, Shift> preferredShifts) {
        this.preferredShifts = preferredShifts;
    }

    public boolean isPreferredShift(int day, Shift shift) {
        if (preferredShifts == null) {
            return false;
        }
        return preferredShifts.get(day) == shift;
    }

    public boolean canAssign(Line line, int day, Shift shift) {
        return isQualified(line) && !isOffDay(day) && !hasShift(day);
    }

    public boolean hasShift(int day) {
        return hasMorningShift(day) || hasLateShift(day);
    }

    public boolean hasRestedOn(int day) {
        return !hasShift(day);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id)
                .append(", ")
                .append(qualifiedLines)
                .append(", ")
                .append(offDays)
                .append(", ")
                .append(preferredOffDays)
                .append(", ")
                .append(preferredShifts);
        return sb.toString();
    }

    public Set<Integer> getOffDays() {
        return offDays;
    }

    public Set<Line> getQualifiedLines() {

        return qualifiedLines;
    }

    @Override
    public int compareTo(Driver o) {
        if (qualifiedLines.size() < o.qualifiedLines.size()) {
            return 1;
        }
        return -id.compareTo(o.id);
    }

    public int getLateShiftCount() {
        int count = 0;
        for (Line line : lateShifts.keySet()) {
            count += lateShifts.get(line).size();
        }
        return count;
    }

    public Map<Integer, Shift> getPreferredShifts() {
        return preferredShifts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Driver driver = (Driver) o;

        return id != null ? id.equals(driver.id) : driver.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /**
     * Duplicates a driver without assigning any shifts
     *
     * @return A duplicated driver without any shifts assigned
     */
    public Driver duplicate() {
        Driver driver = new Driver(id, qualifiedLines, offDays);
        driver.preferredOffDays = preferredOffDays;
        driver.preferredShifts = preferredShifts;
        return driver;
    }
}
