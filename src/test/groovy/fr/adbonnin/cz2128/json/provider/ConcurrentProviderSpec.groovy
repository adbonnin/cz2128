package fr.adbonnin.cz2128.json.provider

import fr.adbonnin.cz2128.fixture.BaseJsonSpec
import fr.adbonnin.cz2128.fixture.Cat
import spock.lang.Subject

class ConcurrentProviderSpec extends BaseJsonSpec {

    void "should use the wrapped json provider"() {
        given:
        @Subject def provider = newMemoryProviderFactory().concurrent(2000)
        def repository = provider.node().setRepository(Cat)

        when:
        repository.save(new Cat(id: id, name: name))

        then:
        isEquals(provider, """[{id: $id, name: "$name"}]""")

        when:
        def found = repository.findFirst { it.id == id }

        then:
        with(found.get()) {
            it.id == id
            it.name == name
        }

        where:
        id = 0
        name = 'Spock'
    }
}
