package fr.adbonnin.cz2128.fixture

import fr.adbonnin.cz2128.json.Json

import java.util.function.Function

abstract class BaseJsonProviderSpec extends BaseJsonSpec {

    abstract Json.Provider setupJsonProvider(String content)

    static RepositoryMapper node = new RepositoryMapper() {
        @Override
        Json.Repository apply(Json.Provider provider) {
            return provider.node()
        }
    }

    static RepositoryMapper value = new RepositoryMapper() {
        @Override
        Json.Repository apply(Json.Provider provider) {
            return provider.value()
        }
    }

    static interface RepositoryMapper extends Function<Json.Provider, Json.Repository> {}
}
