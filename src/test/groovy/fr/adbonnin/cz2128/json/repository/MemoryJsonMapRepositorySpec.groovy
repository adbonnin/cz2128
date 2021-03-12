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

class MemoryJsonMapRepositorySpec extends BaseJsonProviderSpec {

    @Override
    CZ2128.JsonProviderBuilder setupJsonProvider(String content) {
        return newMemoryJsonProvider(content)
    }

    void "should count elements"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, JsonUtils.replaceUpdateStrategy())

        expect:
        repo.count() == expectedCount
        repo.isEmpty() == expectedIsEmpty

        where:
        content                 || expectedCount | expectedIsEmpty
        '{}'                    || 0             | true
        '{a: {}}'               || 1             | false
        '{a: {}, b: {}, c: {}}' || 3             | false
    }

    void "should count elements that test with a predicate on the key"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, JsonUtils.replaceUpdateStrategy())

        expect:
        repo.count { Map.Entry<String, Cat> etr -> etr.key == searchKey } == expectedCount

        where:
        searchKey || expectedCount
        'a'       || 1
        'b'       || 1
        'c'       || 1
        'd'       || 0

        content = '{a: {id: 1, name: "Kirk"}, b: {id: 2, name: "Spock"}, c: {id: 3, name: "Spock"}}'
    }

    void "should count elements that test with a predicate on the value"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, JsonUtils.replaceUpdateStrategy())

        expect:
        repo.count { Map.Entry<String, Cat> etr -> etr.value.name == searchName } == expectedCount

        where:
        searchName || expectedCount
        'Fisher'   || 0
        'Kirk'     || 1
        'Spock'    || 2

        content = '{a: {id: 1, name: "Kirk"}, b: {id: 2, name: "Spock"}, c: {id: 3, name: "Spock"}}'
    }

    void "should read the first element with a predicate on the key"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, JsonUtils.replaceUpdateStrategy())

        expect:
        repo.findFirst { it.key == searchKey }.orElse(null)?.key == expectedFoundKey

        where:
        searchKey || expectedFoundKey
        'd'       || null
        'a'       || 'a'

        content = '{a: {id: 1}, b: {id: 2}, c: {id: 3}}'
    }

    void "should read the first element with a predicate on the value"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, JsonUtils.replaceUpdateStrategy())

        expect:
        repo.findFirst { it.value.id == searchId }.orElse(null)?.value?.id == expectedFoundId

        where:
        searchId || expectedFoundId
        0        || null
        1        || 1

        content = '{a: {id: 1}, b: {id: 2}, c: {id: 3}}'
    }

    void "should read all elements"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, JsonUtils.replaceUpdateStrategy())

        when:
        def values = repo.findAll()

        then:
        values.collect { it.key } == ['a', 'b', 'c']
        values.collect { it.value.id } == [1, 2, 3]

        where:
        content = '{a: {id: 1, name: "Kirk"}, b: {id: 2, name: "Spock"}, c:{id: 3, name: "Spock"}}'
    }

    void "should read all elements with a predicate on the key"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, JsonUtils.replaceUpdateStrategy())

        expect:
        repo.findAll(predicate).collect { it.key } == expectedIdsFound

        where:
        searchKey || expectedIdsFound
        'a'       || ['a']
        'b'       || ['b']
        'c'       || ['c']
        'd'       || []

        content = '{a: {id: 1, name: "Kirk"}, b: {id: 2, name: "Spock"}, c: {id: 3, name: "Spock"}}'
        predicate = { it.key == searchKey } as Predicate<Map.Entry<String, Cat>>
    }

    void "should read all elements with a predicate on the value"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, JsonUtils.replaceUpdateStrategy())

        expect:
        repo.findAll(predicate).collect { it.value.id } == expectedIdsFound

        where:
        searchName || expectedIdsFound
        'Fisher'   || []
        'Kirk'     || [1]
        'Spock'    || [2, 3]

        content = '{a: {id: 1, name: "Kirk"}, b: {id: 2, name: "Spock"}, c: {id: 3, name: "Spock"}}'
        predicate = { it.value.name == searchName } as Predicate<Map.Entry<String, Cat>>
    }

    void "should read with a stream"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, JsonUtils.replaceUpdateStrategy())

        when:
        def found = repo.withStream { Stream<Map.Entry<String, Cat>> s -> s.filter(predicate).findFirst() }

        then:
        found.orElse(null)?.value?.id == expectedFoundId

        where:
        searchId || expectedFoundId
        0        || null
        1        || 1

        content = '{a: {id: 1}, b: {id: 2}, c: {id: 3}}'
        predicate = { it.value.id == searchId } as Predicate<Map.Entry<String, Cat>>
    }

    void "should have no more element when the iterator is used outside the with block"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, JsonUtils.replaceUpdateStrategy())

        when:
        def iterator = repo.withIterator { it }

        then:
        !iterator.hasNext()

        where:
        content = '{a: {id: 1}, b: {id: 2}, c: {id: 3}}'
    }

    void "should have no more element when the stream is used outside the with block"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, JsonUtils.replaceUpdateStrategy())

        when:
        def stream = repo.withStream() { it }

        then:
        !stream.findFirst().isPresent()

        where:
        content = '{a: {id: 1}, b: {id: 2}, c: {id: 3}}'
    }

    void "should save an element"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, updateStrategy)

        when:
        def result = repo.save(key, value)
        repo.save('z', null)

        then:
        result
        isEquals(provider, expectedContent)

        where:
        content                                                 | updateStrategy                    || expectedContent
        ''                                                      | JsonUtils.replaceUpdateStrategy() || '{a: {id: 0, name: "Spock"}, z: null}'
        '{a: {id: 0, name: "Kirk", grade: "Captain"}}'          | JsonUtils.replaceUpdateStrategy() || '{a: {id: 0, name: "Spock"}, z: null}'
        '{z: null, b: {id: 1, name: "Kirk", grade: "Captain"}}' | JsonUtils.replaceUpdateStrategy() || '{z: null, b: {id: 1, name: "Kirk", grade: "Captain"}, a: {id: 0, name: "Spock"}}'

        ''                                                      | JsonUtils.partialUpdateStrategy() || '{a: {id: 0, name: "Spock"}, z: null}'
        '{a: {id: 0, name: "Kirk", grade: "Captain"}}'          | JsonUtils.partialUpdateStrategy() || '{a: {id: 0, name: "Spock", grade: "Captain"}, z: null}'
        '{z: null, b: {id: 1, name: "Kirk", grade: "Captain"}}' | JsonUtils.partialUpdateStrategy() || '{z: null, b: {id: 1, name: "Kirk", grade: "Captain"}, a: {id: 0, name: "Spock"}}'

        key = 'a'
        value = new Cat(id: 0, name: 'Spock')
    }

    void "should save all numbers"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Integer, JsonUtils.replaceUpdateStrategy())

        when:
        def result = repo.saveAll(elements)
        repo.saveAll([z: null])

        then:
        result == elements.size()
        isEquals(provider, expectedContent)

        where:
        content                       | expectedContent
        ''                            | '{d: 4, e: 5, z: null}'
        '{z: null, a: 1, b: 2, c: 3}' | '{z: null, a: 1, b: 2, c: 3, d: 4, e: 5}'

        elements = [d: 4, e: 5]
    }

    void "should save all objects"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, updateStrategy)

        when:
        def result = repo.saveAll(elements)
        repo.saveAll([z: null])

        then:
        result == elements.size()
        isEquals(provider, expectedContent)

        where:
        content                                                   | updateStrategy                    || expectedContent
        ''                                                        | JsonUtils.replaceUpdateStrategy() || '{a: {id: 0, name: "Spock"}, b: {id: 1, name: "Kirk"}, z: null}'
        '{a: {id: 0, name: "Kirk", grade: "Captain"}}'            | JsonUtils.replaceUpdateStrategy() || '{a: {id: 0, name: "Spock"}, b: {id: 1, name: "Kirk"}, z: null}'
        '{z: null, c: {id: 2, name: "Archer", grade: "Captain"}}' | JsonUtils.replaceUpdateStrategy() || '{z: null, c: {id: 2, name: "Archer", grade: "Captain"}, a: {id: 0, name: "Spock"}, b: {id: 1, name: "Kirk"}}'

        ''                                                        | JsonUtils.partialUpdateStrategy() || '{a: {id: 0, name: "Spock"}, b: {id: 1, name: "Kirk"}, z: null}'
        '{a: {id: 0, name: "Kirk", grade: "Captain"}}'            | JsonUtils.partialUpdateStrategy() || '{a: {id: 0, name: "Spock", grade: "Captain"}, b: {id: 1, name: "Kirk"}, z: null}'
        '{z: null, c: {id: 2, name: "Archer", grade: "Captain"}}' | JsonUtils.partialUpdateStrategy() || '{z: null, c: {id: 2, name: "Archer", grade: "Captain"}, a: {id: 0, name: "Spock"}, b: {id: 1, name: "Kirk"}}'

        elements = [a: new Cat(id: 0, name: 'Spock'), b: new Cat(id: 1, name: 'Kirk')]
    }

    void "should save all arrays"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(SpaceCat, updateStrategy)

        when:
        def result = repo.saveAll(elements)
        repo.saveAll([z: null])

        then:
        result == elements.size()
        isEquals(provider, expectedContent)

        where:
        content                                | updateStrategy                    || expectedContent
        ''                                     | JsonUtils.replaceUpdateStrategy() || '{a: [0, "Spock"], b: [1, "Kirk"], z: null}'
        '{z: null, a: [0, "Kirk", "Captain"]}' | JsonUtils.replaceUpdateStrategy() || '{z: null, a: [0, "Spock"], b: [1, "Kirk"]}'

        ''                                     | JsonUtils.partialUpdateStrategy() || '{a: [0, "Spock"], b: [1, "Kirk"], z: null}'
        '{z: null, a: [0, "Kirk", "Captain"]}' | JsonUtils.partialUpdateStrategy() || '{z: null, a: [0, "Spock", "Captain"], b: [1, "Kirk"]}'

        elements = [a: new SpaceCat(id: 0, name: 'Spock'), b: new SpaceCat(id: 1, name: 'Kirk')]
    }

    void "should delete an element"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, JsonUtils.replaceUpdateStrategy())

        when:
        def result = repo.delete(elementKey)

        then:
        result == expectedResult
        isEquals(provider, expectedContent)

        where:
        content                 || expectedResult | expectedContent
        ''                      || false          | '{}'
        '{z: null, a: {id: 0}}' || true           | '{z: null}'
        '{z: null, b: {id: 1}}' || false          | '{z: null, b: {id:1}}'

        elementKey = 'a'
    }

    void "should delete a list of elements"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, JsonUtils.replaceUpdateStrategy())

        when:
        def result = repo.deleteAll(elements)

        then:
        result == expectedResult
        isEquals(provider, expectedContent)

        where:
        content                             || expectedResult | expectedContent
        ''                                  || 0              | '{}'
        '{z: null, a: {id: 0}}'             || 2              | '{}'
        '{z: null, a: {id: 0}, b: {id: 1}}' || 3              | '{}'
        '{z: null, a: {id: 0}, c: {id: 2}}' || 2              | '{c: {id: 2}}'

        elements = ['z', 'a', 'b']
    }

    void "should delete all elements"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, JsonUtils.replaceUpdateStrategy())

        when:
        def result = repo.deleteAll()

        then:
        result == expectedResult
        isEquals(provider, expectedContent)

        where:
        content                                            || expectedResult | expectedContent
        ''                                                 || 0              | '{}'
        '{z: null, a: {name: "Kirk"}}'                     || 2              | '{}'
        '{z: null, a: {name: "Kirk"}, b: {name: "Spock"}}' || 3              | '{}'
    }

    void "should delete a list from a predicate"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.mapRepository(Cat, JsonUtils.replaceUpdateStrategy())

        when:
        def result = repo.deleteAll(predicate)

        then:
        result == expectedResult
        isEquals(provider, expectedContent)

        where:
        content                                            || expectedResult | expectedContent
        ''                                                 || 0              | '{}'
        '{z: null, a: {name: "Kirk"}}'                     || 1              | '{z: null}'
        '{z: null, a: {name: "Kirk"}, b: {name: "Kirk"}}'  || 2              | '{z: null}'
        '{z: null, a: {name: "Kirk"}, c: {name: "Spock"}}' || 1              | '{z: null, c: {name: "Spock"}}'

        predicate = { Map.Entry<String, Cat> etr -> etr?.value?.name == 'Kirk' } as Predicate<Map.Entry<String, Cat>>
    }

    void "should keep all fields with different type"() {
        given:
        def provider = setupJsonProvider(content)
        def catRepo = provider.mapRepository(Cat, JsonUtils.partialUpdateStrategy())
        @Subject ponyRepo = repoBuilder(catRepo)

        when:
        ponyRepo.save('a', spockPony)

        then:
        isEquals(provider, expectedContent)

        where:
        repoBuilder                                             | _
        ({ value -> value.of(Pony) })                           | _
        ({ value -> value.of(Pony.TYPE_REF) })                  | _
        ({ value -> value.of(DEFAULT_MAPPER.readerFor(Pony)) }) | _

        content = '{a: {id: 0, name: "Spock"}, b: {id: 1, name: "Kirk"}}'
        spockPony = new Pony(name: 'Spock', color: 'blue')

        expectedContent = '{a: {id: 0, name: "Spock", color: "blue"}, b: {id: 1, name: "Kirk"}}'
    }
}
