package utils;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleTestTests {
    @Test
    void sumTest() {
        assertEquals(2, SimpleTest.sum(1,2));
    }
}
