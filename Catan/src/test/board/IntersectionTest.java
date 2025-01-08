package test.board;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class IntersectionTest {
    private Intersection intersection;

    @Before
    public void setUp() {
        intersection = new Intersection(0, 1); // Example intersection
    }

    @Test
    public void testIntersectionInitialization() {
        assertEquals("X coordinate should be 0", 0, intersection.getX());
        assertEquals("Y coordinate should be 1", 1, intersection.getY());
    }

    @Test
    public void testEquality() {
        Intersection anotherIntersection = new Intersection(0, 1);
        assertEquals("Intersections with the same coordinates should be equal", intersection, anotherIntersection);
    }
}
