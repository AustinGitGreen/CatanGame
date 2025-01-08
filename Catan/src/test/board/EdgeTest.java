package test.board;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class EdgeTest {
    private Intersection start;
    private Intersection end;
    private Edge edge;

    @Before
    public void setUp() {
        start = new Intersection(0, 0);
        end = new Intersection(1, 0);
        edge = new Edge(start, end);
    }

    @Test
    public void testEdgeInitialization() {
        assertEquals("Start intersection should be (0,0)", start, edge.getStart());
        assertEquals("End intersection should be (1,0)", end, edge.getEnd());
    }
}
