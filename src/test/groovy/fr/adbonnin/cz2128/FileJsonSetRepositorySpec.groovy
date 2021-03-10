package fr.adbonnin.cz2128

class FileJsonSetRepositorySpec extends MemoryJsonSetRepositorySpec {

    @Override
    CZ2128.JsonProviderBuilder setupJsonProvider(String content) {
        return newFileJsonProvider(content)
    }
}
