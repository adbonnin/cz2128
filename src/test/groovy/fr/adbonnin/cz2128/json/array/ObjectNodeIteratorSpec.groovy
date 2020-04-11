package fr.adbonnin.cz2128.json.array

import fr.adbonnin.cz2128.fixture.BaseJsonSpec

class ObjectNodeIteratorSpec extends BaseJsonSpec {

    void "should iterate over nodes"() {
        given:
        def parser = createParser(content)

        when:
        def iterator = new ObjectNodeIterator(parser, mapper)

        then:
        iterator.hasNext()
        iterator.next() == readObjectNode('{name:"Spock"}')

        and:
        iterator.hasNext()
        iterator.next() == readObjectNode('{name:"Kirk"}')

        and:
        !iterator.hasNext()

        when:
        iterator.next()

        then:
        thrown(NoSuchElementException)

        where:
        content = '[{name: "Spock"}, {name: "Kirk"}]'
    }
}
