package fr.adbonnin.cz2128.json.provider

import fr.adbonnin.cz2128.fixture.BaseJsonSpec
import fr.adbonnin.cz2128.fixture.Cat
import spock.lang.Subject

class ObjectFieldProviderSpec extends BaseJsonSpec {

    void "should read and write values in an object field"() {

        def enterprise = [
            new Cat(id: 0, name: 'Spock'),
            new Cat(id: 1, name: 'Kirk')
        ]

        def discovery = [
            new Cat(id: 2, name: 'Archer')
        ]

        given:
        def provider = newMemoryProviderFactory('{init: {enterprise: "skipChildren"}}')
        @Subject def enterpriseRepository = provider.at("enterprise").node().setRepository(Cat)
        @Subject def discoveryRepository = provider.at("discovery").node().setRepository(Cat)

        expect:
        enterpriseRepository.isEmpty()
        discoveryRepository.isEmpty()

        when:
        enterpriseRepository.saveAll(enterprise)

        then:
        isEquals(provider, '{init: {enterprise: "skipChildren"}, enterprise: [{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}]}')
        isEquals(enterpriseRepository, '[{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}]')

        when:
        discoveryRepository.saveAll(discovery)

        then:
        isEquals(provider, '{init: {enterprise: "skipChildren"}, enterprise: [{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}], discovery:[{id: 2, name: "Archer"}]}')
        isEquals(enterpriseRepository, '[{id: 0, name: "Spock"}, {id: 1, name: "Kirk"}]')
        isEquals(discoveryRepository, '[{id: 2, name: "Archer"}]')

        when:
        enterpriseRepository.save(new Cat(id: 1, name: "McCoy"))

        then:
        isEquals(provider, '{init: {enterprise: "skipChildren"}, enterprise: [{id: 0, name: "Spock"}, {id: 1, name: "McCoy"}], discovery:[{id: 2, name: "Archer"}]}')
        isEquals(enterpriseRepository, '[{id: 0, name: "Spock"}, {id: 1, name: "McCoy"}]')
        isEquals(discoveryRepository, '[{id: 2, name: "Archer"}]')
    }
}
