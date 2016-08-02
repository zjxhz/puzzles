package planning.busdriver;

/**
 * Created by huanze on 7/15/2016.
 */
public class Assignment {
    Line line;
    int day;
    Shift shift;
    Driver driver;

    public Assignment(){

    }
    public Assignment(Line line, int day, Shift shift, Driver driver) {
        this.line = line;
        this.day = day;
        this.shift = shift;
        this.driver = driver;
    }

    @Override
    public String toString() {
        int value = AssignmentEvaluator.evaluate(line, day, shift, driver);
        return "Assignment{" +
                "line=" + line +
                ", day=" + day +
                ", shift=" + shift +
                ", driver=" + (driver == null ? null : driver.getId()) +
                ", value=" + value +
                '}';
    }

    public void assign() {
        line.assignShift(day, shift, driver);
    }
}
