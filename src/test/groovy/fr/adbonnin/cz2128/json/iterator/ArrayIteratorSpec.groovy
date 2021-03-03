package fr.adbonnin.cz2128.json.iterator

import fr.adbonnin.cz2128.fixture.BaseJsonSpec
import fr.adbonnin.cz2128.fixture.Cat

class ArrayIteratorSpec extends BaseJsonSpec {

    void "should iterate on object fields"() {

        def enterprise = [
            new Cat(id: 0, name: 'Spock'),
            new Cat(id: 1, name: 'Kirk')
        ]

        def discovery = [
            new Cat(id: 2, name: 'Archer')
        ]

        given:
        def json = DEFAULT_MAPPER.writeValueAsString([enterprise, discovery])
        def parser = DEFAULT_MAPPER.getFactory().createParser(json)

        and:
        def itr = new ArrayIterator(parser)

        expect:
        itr.hasNext()

        with(DEFAULT_MAPPER.readValue(itr.next(), Cat.LIST_TYPE_REF)) {
            size() == 2

            it[0].id == 0
            it[0].name == 'Spock'

            it[1].id == 1
            it[1].name == 'Kirk'
        }

        and:
        itr.hasNext()

        with(DEFAULT_MAPPER.readValue(itr.next(), Cat.LIST_TYPE_REF)) {
            size() == 1

            it[0].id == 2
            it[0].name == 'Archer'
        }

        and:
        !itr.hasNext()

        when:
        itr.next()

        then:
        thrown(NoSuchElementException)
    }
}
