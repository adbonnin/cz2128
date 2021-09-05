package fr.adbonnin.cz2128.json

import fr.adbonnin.cz2128.fixture.BaseJsonSpec
import spock.lang.Subject
import spock.lang.Unroll

class JsonUtilsSpec extends BaseJsonSpec {

    @Unroll
    void "should update"() {
        given:
        def out = new ByteArrayOutputStream()
        @Subject def generator = DEFAULT_MAPPER.getFactory().createGenerator(out)

        when:
        def modified = JsonUtils.partialUpdate(oldNode, newNode, generator)
        generator.close()

        then:
        modified == expectedModified
        readNode(out.toString()) == expectedNode

        cleanup:
        out.close()

        where:
        oldJson | newJson || expectedModified | expectedJson
        null    | null    || false            | ''
        '"old"' | null    || false            | '"old"'
        '0'     | null    || false            | '0'
        'false' | null    || false            | 'false'

        null    | '"new"' || true             | '"new"'
        '"old"' | '"new"' || true             | '"new"'
        '0'     | '1'     || true             | '1'
        'false' | 'true'  || true             | 'true'

        oldNode = readNode(oldJson)
        newNode = readNode(newJson)
        expectedNode = readNode(expectedJson)
    }

    @Unroll
    void "should update array"() {
        given:
        def out = new ByteArrayOutputStream()
        @Subject def generator = DEFAULT_MAPPER.getFactory().createGenerator(out)

        when:
        def modified = JsonUtils.partialUpdateArray(oldNode, newNode, generator)
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
        def modified = JsonUtils.partialUpdateObject(oldNode, newNode, generator)
        generator.close()

        then:
        modified == expectedModified
        readObjectNode(out.toString()) == expectedNode

        cleanup:
        out.close()

        where:
        oldJson                        | newJson                        || expectedModified | expectedJson
        null                           | null                           || false            | ''
        '{}'                           | null                           || false            | '{}'
        '{old: "old"}'                 | null                           || false            | '{old: "old"}'

        null                           | '{}'                           || true             | '{}'
        '{}'                           | '{}'                           || false            | '{}'
        '{old: "old"}'                 | '{}'                           || false            | '{old: "old"}'

        null                           | '{new: "new"}'                 || true             | '{new: "new"}'
        '{}'                           | '{new: "new"}'                 || true             | '{new: "new"}'
        '{old: "old"}'                 | '{new: "new"}'                 || true             | '{old: "old", new: "new"}'

        '{old: "old", updated: "old"}' | '{new: "new", updated: "new"}' || true             | '{old: "old", new: "new", updated: "new"}'

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

    void "should write json node"() {
        def node = readObjectNode(json)

        given:
        def provider = newMemoryProviderFactory()
        def providerAtIndex = newMemoryProviderFactory()
        def providerAtField = newMemoryProviderFactory()

        when:
        JsonUtils.writeNode(node, provider)
        JsonUtils.writeNode(node, providerAtIndex.at(1))
        JsonUtils.writeNode(node, providerAtField.at("test"))

        then:
        isEquals(provider, json)
        isEquals(providerAtIndex, expectedJsonAtIndex)
        isEquals(providerAtField, expectedJsonAtField)

        where:
        json              || expectedJsonAtIndex       | expectedJsonAtField
        '{name: "value"}' || '[null, {name: "value"}]' | '{test: {name: "value"}}'
        null              || '[null, null]'            | '{test: null}'
    }
}
