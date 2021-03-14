package fr.adbonnin.cz2128.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;

public interface JsonRepository extends JsonProvider {

    <U> JsonRepository of(Class<U> type);

    <U> JsonRepository of(TypeReference<U> type);

    <U> JsonRepository of(ObjectReader reader);
}
