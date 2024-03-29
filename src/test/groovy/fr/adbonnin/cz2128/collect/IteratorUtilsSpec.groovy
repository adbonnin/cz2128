package fr.adbonnin.cz2128.collect

import spock.lang.Specification

import java.util.function.Predicate

class IteratorUtilsSpec extends Specification {

    void "should transform the first iterator element in optional"() {
        given:
        def iterator = list.iterator()

        expect:
        IteratorUtils.first(iterator) == expectedResult

        where:
        list      || expectedResult
        []        || Optional.empty()
        [null]    || Optional.empty()
        [1, 2, 3] || Optional.of(1)
    }

    void "should create a new ArrayList from an iterator"() {
        def list = [1, 2, 3]

        given:
        def iterator = list.iterator()

        when:
        def result = IteratorUtils.newArrayList(iterator)

        then:
        result == list

        and:
        result instanceof ArrayList
        !result.is(list)
    }

    void "should create a new LinkedHashSet from an iterator"() {
        def set = [1, 2, 3]

        given:
        def iterator = set.iterator()

        when:
        def result = IteratorUtils.newLinkedHashSet(iterator)

        then:
        result == (set as LinkedHashSet)

        and:
        result instanceof LinkedHashSet
        !result.is(set)
    }

    void "should add all elements to a list"() {
        given:
        def list = [1] as List

        when:
        def wasModified = IteratorUtils.addAll(list, elements.iterator())

        then:
        wasModified == expectWasModified
        list == expectedList

        where:
        elements || expectedList | expectWasModified
        []       || [1]          | false
        [1]      || [1, 1]       | true
        [1, 2]   || [1, 1, 2]    | true
    }

    void "should add all elements to a set"() {
        given:
        def list = [1] as LinkedHashSet

        when:
        def wasModified = IteratorUtils.addAll(list, elements.iterator())

        then:
        wasModified == expectWasModified
        new ArrayList(list) == expectedSet

        where:
        elements || expectedSet | expectWasModified
        []       || [1]         | false
        [1]      || [1]         | false
        [1, 2]   || [1, 2]      | true
    }

    void "should create a new LinkedHashMap from an iterator"() {
        def map = [a: 1, b: 2, c: 3] as LinkedHashMap

        given:
        def iterator = map.entrySet().iterator()

        when:
        def result = IteratorUtils.newLinkedHashMap(iterator)

        then:
        result == map

        and:
        result instanceof LinkedHashMap
        !result.is(map)
    }

    void "should put all elements"() {
        given:
        def map = [:]

        when:
        IteratorUtils.putAll(map, elements.entrySet().iterator())

        then:
        map == elements

        where:
        elements                      | _
        [:] as LinkedHashMap          | _
        [a: 1, b: 2] as LinkedHashMap | _
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
