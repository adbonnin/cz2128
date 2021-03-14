package fr.adbonnin.cz2128.json.provider

import fr.adbonnin.cz2128.json.Json
import fr.adbonnin.cz2128.fixture.BaseJsonProviderSpec
import fr.adbonnin.cz2128.fixture.Cat

import static com.fasterxml.jackson.core.JsonToken.*

class MemoryProviderSpec extends BaseJsonProviderSpec {

    @Override
    Json.Provider setupJsonProvider(String content) {
        return newMemoryJsonProvider(content)
    }

    void "should parse elements"() {
        given:
        def provider = setupJsonProvider(content)

        when:
        def tokens = []
        def parsed = provider.withParser({ parser ->
            def token
            while ((token = parser.nextToken()) != null) {
                tokens.add(token)
            }
            return result
        })

        then:
        parsed == result
        tokens == expectedTokens

        where:
        content = '{id: 1}'
        expectedTokens = [START_OBJECT, FIELD_NAME, VALUE_NUMBER_INT, END_OBJECT]
        result = 42
    }

    void "should generate elements"() {
        given:
        def mapper = DEFAULT_MAPPER
        def provider = setupJsonProvider(content)

        when:
        def tokens = []
        def parsed = provider.withGenerator({ parser, generator ->

            def token
            while ((token = parser.nextToken()) != null) {
                tokens.add(token)
            }

            mapper.writeValue(generator, value)
            return result
        })

        then:
        parsed == result
        tokens == expectedTokens
        isEquals(provider, expectedContent)

        where:
        content = '{id: 1}'
        expectedTokens = [START_OBJECT, FIELD_NAME, VALUE_NUMBER_INT, END_OBJECT]

        value = new Cat(id: 1, name: 'Spock')
        expectedContent = '{id:1, name:"Spock"}'

        result = 42
    }

    void "should not save content when exception is raised"() {
        given:
        def mapper = DEFAULT_MAPPER
        def provider = setupJsonProvider(content)

        when:
        provider.withGenerator({ parser, generator ->
            mapper.writeValue(generator, value)
            throw new IllegalArgumentException(errorMessage)
        })

        then:
        def e = thrown(IllegalArgumentException)
        e.message == errorMessage
        isEquals(provider, content)

        where:
        content = '{}'
        errorMessage = "Error"
        value = new Cat(id: 1, name: 'Spock')
    }

    void "should read and write json node"() {
        given:
        def provider = setupJsonProvider("")

        when:
        def node = provider.readJsonNode()

        then:
        node == null

        when:
        def newNode = readObjectNode(content)
        provider.writeJsonNode(newNode)

        then:
        newNode == provider.readJsonNode()

        where:
        content = '{id:1, name:"Spock"}'
    }
}
