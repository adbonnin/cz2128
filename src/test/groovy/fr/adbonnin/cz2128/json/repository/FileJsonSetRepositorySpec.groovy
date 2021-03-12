package fr.adbonnin.cz2128.json.repository

import fr.adbonnin.cz2128.CZ2128

class FileJsonSetRepositorySpec extends MemoryJsonSetRepositorySpec {

    @Override
    CZ2128.JsonProviderBuilder setupJsonProvider(String content) {
        return newFileJsonProvider(content)
    }
}
