package fr.adbonnin.albedo.util.web;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class HttpUtilsTest {

    @Test
    public void expand() throws Exception {
        final Map<String, Object> pathVariables = new HashMap<>();
        pathVariables.put("test", "2");
        assertEquals("1/2/3", HttpUtils.expand("1/{test}/3", pathVariables));

        pathVariables.put("null", null);
        assertEquals("1//3", HttpUtils.expand("1/{null}/3", pathVariables));
        assertEquals("1//3", HttpUtils.expand("1/{unknown}/3", pathVariables));

        pathVariables.put("array", new String[]{"2", "3"});
        assertEquals("1/2/4", HttpUtils.expand("1/{array}/4", pathVariables));
        assertEquals("1/2/2/4", HttpUtils.expand("1/{array}/{array}/4", pathVariables)); // Only first value is used
    }
}
