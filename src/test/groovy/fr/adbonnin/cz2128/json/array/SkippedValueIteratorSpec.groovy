package fr.adbonnin.cz2128.json.array

import fr.adbonnin.cz2128.fixture.BaseJsonSpec

class SkippedValueIteratorSpec extends BaseJsonSpec {

    void "should iterate over elements"() {
        given:
        def parser = createParser(content)

        when:
        def iterator = new SkippedValueIterator(parser)

        then:
        iterator.hasNext()
        iterator.next() == null

        and:
        iterator.hasNext()
        iterator.next() == null

        and:
        !iterator.hasNext()

        when:
        iterator.next()

        then:
        thrown(NoSuchElementException)

        where:
        content = '[{}, {}]'
    }
}
