package fr.adbonnin.cz2128.json.array

import fr.adbonnin.cz2128.fixture.BaseJsonSpec
import fr.adbonnin.cz2128.fixture.Cat

class ValueIteratorSpec extends BaseJsonSpec {

    void "should iterate over elements"() {
        given:
        def parser = createParser(content)

        when:
        def iterator = new ValueIterator<>(parser, Cat, mapper)

        then:
        iterator.hasNext()
        iterator.next().name == 'Spock'

        and:
        iterator.hasNext()
        iterator.next().name == 'Kirk'

        and:
        !iterator.hasNext()

        when:
        iterator.next()

        then:
        thrown(NoSuchElementException)

        where:
        content = '[{name: "Spock"}, {name: "Kirk"}]'
    }
}
