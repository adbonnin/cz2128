package fr.adbonnin.cz2128.json

import fr.adbonnin.cz2128.fixture.BaseJsonSpec
import spock.lang.Subject

class JsonUtilsSpec extends BaseJsonSpec {

    void "should update object"() {
        given:
        def out = new ByteArrayOutputStream()
        @Subject def generator = mapper.getFactory().createGenerator(out)

        when:
        def modified = JsonUtils.updateObject(oldNode, newNode, generator)
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

        null                        | '{}'                        || true             | '{}'
        '{}'                        | '{}'                        || false            | '{}'
        '{old:"old"}'               | '{}'                        || false            | '{old:"old"}'

        null                        | '{new:"new"}'               || true             | '{new:"new"}'
        '{}'                        | '{new:"new"}'               || true             | '{new:"new"}'

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
