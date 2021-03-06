package fr.adbonnin.cz2128.json.iterator

import com.fasterxml.jackson.databind.node.DoubleNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import fr.adbonnin.cz2128.fixture.BaseJsonSpec

class JsonNodeArrayIteratorSpec extends BaseJsonSpec {

    void "should iterate over nodes"() {
        given:
        def content = '[null, 4.2, "Spock", {name: "Kirk"}]'
        def parser = createJsonParser(content)

        when:
        def iterator = new JsonNodeArrayIterator(parser, DEFAULT_MAPPER)

        then:
        iterator.hasNext()
        iterator.next() == NullNode.instance

        and:
        iterator.hasNext()
        iterator.next() == new DoubleNode(4.2)

        and:
        iterator.hasNext()
        iterator.next() == new TextNode("Spock")

        and:
        iterator.hasNext()
        iterator.next() == readObjectNode('{name: "Kirk"}')

        and:
        !iterator.hasNext()

        when:
        iterator.next()

        then:
        thrown(NoSuchElementException)
    }
}
