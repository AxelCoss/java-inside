package fr.umlv.javainside;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PointTest {

    @Test
    public void x() {
        assertEquals(3, new Point(3, 5).x());
    }

    @Test
    public void y() {
        assertEquals(5, new Point(3, 5).y());
    }

}