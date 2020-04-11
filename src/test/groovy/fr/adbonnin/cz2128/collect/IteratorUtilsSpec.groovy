package fr.adbonnin.cz2128.collect

import spock.lang.Specification

import java.util.function.Predicate

class IteratorUtilsSpec extends Specification {

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
        found.isEmpty() == expectdIsEmpty
        found.orElse(defaultValue) == expectedValue

        where:
        elements   || expectdIsEmpty | expectedValue
        []         || true           | 'D'
        ['A', 'B'] || false          | 'A'
        ['B', 'C'] || false          | 'C'
        ['B', 'B'] || true           | 'D'

        iterator = elements.iterator()
        predicate = { it != 'B' } as Predicate<String>
        defaultValue = 'D'
    }
}
