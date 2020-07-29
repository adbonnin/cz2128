package fr.adbonnin.cz2128

import fr.adbonnin.cz2128.fixture.BaseJsonProviderSpec
import fr.adbonnin.cz2128.fixture.Cat
import fr.adbonnin.cz2128.json.provider.MemoryJsonProvider
import spock.lang.Subject

import java.util.function.Predicate
import java.util.stream.Stream

class MemoryJsonSetRepositorySpec extends BaseJsonProviderSpec {

    @Subject
    def stringJsonProvider = new MemoryJsonProvider()

    @Override
    JsonProvider setupJsonProvider(String content) {
        stringJsonProvider.content = content
        return stringJsonProvider
    }

    @Override
    String getJsonProviderContent() {
        return stringJsonProvider.content
    }

    def <T> JsonSetRepository<T> buildJsonSetRepository(Class<T> type, String content = '') {
        def provider = setupJsonProvider(content)
        return new JsonSetRepository<>(type, provider, DEFAULT_MAPPER)
    }

    void "should count elements"() {
        given:
        @Subject def repo = buildJsonSetRepository(Cat, content)

        expect:
        repo.count() == expectedCount

        where:
        content        || expectedCount
        '[]'           || 0
        '[{}]'         || 1
        '[{}, {}, {}]' || 3
    }

    void "should count elements that test the predicate"() {
        given:
        @Subject def repo = buildJsonSetRepository(Cat, content)

        expect:
        repo.count { Cat cat -> cat.name == searchName } == expectedCount

        where:
        searchName || expectedCount
        'Fisher'   || 0
        'Kirk'     || 1
        'Spock'    || 2

        content = '[{id: 1, name: "Kirk"}, {id: 2, name: "Spock"}, {id: 3, name: "Spock"}]'
    }

    void "should read the first element with a predicate"() {
        given:
        @Subject def repo = buildJsonSetRepository(Cat, content)

        expect:
        repo.findFirst { it.id == searchId }.orElse(null)?.id == expectedFoundId

        where:
        searchId || expectedFoundId
        0        || null
        1        || 1

        content = '[{id: 1}, {id: 2}, {id: 3}]'
    }

    void "should read all elements with a predicate"() {
        given:
        @Subject def repo = buildJsonSetRepository(Cat, content)

        expect:
        repo.findAll(predicate).collect { it.id } == expectedIdsFound

        where:
        searchName || expectedIdsFound
        'Fisher'   || []
        'Kirk'     || [1]
        'Spock'    || [2, 3]

        content = '[{id: 1, name: "Kirk"}, {id: 2, name: "Spock"}, {id: 3, name: "Spock"}]'
        predicate = { it.name == searchName } as Predicate<Cat>
    }

    void "should read with a stream"() {
        given:
        @Subject def repo = buildJsonSetRepository(Cat, content)

        when:
        def found = repo.withStream { Stream<Cat> s -> s.filter({ it.id == searchId }).findFirst() }

        then:
        found.orElse(null)?.id == expectedFoundId

        where:
        searchId || expectedFoundId
        0        || null
        1        || 1

        content = '[{id: 1}, {id: 2}, {id: 3}]'
    }

    void "should have no more element when the iterator is used outside the with block"() {
        given:
        @Subject def repo = buildJsonSetRepository(Cat, content)

        when:
        def iterator = repo.withIterator { Iterator<Cat> s -> s }

        then:
        !iterator.hasNext()

        where:
        content = '[{id: 1}, {id: 2}, {id: 3}]'
    }

    void "should have no more element when the stream is used outside the with block"() {
        given:
        @Subject def repo = buildJsonSetRepository(Cat, content)

        when:
        def stream = repo.withStream() { Stream<Cat> s -> s }

        then:
        !stream.findFirst().isPresent()

        where:
        content = '[{id: 1}, {id: 2}, {id: 3}]'
    }

    void "should save an element"() {
        given:
        @Subject def repo = buildJsonSetRepository(Cat, content)

        when:
        def result = repo.save(element)
        repo.save(null)

        then:
        result
        jsonProviderContent == expectedContent

        where:
        content                                     | expectedContent
        ''                                          | '[{id:0,name:"Spock"}]'
        '[{id: 0, name: "Kirk", grade: "Captain"}]' | '[{id:0,name:"Spock",grade:"Captain"}]'
        '[{id: 1, name: "Kirk", grade: "Captain"}]' | '[{id:1,name:"Kirk",grade:"Captain"},{id:0,name:"Spock"}]'

        element = new Cat(id: 0, name: 'Spock')
    }

    void "should save all elements"() {
        given:
        @Subject def repo = buildJsonSetRepository(Cat, content)

        when:
        def result = repo.saveAll(elements)
        repo.saveAll([null])

        then:
        result == elements.size()
        jsonProviderContent == expectedContent

        where:
        content                                       | expectedContent
        ''                                            | '[{id:0,name:"Spock"},{id:1,name:"Kirk"}]'
        '[{id: 0, name: "Kirk", grade: "Captain"}]'   | '[{id:0,name:"Spock",grade:"Captain"},{id:1,name:"Kirk"}]'
        '[{id: 2, name: "Archer", grade: "Captain"}]' | '[{id:2,name:"Archer",grade:"Captain"},{id:0,name:"Spock"},{id:1,name:"Kirk"}]'

        elements = [new Cat(id: 0, name: 'Spock'), new Cat(id: 1, name: 'Kirk')]
    }

    void "should delete an element"() {
        given:
        @Subject def repo = buildJsonSetRepository(Cat, content)

        when:
        def result = repo.delete(element)

        then:
        result == expectedResult
        jsonProviderContent == expectedContent

        where:
        content     || expectedResult | expectedContent
        ''          || false          | '[]'
        '[{id: 0}]' || true           | '[]'
        '[{id: 1}]' || false          | '[{id:1}]'

        element = new Cat(id: 0)
    }

    void "should delete a list of elements"() {
        given:
        @Subject def repo = buildJsonSetRepository(Cat, content)

        when:
        def result = repo.deleteAll(elements)

        then:
        result == expectedResult
        jsonProviderContent == expectedContent

        where:
        content              || expectedResult | expectedContent
        ''                   || 0              | '[]'
        '[{id: 0}]'          || 1              | '[]'
        '[{id: 0}, {id: 2}]' || 1              | '[{id:2}]'
        '[{id: 0}, {id: 1}]' || 2              | '[]'

        elements = [new Cat(id: 0, name: 'Spock'), new Cat(id: 1, name: 'Kirk')]
    }

    void "should delete all elements"() {
        given:
        @Subject def repo = buildJsonSetRepository(Cat, content)

        when:
        def result = repo.deleteAll()

        then:
        result == expectedResult
        jsonProviderContent == expectedContent

        where:
        content                             || expectedResult | expectedContent
        ''                                  || 0              | '[]'
        '[{name: "Kirk"}]'                  || 1              | '[]'
        '[{name: "Kirk"}, {name: "Spock"}]' || 2              | '[]'
    }

    void "should delete a list from a predicate"() {
        given:
        @Subject def repo = buildJsonSetRepository(Cat, content)

        when:
        def result = repo.deleteAll(predicate)

        then:
        result == expectedResult
        jsonProviderContent == expectedContent

        where:
        content                             || expectedResult | expectedContent
        ''                                  || 0              | '[]'
        '[{name: "Kirk"}]'                  || 1              | '[]'
        '[{name: "Kirk"}, {name: "Kirk"}]'  || 2              | '[]'
        '[{name: "Kirk"}, {name: "Spock"}]' || 1              | '[{name:"Spock"}]'

        predicate = { Cat cat -> cat.name == 'Kirk' } as Predicate<Cat>
    }
}
