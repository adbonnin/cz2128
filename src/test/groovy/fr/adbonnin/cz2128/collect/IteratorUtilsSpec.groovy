package fr.adbonnin.cz2128.collect

import spock.lang.Specification

import java.util.function.Predicate

class IteratorUtilsSpec extends Specification {

    void "should add all elements"() {
        given:
        def list = []

        when:
        def wasModified = IteratorUtils.addAll(list, elements.iterator())

        then:
        wasModified == expectWasModified
        list == elements

        where:
        elements || expectWasModified
        []       || false
        [1, 2]   || true
    }

    void "should count elements"() {
        expect:
        IteratorUtils.count(iterator) == expectedCount

        where:
        elements || expectedCount
        []       || 0
        [1, 2]   || 2

        iterator = elements.iterator()
    }

    void "should filter elements"() {
        given:
        def filtered = IteratorUtils.filter(iterator, predicate)

        expect:
        filtered.hasNext()
        filtered.next() == 'A'

        and:
        filtered.hasNext()
        filtered.next() == 'C'

        and:
        !filtered.hasNext()

        when:
        filtered.next()

        then:
        thrown(NoSuchElementException)

        where:
        iterator = ['A', 'B', 'C'].iterator()
        predicate = { it != 'B' } as Predicate<String>
    }

    void "should find an element"() {
        when:
        def found = IteratorUtils.find(iterator, predicate)

        then:
        found.isPresent() == expectdIsPresent
        found.orElse(defaultValue) == expectedValue

        where:
        elements   || expectdIsPresent | expectedValue
        []         || false            | 'D'
        ['A', 'B'] || true             | 'A'
        ['B', 'C'] || true             | 'C'
        ['B', 'B'] || false            | 'D'

        iterator = elements.iterator()
        predicate = { it != 'B' } as Predicate<String>
        defaultValue = 'D'
    }

    void "should transform empty entry iterator to empty value iterator"() {
        def emptyMap = [:]

        given:
        def entryIterator = emptyMap.entrySet().iterator()

        when:
        def valueIterator = IteratorUtils.valueIterator(entryIterator)

        then:
        !valueIterator.hasNext()

        when:
        entryIterator.next()

        then:
        thrown(NoSuchElementException)
    }

    void "should transform entry iterator to value iterator"() {
        def map = [
            a: 1,
            b: 2
        ]

        given:
        def entryIterator = map.iterator()

        when:
        def valueIterator = IteratorUtils.valueIterator(entryIterator)

        then:
        valueIterator.hasNext()
        valueIterator.next() == 1

        and:
        valueIterator.hasNext()
        valueIterator.next() == 2

        and:
        !valueIterator.hasNext()

        when:
        valueIterator.next()

        then:
        thrown(NoSuchElementException)
    }

    void "should transform a value into an iterator"() {
        def value = new Object()

        when:
        def iterator = IteratorUtils.singletonIterator(value)

        then:
        iterator.hasNext()
        iterator.next() == value

        and:
        !iterator.hasNext()

        when:
        iterator.next()

        then:
        thrown(NoSuchElementException)
    }
}
