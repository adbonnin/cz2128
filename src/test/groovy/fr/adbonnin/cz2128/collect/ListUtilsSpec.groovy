package fr.adbonnin.cz2128.collect

import spock.lang.Specification

class ListUtilsSpec extends Specification {

    void "should create a new ArrayList"() {
        def list = [1, 2, 3]

        given:
        def iterator = list.iterator()

        when:
        def result = ListUtils.newArrayList(iterator)

        then:
        result == list

        and:
        result instanceof ArrayList
        !result.is(list)
    }
}
