package fr.adbonnin.cz2128.json.repository

import fr.adbonnin.cz2128.collect.ListUtils
import fr.adbonnin.cz2128.fixture.BaseJsonProviderSpec
import fr.adbonnin.cz2128.fixture.Cat
import fr.adbonnin.cz2128.json.Json
import spock.lang.Subject

import java.util.stream.Collectors
import java.util.stream.Stream

class MemoryElementRepositorySpec extends BaseJsonProviderSpec {

    @Override
    Json.Provider setupJsonProvider(String content) {
        return newMemoryJsonProvider(content)
    }

    void "should read the element"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.node().elementRepository(Cat)

        expect:
        repo.isEmpty() == expectedIsEmpty
        repo.isPresent() == !expectedIsEmpty

        and:
        repo.get().orElse(null) == expectedValue

        and:
        repo.withStream { Stream<Cat> stream -> stream.collect(Collectors.toList()) } == expectedValues
        repo.withIterator { ListUtils.newArrayList(it) } == expectedValues

        where:
        content   || expectedIsEmpty | expectedValue  | expectedValues
        ''        || true            | null           | []
        'null'    || true            | null           | []
        '{id: 1}' || false           | new Cat(id: 1) | [new Cat(id: 1)]
    }

    void "should save an element"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = factory.apply(provider).elementRepository(Cat)

        when:
        def result = repo.save(cat)

        then:
        result
        isEquals(provider, expectedContent)

        where:
        factory | content                                   || expectedContent
        value   | ''                                        || '{id: 0, name: "Spock"}'
        value   | '{id: 0, name: "Spock"}'                  || '{id: 0, name: "Spock"}'
        value   | '{id: 1, name: "Kirk", grade: "Captain"}' || '{id: 0, name: "Spock"}'

        node    | ''                                        || '{id: 0, name: "Spock"}'
        node    | '{id: 0, name: "Spock"}'                  || '{id: 0, name: "Spock"}'
        node    | '{id: 1, name: "Kirk", grade: "Captain"}' || '{id: 0, name: "Spock", grade: "Captain"}'

        cat = new Cat(id: 0, name: 'Spock')
    }

    void "should save a number"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = factory.apply(provider).elementRepository(Integer)

        when:
        def result = repo.save(element)

        then:
        result == expectedResult
        isEquals(provider, expectedContent)

        where:
        factory | content || expectedContent | expectedResult
        value   | ''      || '42'            | true
        value   | '1'     || '42'            | true
        value   | '42'    || '42'            | true

        node    | ''      || '42'            | true
        node    | '1'     || '42'            | true
        node    | '42'    || '42'            | false

        element = 42
    }

    void "should delete the element"() {
        given:
        def provider = setupJsonProvider(content)
        @Subject def repo = provider.node().elementRepository(Integer)

        when:
        repo.delete()

        then:
        isEquals(provider, 'null')

        where:
        content | _
        ''      | _
        'null'  | _
        '1'     | _
    }
}
