package de.intranda.goobi.plugins;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

public class SamplePluginTest {

    @Test
    public void testVersion() throws IOException {
        String s = "xyz";
        assertNotNull(s);
    }
}
