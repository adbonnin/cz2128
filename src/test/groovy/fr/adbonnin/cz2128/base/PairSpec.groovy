package fr.adbonnin.cz2128.base

import spock.lang.Specification

class PairSpec extends Specification {

    void "should have a consistent hashCode and equals"() {
        def pair12 = Pair.of(1, 2)

        given:
        def pair = Pair.of(key, value)

        expect:
        pair.hashCode() == expectedHashCode

        and:
        (pair.equals(pair12)) == expectedEquals
        (pair.hashCode() == pair12.hashCode()) == expectedEquals

        where:
        key | value || expectedHashCode | expectedEquals
        1   | 2     || 33               | true
        1   | 1     || 32               | false
        3   | 4     || 97               | false
    }
}
