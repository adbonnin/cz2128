package fr.adbonnin.cz2128.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class JacksonUtilsTest {

    private ObjectMapper mapper;

    @BeforeMethod
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
    }

    @Test
    public void testUpdateObject() throws Exception {

    }
}
