package fr.adbonnin.cz2128.json.repository

import fr.adbonnin.cz2128.json.Json

class FileElementRepositorySpec extends MemoryMapRepositorySpec {

    @Override
    Json.ProviderFactory setupProviderFactory(String content) {
        return newFileProviderFactory(content)
    }
}
