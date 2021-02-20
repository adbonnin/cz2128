package fr.adbonnin.cz2128.json.iterator

import fr.adbonnin.cz2128.fixture.BaseJsonSpec

class SkipChildrenArrayIteratorSpec extends BaseJsonSpec {

    void "should iterate over elements"() {
        given:
        def parser = createJsonParser(content)

        when:
        def iterator = new SkipChildrenArrayIterator(parser)

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
