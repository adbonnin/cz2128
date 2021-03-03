package fr.adbonnin.cz2128.base

import spock.lang.Specification

class PairSpec extends Specification {

    void "should have a consistent hashCode and equals"() {
        def pair12 = Pair.of(1, 2)
        def etr12 = new AbstractMap.SimpleEntry(1, 2)

        given:
        def pair = Pair.of(key, value)

        expect:
        pair.hashCode() == expectedHashCode

        and:
        (pair.equals(pair12)) == expectedEquals
        (pair.equals(etr12)) == expectedEquals
        (pair.hashCode() == pair12.hashCode()) == expectedEquals

        where:
        key | value || expectedHashCode | expectedEquals
        1   | 2     || 33               | true
        1   | 1     || 32               | false
        3   | 4     || 97               | false
    }

    void "should map key"() {
        def key = 2
        def value = 4

        given:
        def pair = Pair.of(key, value)

        expect:
        pair.mapKey(k -> k * 3) == Pair.of(6, value)
    }

    void "should map value"() {
        def key = 2
        def value = 4

        given:
        def pair = Pair.of(key, value)

        expect:
        pair.mapValue(v -> v * 3) == Pair.of(key, 12)
    }
}
