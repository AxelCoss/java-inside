package fr.umlv.javainside;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void x() {
        assertEquals(3, new Point(3, 5).x());
    }

    @Test
    void y() {
        assertEquals(5, new Point(3, 5).y());
    }

}