package fr.adbonnin.cz2128.json.iterator

import fr.adbonnin.cz2128.fixture.BaseJsonSpec

class FieldObjectIteratorSpec extends BaseJsonSpec {

    void "should iterate over elements"() {
        given:
        def content = '{a: 0, b: 1}'
        def parser = createJsonParser(content)

        when:
        def iterator = new FieldObjectIterator(parser)

        then:
        iterator.hasNext()
        iterator.next() == 'a'

        and:
        iterator.hasNext()
        iterator.next() == 'b'

        and:
        !iterator.hasNext()

        when:
        iterator.next()

        then:
        thrown(NoSuchElementException)
    }
}
