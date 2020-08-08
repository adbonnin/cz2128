package fr.adbonnin.cz2128.json.provider.wrapper

import fr.adbonnin.cz2128.JsonSetRepository
import fr.adbonnin.cz2128.fixture.BaseJsonSpec
import fr.adbonnin.cz2128.fixture.Cat
import fr.adbonnin.cz2128.json.provider.MemoryJsonProvider

class ConcurrentJsonProviderWrapperSpec extends BaseJsonSpec {

    void "should use the wrapped json provider"() {
        given:
        def provider = new MemoryJsonProvider()
        def wrapper = new ConcurrentJsonProviderWrapper(provider, 2000)
        def repository = new JsonSetRepository<Cat>(Cat, wrapper, DEFAULT_MAPPER, DEFAULT_UPDATE_STRATEGY)

        when:
        repository.save(new Cat(id: id, name: 'Spock'))

        then:
        provider.content == "[{id:$id,name:\"$name\"}]"

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
