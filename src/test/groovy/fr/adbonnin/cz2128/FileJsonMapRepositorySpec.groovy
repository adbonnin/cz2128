package fr.adbonnin.cz2128

class FileJsonMapRepositorySpec extends MemoryJsonMapRepositorySpec {

    @Override
    CZ2128.JsonProviderBuilder setupJsonProvider(String content) {
        return newFileJsonProvider(content)
    }
}
