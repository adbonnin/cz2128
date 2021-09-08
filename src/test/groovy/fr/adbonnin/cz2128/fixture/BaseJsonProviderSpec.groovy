package fr.adbonnin.cz2128.fixture

import fr.adbonnin.cz2128.collect.IteratorUtils
import fr.adbonnin.cz2128.json.Json

import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

abstract class BaseJsonProviderSpec extends BaseJsonSpec {

    abstract Json.ProviderFactory setupProviderFactory(String content)

    static RepositoryFactory node = new RepositoryFactory() {
        @Override
        Json.RepositoryFactory apply(Json.ProviderFactory provider) {
            return provider.node()
        }
    }

    static RepositoryFactory value = new RepositoryFactory() {
        @Override
        Json.RepositoryFactory apply(Json.ProviderFactory provider) {
            return provider.value()
        }
    }

    static <T> Function<Stream<? extends T>, List<T>> streamToList() {
        return new Function<Stream<? extends T>, List<T>>() {
            @Override
            List<T> apply(Stream<? extends T> stream) {
                return stream.collect(Collectors.toList())
            }
        }
    }

    static <T> Function<Iterator<? extends T>, List<T>> iteratorToList() {
        return new Function<Iterator<? extends T>, List<T>>() {
            @Override
            List<T> apply(Iterator<? extends T> iterator) {
                return IteratorUtils.newArrayList(iterator)
            }
        }
    }

    static interface RepositoryFactory extends Function<Json.ProviderFactory, Json.RepositoryFactory> {}
}
