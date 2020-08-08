package fr.adbonnin.cz2128.json

import fr.adbonnin.cz2128.fixture.BaseJsonSpec
import spock.lang.Subject
import spock.lang.Unroll

class JsonUtilsSpec extends BaseJsonSpec {

    @Unroll
    void "should update array"() {
        given:
        def out = new ByteArrayOutputStream()
        @Subject def generator = DEFAULT_MAPPER.getFactory().createGenerator(out)

        when:
        def modified = JsonUtils.partialUpdate().update(oldNode, newNode, generator)
        generator.close()

        then:
        modified == expectedModified
        readArrayNode(out.toString()) == expectedNode

        cleanup:
        out.close()

        where:
        oldJson             | newJson   || expectedModified | expectedJson
        null                | null      || false            | ''
        '[]'                | null      || false            | '[]'
        '["old"]'           | null      || false            | '["old"]'

        null                | '[]'      || true             | '[]'
        '[]'                | '[]'      || false            | '[]'
        '["old"]'           | '[]'      || false            | '["old"]'

        null                | '["new"]' || true             | '["new"]'
        '[]'                | '["new"]' || true             | '["new"]'
        '["old"]'           | '["new"]' || true             | '["new"]'

        '["updated","old"]' | '["new"]' || true             | '["new","old"]'

        oldNode = readArrayNode(oldJson)
        newNode = readArrayNode(newJson)
        expectedNode = readArrayNode(expectedJson)
    }

    @Unroll
    void "should update object"() {
        given:
        def out = new ByteArrayOutputStream()
        @Subject def generator = DEFAULT_MAPPER.getFactory().createGenerator(out)

        when:
        def modified = JsonUtils.partialUpdate().update(oldNode, newNode, generator)
        generator.close()

        then:
        modified == expectedModified
        readObjectNode(out.toString()) == expectedNode

        cleanup:
        out.close()

        where:
        oldJson                     | newJson                     || expectedModified | expectedJson
        null                        | null                        || false            | ''
        '{}'                        | null                        || false            | '{}'
        '{old:"old"}'               | null                        || false            | '{old:"old"}'

        null                        | '{}'                        || true             | '{}'
        '{}'                        | '{}'                        || false            | '{}'
        '{old:"old"}'               | '{}'                        || false            | '{old:"old"}'

        null                        | '{new:"new"}'               || true             | '{new:"new"}'
        '{}'                        | '{new:"new"}'               || true             | '{new:"new"}'
        '{old:"old"}'               | '{new:"new"}'               || true             | '{old:"old",new:"new"}'

        '{old:"old",updated:"old"}' | '{new:"new",updated:"new"}' || true             | '{old:"old",new:"new",updated:"new"}'

        oldNode = readObjectNode(oldJson)
        newNode = readObjectNode(newJson)
        expectedNode = readObjectNode(expectedJson)
    }

    void "should map fields to LinkedHashMap"() {
        expect:
        JsonUtils.mapFieldsToLinkedHashMap(node) == expectedNodeMap

        where:
        json            || expectedJsonMap
        '{}'            || [:]
        '{a: {b: "c"}}' || ['a': '{b: "c"}']

        node = readObjectNode(json)
        expectedNodeMap = expectedJsonMap.collectEntries { [it.key, readObjectNode(it.value)] }
    }
}
