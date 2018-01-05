package com.company;

/**
 * Simple class for containing two objects which are used in comparisons for testing purposes.
 */

public class TestTuple {

    private Object expected, toCheck;

    public TestTuple(Object expected, Object toCheck) {
        this.expected = expected;
        this.toCheck = toCheck;
    }

    // Getters for member variables.
    public Object getExpected() {
        return expected;
    }

    public Object getToCheck() {
        return toCheck;
    }

}
