package fr.adbonnin.cz2128.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;

import static fr.adbonnin.cz2128.serializer.JacksonUtils.updateObject;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class JacksonUtilsTest {

    private ObjectMapper mapper;

    private ByteArrayOutputStream out;

    private JsonGenerator generator;

    @BeforeMethod
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        out = new ByteArrayOutputStream();
        generator = mapper.getFactory().createGenerator(out);
    }

    @Test
    public void testUpdateObjectWithNull() throws Exception {
        updateObject(null, null, generator);
        assertTrue(out.toString().isEmpty());

        final ObjectNode node = mapper.createObjectNode();
        node.set("foo", new TextNode("foo"));
        node.set("bar", new TextNode("bar"));

        updateObject(null, node, generator);
        generator.close();
        assertEquals(mapper.readTree(out.toByteArray()), node);

        updateObject(node, null, generator);
        generator.close();
        assertEquals(mapper.readTree(out.toByteArray()), node);
    }

    @Test
    public void testUpdate() throws Exception {
        final ObjectNode oldNode = mapper.createObjectNode();
        oldNode.set("foo1", new TextNode("foo1"));
        oldNode.set("bar", new TextNode("bar1"));

        final ObjectNode newNode = mapper.createObjectNode();
        newNode.set("foo2", new TextNode("foo2"));
        newNode.set("bar", new TextNode("bar2"));

        final ObjectNode excepted = mapper.createObjectNode();
        excepted.set("foo1", new TextNode("foo1"));
        excepted.set("bar", new TextNode("bar2"));
        excepted.set("foo2", new TextNode("foo2"));

        updateObject(oldNode, newNode, generator);
        generator.close();

        assertEquals(mapper.readTree(out.toByteArray()), excepted);
    }
}
