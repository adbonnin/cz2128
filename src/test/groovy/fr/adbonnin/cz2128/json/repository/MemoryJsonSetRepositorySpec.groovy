package fr.adbonnin.cz2128.json.repository

import fr.adbonnin.cz2128.CZ2128
import fr.adbonnin.cz2128.fixture.BaseJsonProviderSpec
import fr.adbonnin.cz2128.fixture.Cat
import fr.adbonnin.cz2128.fixture.Pony
import fr.adbonnin.cz2128.fixture.SpaceCat
import fr.adbonnin.cz2128.json.JsonUtils
import spock.lang.Subject

import java.util.function.Predicate
import java.util.stream.Stream

class MemoryJsonSetRepositorySpec extends BaseJsonProviderSpec {

    @Override
    CZ2128.JsonProviderBuilder setupJsonProvider(String content) {
        return newMemoryJsonProvider(content)
    }

    void "should count elements"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(Cat, JsonUtils.replaceUpdateStrategy())

        expect:
        repo.count() == expectedCount
        repo.isEmpty() == expectedIsEmpty

        where:
        content        || expectedCount | expectedIsEmpty
        '[]'           || 0             | true
        '[{}]'         || 1             | false
        '[{}, {}, {}]' || 3             | false
    }

    void "should count elements that test with a predicate"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(Cat, JsonUtils.replaceUpdateStrategy())

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
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(Cat, JsonUtils.replaceUpdateStrategy())

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
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(Cat, JsonUtils.replaceUpdateStrategy())

        expect:
        repo.findAll().collect { it.id } == [1, 2, 3]

        where:
        content = '[{id: 1, name: "Kirk"}, {id: 2, name: "Spock"}, {id: 3, name: "Spock"}]'
    }

    void "should read all elements with a predicate"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(Cat, JsonUtils.replaceUpdateStrategy())

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
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(Cat, JsonUtils.replaceUpdateStrategy())

        when:
        def found = repo.withStream { Stream<Cat> s -> s.filter(predicate).findFirst() }

        then:
        found.orElse(null)?.id == expectedFoundId

        where:
        searchId || expectedFoundId
        0        || null
        1        || 1

        content = '[{id: 1}, {id: 2}, {id: 3}]'
        predicate = { it.id == searchId } as Predicate<Cat>
    }

    void "should have no more element when the iterator is used outside the with block"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(Cat, JsonUtils.replaceUpdateStrategy())

        when:
        def iterator = repo.withIterator { it }

        then:
        !iterator.hasNext()

        where:
        content = '[{id: 1}, {id: 2}, {id: 3}]'
    }

    void "should have no more element when the stream is used outside the with block"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(Cat, JsonUtils.replaceUpdateStrategy())

        when:
        def stream = repo.withStream() { it }

        then:
        !stream.findFirst().isPresent()

        where:
        content = '[{id: 1}, {id: 2}, {id: 3}]'
    }

    void "should save an element"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(Cat, updateStrategy)

        when:
        def result = repo.save(element)
        repo.save(null)

        then:
        result
        isEquals(provider, expectedContent)

        where:
        content                                           | updateStrategy                    || expectedContent
        ''                                                | JsonUtils.replaceUpdateStrategy() || '[{id: 0, name: "Spock"}, null]'
        '[{id: 0, name: "Kirk", grade: "Captain"}]'       | JsonUtils.replaceUpdateStrategy() || '[{id: 0, name: "Spock"}, null]'
        '[null, {id: 1, name: "Kirk", grade: "Captain"}]' | JsonUtils.replaceUpdateStrategy() || '[null, {id: 1, name: "Kirk", grade: "Captain"}, {id: 0, name: "Spock"}]'

        ''                                                | JsonUtils.partialUpdateStrategy() || '[{id: 0, name: "Spock"}, null]'
        '[{id: 0, name: "Kirk", grade: "Captain"}]'       | JsonUtils.partialUpdateStrategy() || '[{id: 0, name: "Spock", grade: "Captain"}, null]'
        '[null, {id: 1, name: "Kirk", grade: "Captain"}]' | JsonUtils.partialUpdateStrategy() || '[null, {id: 1, name: "Kirk", grade: "Captain"}, {id: 0, name: "Spock"}]'

        element = new Cat(id: 0, name: 'Spock')
    }

    void "should save all numbers"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(Integer, JsonUtils.replaceUpdateStrategy())

        when:
        def result = repo.saveAll(elements)
        repo.saveAll([null] as Integer[])

        then:
        result == elements.size()
        isEquals(provider, expectedContent)

        where:
        content           | expectedContent
        ''                | '[4, 5, null]'
        '[null, 1, 2, 3]' | '[null, 1, 2, 3, 4, 5]'

        elements = [4, 5]
    }

    void "should save all objects"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(Cat, updateStrategy)

        when:
        def result = repo.saveAll(elements)
        repo.saveAll([null])

        then:
        result == elements.size()
        isEquals(provider, expectedContent)

        where:
        content                                             | updateStrategy                    || expectedContent
        ''                                                  | JsonUtils.replaceUpdateStrategy() || '[{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}, null]'
        '[{id: 0, name: "Kirk", grade: "Captain"}]'         | JsonUtils.replaceUpdateStrategy() || '[{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}, null]'
        '[null, {id: 2, name: "Archer", grade: "Captain"}]' | JsonUtils.replaceUpdateStrategy() || '[null, {id: 2, name: "Archer", grade: "Captain"}, {id: 0, name: "Spock"}, {id: 1, name: "Kirk"}]'

        ''                                                  | JsonUtils.partialUpdateStrategy() || '[{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}, null]'
        '[{id: 0, name: "Kirk", grade: "Captain"}]'         | JsonUtils.partialUpdateStrategy() || '[{id: 0, name: "Spock",  grade: "Captain"}, {id: 1, name: "Kirk"}, null]'
        '[null, {id: 2, name: "Archer", grade: "Captain"}]' | JsonUtils.partialUpdateStrategy() || '[null, {id: 2, name: "Archer", grade: "Captain"}, {id: 0, name: "Spock"}, {id: 1, name: "Kirk"}]'

        elements = [new Cat(id: 0, name: 'Spock'), new Cat(id: 1, name: 'Kirk')]
    }

    void "should save all arrays"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(SpaceCat, updateStrategy)

        when:
        def result = repo.saveAll(elements)
        repo.saveAll([null])

        then:
        result == elements.size()
        isEquals(provider, expectedContent)

        where:
        content                          | updateStrategy                    || expectedContent
        ''                               | JsonUtils.replaceUpdateStrategy() || '[[0, "Spock"], [1, "Kirk"], null]'
        '[null, [0, "Kirk", "Captain"]]' | JsonUtils.replaceUpdateStrategy() || '[null, [0, "Spock"], [1, "Kirk"]]'

        ''                               | JsonUtils.partialUpdateStrategy() || '[[0, "Spock"], [1, "Kirk"], null]'
        '[null, [0, "Kirk", "Captain"]]' | JsonUtils.partialUpdateStrategy() || '[null, [0, "Spock", "Captain"], [1, "Kirk"]]'

        elements = [new SpaceCat(id: 0, name: 'Spock'), new SpaceCat(id: 1, name: 'Kirk')]
    }

    void "should delete an element"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(Cat, JsonUtils.replaceUpdateStrategy())

        when:
        def result = repo.delete(element)

        then:
        result == expectedResult
        isEquals(provider, expectedContent)

        where:
        content           || expectedResult | expectedContent
        ''                || false          | '[]'
        '[null, {id: 0}]' || true           | '[null]'
        '[null, {id: 1}]' || false          | '[null, {id:1}]'

        element = new Cat(id: 0)
    }

    void "should delete a list of elements"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(Cat, JsonUtils.replaceUpdateStrategy())

        when:
        def result = repo.deleteAll(elements)

        then:
        result == expectedResult
        isEquals(provider, expectedContent)

        where:
        content                    || expectedResult | expectedContent
        ''                         || 0              | '[]'
        '[null, {id: 0}]'          || 2              | '[]'
        '[null, {id: 0}, {id: 1}]' || 3              | '[]'
        '[null, {id: 0}, {id: 2}]' || 2              | '[{id: 2}]'

        elements = [null, new Cat(id: 0, name: 'Spock'), new Cat(id: 1, name: 'Kirk')]
    }

    void "should delete all elements"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(Cat, JsonUtils.replaceUpdateStrategy())

        when:
        def result = repo.deleteAll()

        then:
        result == expectedResult
        isEquals(provider, expectedContent)

        where:
        content                                   || expectedResult | expectedContent
        ''                                        || 0              | '[]'
        '[null, {name: "Kirk"}]'                  || 2              | '[]'
        '[null, {name: "Kirk"}, {name: "Spock"}]' || 3              | '[]'
    }

    void "should delete a list from a predicate"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.setRepository(Cat, JsonUtils.replaceUpdateStrategy())

        when:
        def result = repo.deleteAll(predicate)

        then:
        result == expectedResult
        isEquals(provider, expectedContent)

        where:
        content                                   || expectedResult | expectedContent
        ''                                        || 0              | '[]'
        '[null, {name: "Kirk"}]'                  || 1              | '[null]'
        '[null, {name: "Kirk"}, {name: "Kirk"}]'  || 2              | '[null]'
        '[null, {name: "Kirk"}, {name: "Spock"}]' || 1              | '[null, {name: "Spock"}]'

        predicate = { Cat cat -> cat?.name == 'Kirk' } as Predicate<Cat>
    }

    void "should keep all fields with different type"() {
        given:
        def provider = setupJsonProvider(content)
        def catRepo = provider.setRepository(Cat, JsonUtils.partialUpdateStrategy())
        @Subject ponyRepo = repoBuilder(catRepo)

        when:
        ponyRepo.save(spockPony)

        then:
        isEquals(provider, expectedContent)

        where:
        repoBuilder                                             | _
        ({ value -> value.of(Pony) })                           | _
        ({ value -> value.of(Pony.TYPE_REF) })                  | _
        ({ value -> value.of(DEFAULT_MAPPER.readerFor(Pony)) }) | _

        content = '[{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}]'
        spockPony = new Pony(name: 'Spock', color: 'blue')

        expectedContent = '[{id: 0, name: "Spock", color: "blue"}, {id: 1, name: "Kirk"}]'
    }
}
