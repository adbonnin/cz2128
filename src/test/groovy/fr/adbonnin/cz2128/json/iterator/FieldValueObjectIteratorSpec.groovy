package fr.adbonnin.cz2128.json.iterator

import fr.adbonnin.cz2128.fixture.BaseJsonSpec
import fr.adbonnin.cz2128.fixture.Cat

class FieldValueObjectIteratorSpec extends BaseJsonSpec {

    void "should iterates over object elements"() {
        given:
        def content = '{a: null, b: {name: "Spock"}, c: {name: "Kirk"}}'
        def parser = createJsonParser(content)
        def reader = DEFAULT_MAPPER.readerFor(Cat)

        when:
        def iterator = new FieldValueObjectIterator<Cat>(parser, reader)

        then:
        iterator.hasNext()

        with(iterator.next()) {
            key == 'a'
            !value
        }

        then:
        iterator.hasNext()

        with(iterator.next()) {
            key == 'b'
            value.name == 'Spock'
        }

        and:
        iterator.hasNext()

        with(iterator.next()) {
            key == 'c'
            value.name == 'Kirk'
        }

        and:
        !iterator.hasNext()

        when:
        iterator.next()

        then:
        thrown(NoSuchElementException)
    }

    void "should iterates over number elements"() {
        given:
        def content = '{a: null, b: 42}'
        def parser = createJsonParser(content)
        def reader = DEFAULT_MAPPER.readerFor(Number)

        when:
        def iterator = new FieldValueObjectIterator<Number>(parser, reader)

        then:
        iterator.hasNext()

        with(iterator.next()) {
            key == 'a'
            !value
        }

        then:
        iterator.hasNext()

        with(iterator.next()) {
            key == 'b'
            value == 42
        }

        and:
        !iterator.hasNext()

        when:
        iterator.next()

        then:
        thrown(NoSuchElementException)
    }
}
