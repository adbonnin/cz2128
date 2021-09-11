package fr.adbonnin.cz2128.json.provider;

import fr.adbonnin.cz2128.json.JsonProvider;

import java.io.IOException;

public interface ContentProvider extends JsonProvider {

    String getContent() throws IOException;

    void setContent(String content) throws IOException;
}
