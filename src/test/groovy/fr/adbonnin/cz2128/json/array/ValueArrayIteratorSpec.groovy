package fr.adbonnin.cz2128.json.array

import fr.adbonnin.cz2128.fixture.BaseJsonSpec
import fr.adbonnin.cz2128.fixture.Cat

class ValueArrayIteratorSpec extends BaseJsonSpec {

    void "should iterates over object elements"() {
        given:
        def content = '[null, {name: "Spock"}, {name: "Kirk"}]'
        def parser = createParser(content)

        when:
        def iterator = new ValueArrayIterator<>(parser, Cat, mapper)

        then:
        iterator.hasNext()
        !iterator.next()

        then:
        iterator.hasNext()
        iterator.next().name == 'Spock'

        and:
        iterator.hasNext()
        iterator.next().name == 'Kirk'

        and:
        !iterator.hasNext()

        when:
        iterator.next()

        then:
        thrown(NoSuchElementException)
    }

    void "should iterates over number elements"() {
        given:
        def content = '[null, 42]'
        def parser = createParser(content)

        when:
        def iterator = new ValueArrayIterator<>(parser, Number, mapper)

        then:
        iterator.hasNext()
        !iterator.next()

        then:
        iterator.hasNext()
        iterator.next() == 42

        and:
        !iterator.hasNext()

        when:
        iterator.next()

        then:
        thrown(NoSuchElementException)
    }
}
