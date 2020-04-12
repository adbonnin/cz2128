package fr.adbonnin.cz2128

import fr.adbonnin.cz2128.base.FileUtils
import fr.adbonnin.cz2128.json.provider.FileJsonProvider
import spock.lang.Subject

import java.nio.file.Files

class FileJsonSetRepositorySpec extends MemoryJsonSetRepositorySpec {

    @Subject
    def fileJsonProvider = new FileJsonProvider(Files.createTempFile('test-', '.json'))

    @Override
    JsonProvider setupJsonProvider(String content) {
        fileJsonProvider.content = content
        return fileJsonProvider
    }

    def cleanup() {
        FileUtils.deleteIfExistsQuietly(fileJsonProvider.file)
        FileUtils.deleteIfExistsQuietly(fileJsonProvider.tempFile)
    }

    @Override
    String getJsonProviderContent() {
        return fileJsonProvider.content
    }
}
