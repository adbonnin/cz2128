package fr.adbonnin.cz2128

import fr.adbonnin.cz2128.json.provider.FileJsonProvider

import java.nio.file.Files

class FileJsonSetRepositorySpec extends MemoryJsonSetRepositorySpec {

    @Override
    JsonProvider setupJsonProvider(String content) {
        def tempFile = Files.createTempFile('test-', '.json')
        def jsonProvider = new FileJsonProvider(tempFile)
        jsonProvider.content = content
        return jsonProvider
    }
}
