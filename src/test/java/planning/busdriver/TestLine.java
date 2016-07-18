package planning.busdriver.tests;

import planning.busdriver.Driver;
import planning.busdriver.Line;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by huanze on 7/15/2016.
 */
public class TestLine {
    private Line line1;
    private Set<Line> lineSet;
    private Driver a;
    private Driver b;

    @Before
    public void setUp(){
        line1 = new Line("1");
        lineSet = new HashSet<>();
        lineSet.add(line1);
        a = new Driver("A", lineSet);
        b = new Driver("B", lineSet);
    }
    @Test
    public void setMorningShift(){
        assertEquals(null, line1.getMorningShift(1));
        line1.assignMorningShift(1, a);
        assertEquals(a, line1.getMorningShift(1));
    }

    @Test
    public void switchMorningShift(){
        line1.assignMorningShift(1, a);
        assertEquals(a, line1.getMorningShift(1));
        line1.assignMorningShift(1, b);
        assertEquals(b, line1.getMorningShift(1));
    }

    @Test
    public void setLateShift(){
        assertEquals(null, line1.getLateShift(1));
        line1.assignLateShift(1, a);
        assertEquals(a, line1.getLateShift(1));
    }

    @Test
    public void switchLateShift(){
        line1.assignLateShift(1, a);
        assertEquals(a, line1.getLateShift(1));
        line1.assignLateShift(1, b);
        assertEquals(b, line1.getLateShift(1));
    }
}
