package fr.adbonnin.cz2128.json.iterator

import com.fasterxml.jackson.databind.node.DoubleNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import fr.adbonnin.cz2128.base.Pair
import fr.adbonnin.cz2128.fixture.BaseJsonSpec

class JsonNodeObjectIteratorSpec extends BaseJsonSpec {

    void "should iterate over nodes"() {
        given:
        def content = '{a: null, b: 4.2, c: "Spock", d: {name: "Kirk"}}'
        def parser = createJsonParser(content)

        when:
        def iterator = new JsonNodeObjectIterator(parser, DEFAULT_MAPPER)

        then:
        iterator.hasNext()
        iterator.next() == Pair.of('a', NullNode.instance)

        and:
        iterator.hasNext()
        iterator.next() == Pair.of('b', new DoubleNode(4.2))

        and:
        iterator.hasNext()
        iterator.next() == Pair.of('c', new TextNode("Spock"))

        and:
        iterator.hasNext()
        iterator.next() == Pair.of('d', readObjectNode('{name: "Kirk"}'))

        and:
        !iterator.hasNext()

        when:
        iterator.next()

        then:
        thrown(NoSuchElementException)
    }
}
