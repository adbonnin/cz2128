package fr.adbonnin.cz2128.json.provider.wrapper

import fr.adbonnin.cz2128.JsonSetRepository
import fr.adbonnin.cz2128.fixture.BaseJsonSpec
import fr.adbonnin.cz2128.fixture.Cat

class ConcurrentJsonProviderWrapperSpec extends BaseJsonSpec {

    void "should use the wrapped json provider"() {
        given:
        def mapper = DEFAULT_MAPPER
        def updateStrategy = DEFAULT_UPDATE_STRATEGY

        and:
        def provider = newMemoryJsonProvider()
        def wrapper = new ConcurrentJsonProviderWrapper(provider, 2000)
        def repository = new JsonSetRepository<Cat>(Cat, mapper, wrapper, updateStrategy)

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
