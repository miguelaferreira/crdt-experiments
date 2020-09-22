package com.github.miguelaferreira.crdt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScratchBook {

    @Test
    void stringFormatWidth() {
        final String format = String.format("%c%d: %10s", 'A', 1, "foo");

        Assertions.assertEquals("A1:        foo", format);
    }
}
