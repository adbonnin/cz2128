package fr.adbonnin.cz2128

class FileJsonMapRepositorySpec extends MemoryJsonMapRepositorySpec {

    @Override
    JsonProvider setupJsonProvider(String content) {
        return newFileJsonProvider(content)
    }
}
