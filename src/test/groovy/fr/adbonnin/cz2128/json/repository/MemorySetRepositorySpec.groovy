package fr.adbonnin.cz2128.json.repository

import fr.adbonnin.cz2128.collect.IteratorUtils
import fr.adbonnin.cz2128.fixture.BaseJsonProviderSpec
import fr.adbonnin.cz2128.fixture.Cat
import fr.adbonnin.cz2128.fixture.Pony
import fr.adbonnin.cz2128.fixture.SpaceCat
import fr.adbonnin.cz2128.json.Json
import spock.lang.Subject

import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream

class MemorySetRepositorySpec extends BaseJsonProviderSpec {

    @Override
    Json.ProviderFactory setupProviderFactory(String content) {
        return newMemoryProviderFactory(content)
    }

    void "should count elements"() {
        given:
        def provider = setupProviderFactory(content)
        @Subject def repo = provider.node().setRepository(Cat)

        expect:
        repo.count() == expectedCount
        repo.isEmpty() == expectedIsEmpty

        where:
        content        || expectedCount | expectedIsEmpty
        ''             || 0             | true
        '[]'           || 0             | true
        '[{}]'         || 1             | false
        '[{}, {}, {}]' || 3             | false
    }

    void "should count elements that test with a predicate"() {
        given:
        def provider = setupProviderFactory(content)
        @Subject def repo = provider.node().setRepository(Cat)

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
        def provider = setupProviderFactory(content)
        @Subject def repo = provider.node().setRepository(Cat)

        expect:
        repo.findFirst { it.id == searchId }.orElse(null)?.id == expectedFoundId

        where:
        searchId || expectedFoundId
        0        || null
        1        || 1

        content = '[{id: 1}, {id: 2}, {id: 3}]'
    }

    void "should read all elements"() {
        given:
        def provider = setupProviderFactory(content)
        @Subject def repo = provider.node().setRepository(Cat)

        expect:
        repo.findAll() == new LinkedHashSet(expectedResult)
        repo.withStream(streamToList()) == expectedResult
        repo.withIterator(iteratorToList()) == expectedResult

        where:
        content                       || expectedResult
        ''                            || []
        '[]'                          || []
        '[{id: 1}, {id: 2}, {id: 3}]' || [new Cat(id: 1), new Cat(id: 2), new Cat(id: 3)]
    }

    void "should read all elements with a predicate"() {
        given:
        def provider = setupProviderFactory(content)
        @Subject def repo = provider.node().setRepository(Cat)

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
        def provider = setupProviderFactory(content)
        @Subject def repo = provider.node().setRepository(Cat)

        when:
        def result = repo.withStream { Stream<Cat> s -> s.collect(Collectors.toList()) }

        then:
        result == [new Cat(id: 1), new Cat(id: 2), new Cat(id: 3)]

        where:
        content = '[{id: 1}, {id: 2}, {id: 3}]'
    }

    void "should read with an iterator"() {
        given:
        def provider = setupProviderFactory(content)
        @Subject def repo = provider.node().setRepository(Cat)

        when:
        def result = repo.withIterator { IteratorUtils.newArrayList(it) }

        then:
        result == [new Cat(id: 1), new Cat(id: 2), new Cat(id: 3)]

        where:
        content = '[{id: 1}, {id: 2}, {id: 3}]'
    }

    void "should have no more element when the iterator is used outside the with block"() {
        given:
        def provider = setupProviderFactory(content)
        @Subject def repo = provider.node().setRepository(Cat)

        when:
        def iterator = repo.withIterator { it }

        then:
        !iterator.hasNext()

        where:
        content = '[{id: 1}, {id: 2}, {id: 3}]'
    }

    void "should have no more element when the stream is used outside the with block"() {
        given:
        def provider = setupProviderFactory(content)
        @Subject def repo = provider.node().setRepository(Cat)

        when:
        def stream = repo.withStream() { it }

        then:
        !stream.findFirst().isPresent()

        where:
        content = '[{id: 1}, {id: 2}, {id: 3}]'
    }

    void "should save an element"() {
        given:
        def provider = setupProviderFactory(content)
        @Subject def repo = repository.apply(provider).setRepository(Cat)

        when:
        def result = repo.save(element)
        repo.save(null)

        then:
        result
        isEquals(provider, expectedContent)

        where:
        repository | content                                         || expectedContent
        value      | ''                                              || '[{id: 0, name: "Spock"}, null]'
        value      | '[{id: 0, name: "Kirk", grade: "Captain"}]'     || '[{id: 0, name: "Spock"}, null]'
        value      | '[null, {id: 1, name: "Kirk", other: "value"}]' || '[null, {id: 1, name: "Kirk"}, {id: 0, name: "Spock"}]'

        node       | ''                                              || '[{id: 0, name: "Spock"}, null]'
        node       | '[{id: 0, name: "Kirk", grade: "Captain"}]'     || '[{id: 0, name: "Spock", grade: "Captain"}, null]'
        node       | '[null, {id: 1, name: "Kirk", other: "value"}]' || '[null, {id: 1, name: "Kirk", other: "value"}, {id: 0, name: "Spock"}]'

        element = new Cat(id: 0, name: 'Spock')
    }

    void "should save all numbers"() {
        given:
        def provider = setupProviderFactory(content)
        @Subject def repo = repository.apply(provider).setRepository(Integer)

        when:
        def result = repo.saveAll(elements)
        def nullResult = repo.save(null)

        then:
        result == elements.size()
        nullResult == expectedNullResult
        isEquals(provider, expectedContent)

        where:
        repository | content           || expectedContent         | expectedNullResult
        value      | ''                || '[4, 5, null]'          | true
        value      | '[null, 1, 2, 3]' || '[null, 1, 2, 3, 4, 5]' | true

        node       | ''                || '[4, 5, null]'          | true
        node       | '[null, 1, 2, 3]' || '[null, 1, 2, 3, 4, 5]' | false

        elements = [4, 5] as Integer[]
    }

    void "should save all objects"() {
        given:
        def provider = setupProviderFactory(content)
        @Subject def repo = repository.apply(provider).setRepository(Cat)

        when:
        def result = repo.saveAll(elements)
        repo.saveAll([null])

        then:
        result == elements.size()
        isEquals(provider, expectedContent)

        where:
        repository | content                                           || expectedContent
        value      | ''                                                || '[{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}, null]'
        value      | '[{id: 0, name: "Kirk", grade: "Captain"}]'       || '[{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}, null]'
        value      | '[null, {id: 2, name: "Archer", other: "value"}]' || '[null, {id: 2, name: "Archer"}, {id: 0, name: "Spock"}, {id: 1, name: "Kirk"}]'

        node       | ''                                                || '[{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}, null]'
        node       | '[{id: 0, name: "Kirk", grade: "Captain"}]'       || '[{id: 0, name: "Spock",  grade: "Captain"}, {id: 1, name: "Kirk"}, null]'
        node       | '[null, {id: 2, name: "Archer", other: "value"}]' || '[null, {id: 2, name: "Archer", other: "value"}, {id: 0, name: "Spock"}, {id: 1, name: "Kirk"}]'

        elements = [new Cat(id: 0, name: 'Spock'), new Cat(id: 1, name: 'Kirk')]
    }

    void "should save all arrays"() {
        given:
        def provider = setupProviderFactory(content)
        @Subject def repo = repository.apply(provider).setRepository(SpaceCat)

        when:
        def result = repo.saveAll(elements)
        repo.saveAll([null])

        then:
        result == elements.size()
        isEquals(provider, expectedContent)

        where:
        repository | content                          || expectedContent
        value      | ''                               || '[[0, "Spock"], [1, "Kirk"], null]'
        value      | '[null, [0, "Kirk", "Captain"]]' || '[null, [0, "Spock"], [1, "Kirk"]]'

        node       | ''                               || '[[0, "Spock"], [1, "Kirk"], null]'
        node       | '[null, [0, "Kirk", "Captain"]]' || '[null, [0, "Spock", "Captain"], [1, "Kirk"]]'

        elements = [new SpaceCat(id: 0, name: 'Spock'), new SpaceCat(id: 1, name: 'Kirk')]
    }

    void "should delete an element"() {
        given:
        def provider = setupProviderFactory(content)
        @Subject def repo = repository.apply(provider).setRepository(Cat)

        when:
        def result = repo.delete(element)

        then:
        result == expectedResult
        isEquals(provider, expectedContent)

        where:
        repository | content                                         || expectedResult | expectedContent
        value      | ''                                              || false          | '[]'
        value      | '[null, {id: 0}]'                               || true           | '[null]'
        value      | '[null, {id: 1, name: "Kirk", other: "value"}]' || false          | '[null, {id: 1, name: "Kirk"}]'

        node       | ''                                              || false          | '[]'
        node       | '[null, {id: 0}]'                               || true           | '[null]'
        node       | '[null, {id: 1, name: "Kirk", other: "value"}]' || false          | '[null, {id: 1, name: "Kirk", other: "value"}]'

        element = new Cat(id: 0)
    }

    void "should delete a list of elements"() {
        given:
        def provider = setupProviderFactory(content)
        @Subject def repo = repository.apply(provider).setRepository(Cat)

        when:
        def result = repo.deleteAll(elements)

        then:
        result == expectedResult
        isEquals(provider, expectedContent)

        where:
        repository | content                                                  || expectedResult | expectedContent
        value      | ''                                                       || 0              | '[]'
        value      | '[null, {id: 0}]'                                        || 2              | '[]'
        value      | '[null, {id: 0}, {id: 1}]'                               || 3              | '[]'
        value      | '[null, {id: 0}, {id: 2, name: "Kirk", other: "value"}]' || 2              | '[{id: 2, name: "Kirk"}]'

        node       | ''                                                       || 0              | '[]'
        node       | '[null, {id: 0}]'                                        || 2              | '[]'
        node       | '[null, {id: 0}, {id: 1}]'                               || 3              | '[]'
        node       | '[null, {id: 0}, {id: 2, name: "Kirk", other: "value"}]' || 2              | '[{id: 2, name: "Kirk", other: "value"}]'

        elements = [null, new Cat(id: 0, name: 'Spock'), new Cat(id: 1, name: 'Kirk')]
    }

    void "should delete all elements"() {
        given:
        def provider = setupProviderFactory(content)
        @Subject def repo = repository.apply(provider).setRepository(Cat)

        when:
        def result = repo.deleteAll()

        then:
        result == expectedResult
        isEquals(provider, expectedContent)

        where:
        repository | content                                   || expectedResult | expectedContent
        value      | ''                                        || 0              | '[]'
        value      | '[null, {name: "Kirk"}]'                  || 2              | '[]'
        value      | '[null, {name: "Kirk"}, {name: "Spock"}]' || 3              | '[]'

        node       | ''                                        || 0              | '[]'
        node       | '[null, {name: "Kirk"}]'                  || 2              | '[]'
        node       | '[null, {name: "Kirk"}, {name: "Spock"}]' || 3              | '[]'
    }

    void "should delete a list from a predicate"() {
        given:
        def provider = setupProviderFactory(content)
        @Subject def repo = repository.apply(provider).setRepository(Cat)

        when:
        def result = repo.deleteAll(predicate)

        then:
        result == expectedResult
        isEquals(provider, expectedContent)

        where:
        repository | content                                                   || expectedResult | expectedContent
        value      | ''                                                        || 0              | '[]'
        value      | '[null, {name: "Kirk"}]'                                  || 1              | '[null]'
        value      | '[null, {name: "Kirk"}, {name: "Kirk"}]'                  || 2              | '[null]'
        value      | '[null, {name: "Kirk"}, {name: "Spock"}]'                 || 1              | '[null, {id: null, name: "Spock"}]'

        node       | ''                                                        || 0              | '[]'
        node       | '[null, {name: "Kirk"}]'                                  || 1              | '[null]'
        node       | '[null, {name: "Kirk"}, {name: "Kirk"}]'                  || 2              | '[null]'
        node       | '[null, {name: "Kirk"}, {name: "Spock", other: "value"}]' || 1              | '[null, {name: "Spock", other: "value"}]'

        predicate = { Cat cat -> cat?.name == 'Kirk' } as Predicate<Cat>
    }

    void "should save with a different type"() {
        given:
        def provider = setupProviderFactory(content)
        def catRepo = repository.apply(provider).setRepository(Cat)
        @Subject ponyRepo = repoBuilder(catRepo)

        when:
        ponyRepo.save(spockPony)

        then:
        isEquals(provider, expectedContent)

        where:
        repository | repoBuilder                                             || expectedContent
        value      | ({ value -> value.of(Pony) })                           || '[{name: "Spock", color: "blue"}, {name: "Kirk", color: null}]'
        value      | ({ value -> value.of(Pony.TYPE_REF) })                  || '[{name: "Spock", color: "blue"}, {name: "Kirk", color: null}]'
        value      | ({ value -> value.of(DEFAULT_MAPPER.readerFor(Pony)) }) || '[{name: "Spock", color: "blue"}, {name: "Kirk", color: null}]'

        node       | ({ value -> value.of(Pony) })                           || '[{id: 0, name: "Spock", color: "blue"}, {id: 1, name: "Kirk"}]'
        node       | ({ value -> value.of(Pony.TYPE_REF) })                  || '[{id: 0, name: "Spock", color: "blue"}, {id: 1, name: "Kirk"}]'
        node       | ({ value -> value.of(DEFAULT_MAPPER.readerFor(Pony)) }) || '[{id: 0, name: "Spock", color: "blue"}, {id: 1, name: "Kirk"}]'

        content = '[{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}]'
        spockPony = new Pony(name: 'Spock', color: 'blue')
    }
}
