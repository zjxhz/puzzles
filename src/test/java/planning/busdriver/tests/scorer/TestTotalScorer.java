package planning.busdriver.tests.scorer;

import planning.busdriver.Driver;
import planning.busdriver.Line;
import planning.busdriver.scorer.Scorer;
import planning.busdriver.scorer.TotalScorer;
import org.junit.Test;

import java.util.Set;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by wayne on 7/16/16.
 */
public class TestTotalScorer {
    TotalScorer scorer = new TotalScorer();
    static class MockScorer implements Scorer {
        final int val;
        MockScorer(int val){
            this.val = val;
        }
        @Override
        public int evaluate(Set<Line> lines, Set<Integer> days, Set<Driver> drivers) {
            return val;
        }
    }
    @Test
    public void testDefaultScore(){
        assertEquals(0, scorer.evaluate(null, null, null));
    }

    @Test
    public void testSubScorer(){
        scorer.addScorer(new MockScorer(1));
        assertEquals(1, scorer.evaluate(null, null, null));
    }

    @Test
    public void testSubScorers(){
        scorer.addScorer(new MockScorer(1));
        scorer.addScorer(new MockScorer(-2));
        assertEquals(-1, scorer.evaluate(null, null, null));
    }
}
