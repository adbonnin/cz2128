package fr.adbonnin.cz2128.json.iterator

import fr.adbonnin.cz2128.fixture.BaseJsonSpec
import fr.adbonnin.cz2128.fixture.Cat

class ObjectIteratorSpec extends BaseJsonSpec {

    void "should raise an exception if the element is not an object"() {
        given:
        def parser = DEFAULT_MAPPER.getFactory().createParser("[]")

        and:
        def itr = new ObjectIterator(parser)

        when:
        itr.hasNext()

        then:
        def exception = thrown(IllegalStateException)
        exception.message == 'Parser must start with an object token; token: START_ARRAY'
    }

    void "should iterate on object fields"() {

        def enterprise = [
            new Cat(id: 0, name: 'Spock'),
            new Cat(id: 1, name: 'Kirk')
        ]

        def discovery = [
            new Cat(id: 2, name: 'Archer')
        ]

        given:
        def json = DEFAULT_MAPPER.writeValueAsString([enterprise: enterprise, discovery: discovery])
        def parser = DEFAULT_MAPPER.getFactory().createParser(json)

        and:
        def itr = new ObjectIterator(parser)

        expect:
        itr.hasNext()

        when:
        def enterprisePair = itr.next()
        def parsedEnterprise = DEFAULT_MAPPER.readValue(parser, Cat.LIST_TYPE_REF)

        then:
        enterprisePair.key == 'enterprise'
        parsedEnterprise == enterprise

        and:
        itr.hasNext()

        when:
        def discoveryPair = itr.next()
        def parsedDiscovery = DEFAULT_MAPPER.readValue(parser, Cat.LIST_TYPE_REF)

        then:
        discoveryPair.key == 'discovery'
        parsedDiscovery == discovery

        and:
        !itr.hasNext()

        when:
        itr.next()

        then:
        thrown(NoSuchElementException)
    }
}
