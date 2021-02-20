package fr.adbonnin.cz2128

class FileJsonSetRepositorySpec extends MemoryJsonSetRepositorySpec {

    @Override
    JsonProvider setupJsonProvider(String content) {
        return newFileJsonProvider(content)
    }
}
