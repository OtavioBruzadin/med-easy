package utils;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleTestTests {
    @Test
    void sumTest() {
        assertEquals(3, SimpleTest.sum(1,2));
    }
}
