package fr.adbonnin.cz2128.json.provider

import fr.adbonnin.cz2128.JsonProvider
import fr.adbonnin.cz2128.fixture.BaseJsonProviderSpec
import fr.adbonnin.cz2128.fixture.Cat

import static com.fasterxml.jackson.core.JsonToken.*

class MemoryJsonProviderSpec extends BaseJsonProviderSpec {

    @Override
    JsonProvider setupJsonProvider(String content) {
        def stringJsonProvider = new MemoryJsonProvider()
        stringJsonProvider.content = content
        return stringJsonProvider
    }

    void "should parse elements"() {
        given:
        def provider = setupJsonProvider(content)

        when:
        def tokens = []
        def parsed = provider.withParser(DEFAULT_MAPPER, { parser ->
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
        def provider = setupJsonProvider(content)

        when:
        def tokens = []
        def parsed = provider.withGenerator(DEFAULT_MAPPER, { parser, generator ->
            def token
            while ((token = parser.nextToken()) != null) {
                tokens.add(token)
            }

            DEFAULT_MAPPER.writeValue(generator, value)
            return result
        })

        then:
        parsed == result
        tokens == expectedTokens
        provider.content == expectedContent

        where:
        content = '{id: 1}'
        expectedTokens = [START_OBJECT, FIELD_NAME, VALUE_NUMBER_INT, END_OBJECT]

        value = new Cat(id: 1, name: 'Spock')
        expectedContent = '{id:1,name:"Spock"}'

        result = 42
    }

    void "should not save content when exception is raised"() {
        given:
        def provider = setupJsonProvider(content)

        when:
        provider.withGenerator(DEFAULT_MAPPER, { parser, generator ->
            DEFAULT_MAPPER.writeValue(generator, value)
            throw new IllegalArgumentException(errorMessage)
        })

        then:
        def e = thrown(IllegalArgumentException)
        e.message == errorMessage
        provider.content == content

        where:
        content = '{}'
        errorMessage = "Error"
        value = new Cat(id: 1, name: 'Spock')
    }
}
