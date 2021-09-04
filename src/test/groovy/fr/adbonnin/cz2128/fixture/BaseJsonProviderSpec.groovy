package fr.adbonnin.cz2128.fixture

import fr.adbonnin.cz2128.collect.ListUtils
import fr.adbonnin.cz2128.json.Json

import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

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
                return ListUtils.newArrayList(iterator)
            }
        }
    }

    static interface RepositoryMapper extends Function<Json.Provider, Json.Repository> {}
}
