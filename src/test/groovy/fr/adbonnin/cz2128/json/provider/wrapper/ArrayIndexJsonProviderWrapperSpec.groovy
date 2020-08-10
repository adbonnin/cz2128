package fr.adbonnin.cz2128.json.provider.wrapper

import fr.adbonnin.cz2128.JsonSetRepository
import fr.adbonnin.cz2128.fixture.BaseJsonSpec
import fr.adbonnin.cz2128.fixture.Cat
import fr.adbonnin.cz2128.json.provider.MemoryJsonProvider

class ArrayIndexJsonProviderWrapperSpec extends BaseJsonSpec {

    void "should read and write values in an array index"() {

        def enterprise = [
            new Cat(id: 0, name: 'Spock'),
            new Cat(id: 1, name: 'Kirk')
        ]

        def discovery = [
            new Cat(id: 2, name: 'Archer')
        ]

        given:
        def mapper = DEFAULT_MAPPER
        def updateStrategy = DEFAULT_UPDATE_STRATEGY

        and:
        def provider = new MemoryJsonProvider()
        def enterpriseRepository = new JsonSetRepository(Cat, provider.at(1), mapper, updateStrategy)
        def discoveryRepository = new JsonSetRepository(Cat, provider.at(2), mapper, updateStrategy)

        when:
        enterpriseRepository.saveAll(enterprise)

        then:
        isEquals(provider, '[null, [{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}]]')
        isEquals(enterpriseRepository, '[{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}]')

        when:
        discoveryRepository.saveAll(discovery)

        then:
        isEquals(provider, '[null, [{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}], [{id: 2, name: "Archer"}]]')
        isEquals(enterpriseRepository, '[{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}]')
        isEquals(discoveryRepository, '[{id: 2, name: "Archer"}]')

        when:
        enterpriseRepository.save(new Cat(id: 1, name: "McCoy"))

        then:
        isEquals(provider, '[null, [{id: 0, name: "Spock"}, {id: 1, name: "McCoy"}], [{id: 2, name: "Archer"}]]')
        isEquals(enterpriseRepository, '[{id: 0, name: "Spock"}, {id: 1, name: "McCoy"}]')
        isEquals(discoveryRepository, '[{id: 2, name: "Archer"}]')
    }
}
