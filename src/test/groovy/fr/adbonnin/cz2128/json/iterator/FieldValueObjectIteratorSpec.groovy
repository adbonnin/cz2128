package fr.adbonnin.cz2128.json.iterator

import fr.adbonnin.cz2128.collect.IteratorUtils
import fr.adbonnin.cz2128.fixture.BaseJsonSpec
import fr.adbonnin.cz2128.fixture.Cat

class FieldValueObjectIteratorSpec extends BaseJsonSpec {

    void "should raise an exception if there is no more elements"() {
        def content = '{a: true}'

        given:
        def parser = createJsonParser(content)
        def reader = DEFAULT_MAPPER.readerFor(Boolean)

        when:
        def iterator = new FieldValueObjectIterator<Cat>(parser, reader)

        then:
        iterator.hasNext()
        iterator.next()

        and:
        !iterator.hasNext()

        when:
        iterator.next()

        then:
        thrown(NoSuchElementException)
    }

    void "should iterates over object elements"() {
        given:
        def parser = createJsonParser(content)
        def reader = DEFAULT_MAPPER.readerFor(Cat)

        when:
        def iterator = new FieldValueObjectIterator<Cat>(parser, reader, fieldPredicate, valuePredicate)
        def map = IteratorUtils.newLinkedHashMap(iterator)

        then:
        map == expectedResult

        where:
        fieldPredicate  | valuePredicate    || expectedResult
        ({ false })     | ({ false })       || [:]
        ({ false })     | ({ true })        || [:]
        ({ true })      | ({ false })       || [:]
        ({ true })      | ({ true })        || [a: null, b: new Cat(id: 0), c: new Cat(id: 1)]

        ({ it == 'b' }) | ({ true })        || [b: new Cat(id: 0)]
        ({ true })      | ({ it?.id == 1 }) || [c: new Cat(id: 1)]

        content = '{a: null, b: {id: 0}, c: {id: 1}}'
    }

    void "should iterates over number elements"() {
        given:
        def parser = createJsonParser(content)
        def reader = DEFAULT_MAPPER.readerFor(Number)

        when:
        def iterator = new FieldValueObjectIterator<Number>(parser, reader, fieldPredicate, valuePredicate)
        def map = IteratorUtils.newLinkedHashMap(iterator)

        then:
        map == expectedResult

        where:
        fieldPredicate  | valuePredicate  || expectedResult
        ({ false })     | ({ false })     || [:]
        ({ false })     | ({ true })      || [:]
        ({ true })      | ({ false })     || [:]
        ({ true })      | ({ true })      || [a: null, b: 42, c: 123]

        ({ it == 'b' }) | ({ true })      || [b: 42]
        ({ true })      | ({ it == 123 }) || [c: 123]

        content = '{a: null, b: 42, c: 123}'
    }
}
