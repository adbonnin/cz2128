package fr.adbonnin.cz2128.json.repository;

import fr.adbonnin.cz2128.json.JsonRepository;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface MapRepository<T> extends JsonRepository<T> {

    boolean isEmpty();

    long count();

    long count(Predicate<? super Map.Entry<String, T>> predicate);

    Optional<Map.Entry<String, T>> findFirst(Predicate<? super Map.Entry<String, T>> predicate);

    Map<String, T> findAll();

    Map<String, T> findAll(Predicate<? super Map.Entry<String, T>> predicate);

    <R> R withFieldIterator(Function<Iterator<? extends String>, R> function);

    <R> R withEntryIterator(Function<Iterator<? extends Map.Entry<String, T>>, R> function);

    <R> R withEntryStream(Function<Stream<? extends Map.Entry<String, T>>, R> function);

    boolean save(String key, T element);

    long saveAll(Map<String, ? extends T> elements);

    boolean delete(String key);

    long deleteAll();

    long deleteAll(Collection<String> elements);

    long deleteAll(Predicate<? super Map.Entry<String, T>> predicate);
}
